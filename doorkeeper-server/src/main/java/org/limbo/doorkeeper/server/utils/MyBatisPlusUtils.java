/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.doorkeeper.server.utils;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


/**
 * @author Devil
 * @date 2020/11/18 3:31 PM
 */
public class MyBatisPlusUtils {

    private static final int BATCH_SIZE = 512;

    private static Map<Class, String> poTableNameCache = new ConcurrentHashMap<>(16);

    /**
     * 如果前端传来total表示不需要进行总数统计
     * 将自己的Page对象转换为MyBatisPlus可用的Page对象
     * 因为自己的Page对象用于在dubbo调用时传参，进行序列化方便；
     * 而MyBatisPlus的Page中存在大量属性，而且很多属性没有Getter和Setter，不利于序列化，因此不用；
     */
    public static <PO, VO> IPage<PO> pageOf(org.limbo.doorkeeper.api.model.Page<VO> ipage) {
        Page<PO> page = new Page<>();
        page.setCurrent(ipage.getCurrent());
        page.setSize(ipage.getSize());
        page.setSearchCount(ipage.getTotal() < 0);
        page.setTotal(page.isSearchCount() ? 0 : ipage.getTotal());

        // todo 排序
//        List<String> orderByList = ipage.getOrderBy();
//        List<String> sortList = ipage.getSort();
//        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderByList)
//                && org.apache.commons.collections4.CollectionUtils.isNotEmpty(sortList)
//                && orderByList.size() == sortList.size()) {
//            for (int i = 0; i < orderByList.size(); i++) {
//                String orderBy = orderByList.get(i);
//                String sort = sortList.get(i);
//                page.addOrder(Sorts.ASC.is(sort)
//                        ? OrderItem.asc(orderBy)
//                        : OrderItem.desc(orderBy));
//            }
//        }
        return page;
    }

    /**
     * 根据PO类注解解析PO类的表名
     */
    public static String parseTableName(Class poClass) {
        if (poTableNameCache.containsKey(poClass)) {
            return poTableNameCache.get(poClass);
        }

        return poTableNameCache.computeIfAbsent(poClass, c -> {
            TableName[] tableNames = (TableName[]) poClass.getAnnotationsByType(TableName.class);
            if (tableNames.length <= 0) {
                throw new RuntimeException("此对象无" + TableName.class.getName());
            }

            return tableNames[0].value();
        });
    }

    /**
     * 此方法内部不保证事务，需要在外部调用的时候，保证在事务内调用
     */
    public static <S extends T, T> void batchSave(List<S> entities, Class<T> modelClass) {
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE, modelClass);
        try (SqlSession batchSqlSession = sqlSessionBatch(modelClass)) {
            int i = 0;
            for (T entity : entities) {
                batchSqlSession.insert(sqlStatement, entity);
                if (i >= 1 && i % BATCH_SIZE == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
    }

    public static <T> int batchUpdate(Collection<Wrapper<T>> wrappers, Class<T> poClass) {
        if (CollectionUtils.isEmpty(wrappers)) {
            return 0;
        }

        AtomicInteger updateCount = new AtomicInteger(0);
        String sqlStatement = sqlStatement(SqlMethod.UPDATE, poClass);
        try (SqlSession batchSqlSession = sqlSessionBatch(poClass)) {
            int i = 0;
            for (Wrapper<T> wrapper : wrappers) {
                Map<String, Object> map = new HashMap<>(2);
                // map.put(Constants.ENTITY, this);
                map.put(Constants.WRAPPER, wrapper);
                batchSqlSession.update(sqlStatement, map);
                if (i >= 1 && i % BATCH_SIZE == 0) {
                    updateCount.addAndGet(sumUpdateCount(batchSqlSession.flushStatements()));
                }
                i++;
            }
            updateCount.addAndGet(sumUpdateCount(batchSqlSession.flushStatements()));
        }

        return updateCount.get();
    }

    public static  <S extends T, T> int batchUpdateById(Collection<S> entityList, Class<T> modelClass) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");

        AtomicInteger updateCount = new AtomicInteger(0);
        String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID, modelClass);
        try (SqlSession batchSqlSession = sqlSessionBatch(modelClass)) {
            int i = 0;
            for (T anEntityList : entityList) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, anEntityList);
                batchSqlSession.update(sqlStatement, param);
                if (i >= 1 && i % BATCH_SIZE == 0) {
                    updateCount.addAndGet(sumUpdateCount(batchSqlSession.flushStatements()));
                }
                i++;
            }
            updateCount.addAndGet(sumUpdateCount(batchSqlSession.flushStatements()));
        }
        return updateCount.get();
    }


    private static String sqlStatement(SqlMethod sqlMethod, Class<?> modelClass) {
        return SqlHelper.table(modelClass).getSqlStatement(sqlMethod.getMethod());
    }

    private static SqlSession sqlSessionBatch(Class<?> modelClass) {
        return SqlHelper.sqlSessionBatch(modelClass);
    }

    private static int sumUpdateCount(List<BatchResult> results) {
        if (CollectionUtils.isNotEmpty(results)) {
            return results.stream().flatMapToInt(r -> IntStream.of(r.getUpdateCounts())).sum();
        }
        return 0;
    }

}
