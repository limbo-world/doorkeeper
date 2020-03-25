/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
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

package org.limbo.authc.api.interfaces.apis;

import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.po.AccountPO;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.api.interfaces.beans.vo.ProjectVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/6 8:05 AM
 * @email brozen@qq.com
 */
public interface AccountApi {

    /**
     * 更新账户信息，不包括密码信息
     */
    Response<AccountVO> update(AccountVO.UpdateParam param);

    /**
     * 更新账户密码
     */
    Response<AccountVO> updatePassword(AccountVO.UpdatePasswordParam param);

    /**
     * 删除账户
     */
    Response<AccountVO> delete(AccountVO.DeleteParam param);

    /**
     * 账户删除回滚
     */
    Response<Boolean> cancelDelete(AccountVO.DeleteParam param);

    /**
     * 列出项目下全部账户
     */
    Response<List<AccountVO>> list(Param param);

    /**
     * 查询账户
     */
    Response<Page<AccountVO>> query(AccountVO.QueryParam param);

    /**
     * 查询单个账户
     */
    Response<AccountVO> get(AccountVO.GetParam param);

}
