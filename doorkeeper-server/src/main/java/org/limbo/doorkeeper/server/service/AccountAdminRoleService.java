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

import org.limbo.doorkeeper.api.model.param.AccountAdminRoleAddParam;
import org.limbo.doorkeeper.api.model.param.AccountAdminRoleQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountAdminRoleVO;

import java.util.List;

/**
 * @Author Devil
 * @Date 2020/12/7 10:05 上午
 */
public interface AccountAdminRoleService {
    /**
     * 查询列表
     */
    List<AccountAdminRoleVO> list(Long projectId, AccountAdminRoleQueryParam param);
    /**
     * 批量添加
     */
    void batchSave(Long projectId, List<AccountAdminRoleAddParam> params);

    /**
     * 批量删除
     */
    int batchDelete(Long projectId, List<Long> accountAdminRoleIds);
}
