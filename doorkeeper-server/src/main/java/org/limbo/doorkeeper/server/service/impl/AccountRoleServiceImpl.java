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
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.AccountRoleAddParam;
import org.limbo.doorkeeper.api.model.param.AccountRoleQueryParam;
import org.limbo.doorkeeper.api.model.param.RolePermissionAddParam;
import org.limbo.doorkeeper.api.model.vo.AccountRoleVO;
import org.limbo.doorkeeper.api.model.vo.RolePermissionVO;
import org.limbo.doorkeeper.server.dao.AccountRoleMapper;
import org.limbo.doorkeeper.server.entity.AccountRole;
import org.limbo.doorkeeper.server.entity.RolePermission;
import org.limbo.doorkeeper.server.service.AccountRoleService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 11:31 AM
 */
@Service
public class AccountRoleServiceImpl implements AccountRoleService {

    @Autowired
    private AccountRoleMapper accountRoleMapper;


    @Override
    public List<AccountRoleVO> list(Long projectId, AccountRoleQueryParam param) {
        List<AccountRole> rolePermissions = accountRoleMapper.selectList(Wrappers.<AccountRole>lambdaQuery()
                .eq(AccountRole::getProjectId, projectId)
                .eq(param.getAccountId() != null, AccountRole::getAccountId, param.getAccountId())
        );
        return EnhancedBeanUtils.createAndCopyList(rolePermissions, AccountRoleVO.class);
    }

    @Override
    public void batchSave(Long projectId, List<AccountRoleAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<AccountRole> accountRoles = EnhancedBeanUtils.createAndCopyList(params, AccountRole.class);
        for (AccountRole accountRole : accountRoles) {
            accountRole.setProjectId(projectId);
        }
        accountRoleMapper.batchInsertIgnore(accountRoles);
    }

    @Override
    public int batchDelete(Long projectId, List<Long> accountRoleIds) {
        return accountRoleMapper.delete(Wrappers.<AccountRole>lambdaQuery()
                .eq(AccountRole::getProjectId, projectId)
                .in(AccountRole::getAccountRoleId, accountRoleIds)
        );
    }
}
