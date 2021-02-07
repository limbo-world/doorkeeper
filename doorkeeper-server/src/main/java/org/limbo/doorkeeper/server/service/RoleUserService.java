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
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.role.RoleUserBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.role.RoleUserQueryParam;
import org.limbo.doorkeeper.api.model.vo.RoleUserVO;
import org.limbo.doorkeeper.server.dal.mapper.RealmMapper;
import org.limbo.doorkeeper.server.dal.mapper.UserRoleMapper;
import org.limbo.doorkeeper.server.dal.entity.UserRole;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/18 10:15 上午
 */
@Service
public class RoleUserService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RealmMapper realmMapper;

    public Page<RoleUserVO> page(Long realmId, Long roleId, RoleUserQueryParam param) {
        param.setRealmId(realmId);
        param.setRoleId(roleId);
        long count = userRoleMapper.listRoleUserCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(userRoleMapper.listRoleUserVOS(param));
        }
        return param;
    }

    @Transactional
    public void batchUpdate(Long roleId, RoleUserBatchUpdateParam param) {
        switch (param.getType()) {
            case SAVE: // 新增
                List<UserRole> userRoles = new ArrayList<>();
                for (Long userId : param.getUserIds()) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRoles.add(userRole);
                }
                MyBatisPlusUtils.batchSave(userRoles, UserRole.class);
                break;
            case DELETE: // 删除
                userRoleMapper.delete(Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getRoleId, roleId)
                        .in(UserRole::getUserId, param.getUserIds())
                );
                break;
            default:
                break;
        }
    }

}
