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
import org.limbo.doorkeeper.api.model.param.batch.UserRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.UserRoleVO;
import org.limbo.doorkeeper.server.infrastructure.po.UserRolePO;
import org.limbo.doorkeeper.server.infrastructure.mapper.*;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/12 3:31 下午
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public List<UserRoleVO> list(Long realmId, Long userId) {
        List<UserRolePO> userRoles = userRoleMapper.selectList(Wrappers.<UserRolePO>lambdaQuery()
                .eq(UserRolePO::getUserId, userId)
        );
        return EnhancedBeanUtils.createAndCopyList(userRoles, UserRoleVO.class);
    }

    @Transactional
    public void batchUpdate(Long userId, UserRoleBatchUpdateParam param) {
        switch (param.getType()) {
            case SAVE: // 新增
                List<UserRolePO> userRoles = new ArrayList<>();
                for (Long roleId : param.getRoleIds()) {
                    UserRolePO userRole = new UserRolePO();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRoles.add(userRole);
                }
                MyBatisPlusUtils.batchSave(userRoles, UserRolePO.class);
                break;
            case DELETE: // 删除
                userRoleMapper.delete(Wrappers.<UserRolePO>lambdaQuery()
                        .eq(UserRolePO::getUserId, userId)
                        .in(UserRolePO::getRoleId, param.getRoleIds())
                );
                break;
            default:
                break;
        }
    }

}
