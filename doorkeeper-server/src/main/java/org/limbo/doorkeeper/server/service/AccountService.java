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

import com.baomidou.mybatisplus.extension.service.IService;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.po.AccountPO;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/2/27 5:41 PM
 * @email brozen@qq.com
 */
public interface AccountService extends IService<AccountPO> {

    void updateAccount(AccountVO.UpdateParam param);

    void updatePassword(AccountVO.UpdatePasswordParam param);

    void deleteAccount(AccountVO.DeleteParam param);

    void unDeleteAccount(AccountVO.DeleteParam param);

    List<AccountVO> listAccounts(Long projectId);

    Page<AccountVO> queryAccount(AccountVO.QueryParam param);

    AccountVO get(AccountVO.GetParam param);

}
