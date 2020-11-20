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

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.AccountRoleAddParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.dao.AccountRoleMapper;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.service.AccountService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 10:58 AM
 * @email brozen@qq.com
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AccountRoleMapper accountRoleMapper;

    @Override
    @Transactional
    public AccountVO addAccount(AccountAddParam param) {
        Account po = EnhancedBeanUtils.createAndCopy(param, Account.class);

        // 判断用户名是否已存在
        Verifies.verify(accountMapper.countByUsername(param.getProjectId(), param.getUsername()) == 0, "用户名已存在");

        try {
            accountMapper.insert(po);
        } catch (DuplicateKeyException e) {
            throw new ParamException("账户已存在");
        }

        // 找到项目中需要默认添加的角色
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getProjectId, param.getProjectId())
                .eq(Role::getIsDefault, true)
                .eq(Role::getIsDeleted, false)
        );

        if (CollectionUtils.isNotEmpty(roles)) {
            List<AccountRoleAddParam> accountRoles = new ArrayList<>();
            for (Role role : roles) {
                AccountRoleAddParam accountRole = new AccountRoleAddParam();
                accountRole.setAccountId(po.getAccountId());
                accountRole.setRoleId(role.getRoleId());
                accountRoles.add(accountRole);
            }
            accountRoleMapper.batchInsertOrUpdate(accountRoles);
        }

        return EnhancedBeanUtils.createAndCopy(po, AccountVO.class);
    }
}
