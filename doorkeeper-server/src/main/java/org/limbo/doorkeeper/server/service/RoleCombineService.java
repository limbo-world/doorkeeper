/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.api.model.param.batch.RoleCombineBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleCombineVO;
import org.limbo.doorkeeper.server.infrastructure.po.RoleCombinePO;
import org.limbo.doorkeeper.server.infrastructure.mapper.RoleCombineMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/5 1:58 下午
 */
@Service
public class RoleCombineService {

    @Autowired
    private RoleCombineMapper roleCombineMapper;

    public List<RoleCombineVO> list(Long realmId, Long parentId) {
        List<RoleCombinePO> roleCombines = roleCombineMapper.selectList(Wrappers.<RoleCombinePO>lambdaQuery()
                .eq(RoleCombinePO::getParentId, parentId)
        );
        return EnhancedBeanUtils.createAndCopyList(roleCombines, RoleCombineVO.class);
    }

    @Transactional
    public void batchUpdate(Long realmId, Long parentId, RoleCombineBatchUpdateParam param) {
        switch (param.getType()) {
            case SAVE: // 新增
                List<RoleCombinePO> roleCombines = new ArrayList<>();
                for (Long roleId : param.getRoleIds()) {
                    RoleCombinePO roleCombine = new RoleCombinePO();
                    roleCombine.setParentId(parentId);
                    roleCombine.setRoleId(roleId);
                    roleCombines.add(roleCombine);
                }
                MyBatisPlusUtils.batchSave(roleCombines, RoleCombinePO.class);
                break;
            case DELETE: // 删除
                roleCombineMapper.delete(Wrappers.<RoleCombinePO>lambdaQuery()
                        .eq(RoleCombinePO::getParentId, parentId)
                        .in(RoleCombinePO::getRoleId, param.getRoleIds())
                );
                break;
            default:
                break;
        }
    }

}
