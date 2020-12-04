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

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.AccountQueryParam;
import org.limbo.doorkeeper.api.model.param.AccountUpdateParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/2/27 5:41 PM
 * @email brozen@qq.com
 */
public interface AccountService {

    /**
     * 添加用户
     */
    AccountVO addAccount(Long currentProjectId, Long currentAccountId, AccountAddParam param);

    /**
     * 更新账户
     */
    Integer update(Long currentProjectId, Long currentAccountId, AccountUpdateParam param);

    /**
     * 分页查询
     */
    Page<AccountVO> queryPage(Long projectId, AccountQueryParam param);


    List<AccountVO> list(Long projectId);

    AccountVO get(Long accountId);

}
