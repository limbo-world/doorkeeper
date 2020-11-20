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
import org.limbo.doorkeeper.api.model.param.AccountRoleAddParam;
import org.limbo.doorkeeper.server.dao.AccountRoleMapper;
import org.limbo.doorkeeper.server.entity.AccountRole;
import org.limbo.doorkeeper.server.service.AccountRoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 11:31 AM
 */
@Data
public class AccountRoleServiceImpl implements AccountRoleService {

    @Autowired
    private AccountRoleMapper accountRoleMapper;

    @Override
    public void addAccountRole(List<AccountRoleAddParam> params) {
        accountRoleMapper.batchInsertOrUpdate(params);
    }

    @Override
    public void deleteAccountRole(List<Long> accountRoleIds) {
        accountRoleMapper.update(null, Wrappers.<AccountRole>lambdaUpdate()
                .set(AccountRole::getIsDeleted, true)
                .in(AccountRole::getAccountRoleId, accountRoleIds)
        );
    }
}
