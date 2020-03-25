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

package org.limbo.authc.admin.dubbo.consumers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.limbo.authc.admin.beans.po.AdminAccountProjectPO;
import org.limbo.authc.admin.service.AdminAccountProjectService;
import org.limbo.authc.admin.service.AdminAccountService;
import org.limbo.authc.api.interfaces.apis.AccountApi;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author devil
 * @date 2020/3/10
 */
@Slf4j
@Component
public class AccountDubboConsumer {

    @Delegate
    @Reference(version = "1.0.0", filter = {"consumerAuthcFilter"})
    private AccountApi accountApi;

    @Autowired
    private AdminAccountService adminAccountService;
    @Autowired
    private AdminAccountProjectService adminAccountProjectService;

    @Transactional
    public Response<AccountVO> deleteAccount(AccountVO.DeleteParam param) {
        // 删除本地数据
        adminAccountService.removeById(param.getAccountId());
        adminAccountProjectService.remove(Wrappers.<AdminAccountProjectPO>lambdaQuery()
                .eq(AdminAccountProjectPO::getAccountId, param.getAccountId()));

        Response<AccountVO> response = accountApi.delete(param);
        Verifies.verify(response.ok(), "账户删除失败");
        return response;
    }

}
