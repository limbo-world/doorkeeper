/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.ApiAddParam;
import org.limbo.doorkeeper.api.model.param.ApiQueryParam;
import org.limbo.doorkeeper.api.model.param.ApiUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ApiVO;
import org.limbo.doorkeeper.server.dao.ApiMapper;
import org.limbo.doorkeeper.server.dao.PermissionApiMapper;
import org.limbo.doorkeeper.server.entity.Api;
import org.limbo.doorkeeper.server.entity.PermissionApi;
import org.limbo.doorkeeper.server.service.ApiService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/19 7:17 PM
 */
@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private PermissionApiMapper permissionApiMapper;

    @Override
    @Transactional
    public ApiVO addApi(Long projectId, ApiAddParam param) {
        Api api = EnhancedBeanUtils.createAndCopy(param, Api.class);
        api.setProjectId(projectId);
        apiMapper.insert(api);
        return EnhancedBeanUtils.createAndCopy(api, ApiVO.class);
    }

    @Override
    @Transactional
    public int updateApi(Long projectId, ApiUpdateParam param) {
        return apiMapper.update(null, Wrappers.<Api>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getApiDescribe()), Api::getApiDescribe, param.getApiDescribe())
                .set(StringUtils.isNotBlank(param.getApiName()), Api::getApiName, param.getApiName())
                .eq(Api::getApiId, param.getApiId())
                .eq(Api::getProjectId, projectId)
        );
    }

    @Override
    @Transactional
    public void deleteApi(Long projectId, List<Long> apiIds) {
        // 删除api
        apiMapper.delete(Wrappers.<Api>lambdaQuery()
                .in(Api::getApiId, apiIds)
                .eq(Api::getProjectId, projectId)
        );

        // 删除绑定
        permissionApiMapper.delete(Wrappers.<PermissionApi>lambdaQuery().in(PermissionApi::getApiId, apiIds));
    }

    @Override
    public List<ApiVO> all(Long projectId) {
        List<Api> apis = apiMapper.selectList(Wrappers.<Api>lambdaQuery()
                .eq(Api::getProjectId, projectId)
        );
        return EnhancedBeanUtils.createAndCopyList(apis, ApiVO.class);
    }

    @Override
    public Page<ApiVO> queryPage(Long projectId, ApiQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Api> mpage = MyBatisPlusUtils.pageOf(param);
        mpage = apiMapper.selectPage(mpage, Wrappers.<Api>lambdaQuery()
                .eq(Api::getProjectId, projectId)
                .eq(StringUtils.isNotBlank(param.getApiMethod()), Api::getApiMethod, param.getApiMethod())
                .like(StringUtils.isNotBlank(param.getApiName()), Api::getApiName, param.getApiName())
                .like(StringUtils.isNotBlank(param.getApiUrl()), Api::getApiUrl, param.getApiUrl())
        );

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), ApiVO.class));
        return param;
    }
}
