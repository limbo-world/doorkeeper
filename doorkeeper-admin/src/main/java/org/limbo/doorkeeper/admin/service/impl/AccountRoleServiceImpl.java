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

package org.limbo.doorkeeper.admin.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.admin.model.param.AccountRoleUpdateParam;
import org.limbo.doorkeeper.admin.service.AccountRoleService;
import org.limbo.doorkeeper.api.client.AccountRoleClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2020/11/27 10:52 AM
 */
@Service
public class AccountRoleServiceImpl implements AccountRoleService {

    @Autowired
    private AccountRoleClient accountRoleClient;

    // todo 失败处理
    @Override
    public void update(AccountRoleUpdateParam param) {
        // 删除
        if (CollectionUtils.isNotEmpty(param.getDeleteAccountRoleIds())) {
            accountRoleClient.batchDelete(param.getDeleteAccountRoleIds());
        }

        //新增
        if (CollectionUtils.isNotEmpty(param.getAddAccountRoles())) {
            accountRoleClient.batchSave(param.getAddAccountRoles());
        }
    }
}
