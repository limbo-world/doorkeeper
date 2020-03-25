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

import org.limbo.authc.admin.beans.po.AdminAccountPO;
import org.limbo.authc.admin.beans.po.AdminProjectPO;
import org.limbo.authc.admin.dao.AdminAccountProjectMapper;
import org.limbo.authc.admin.dao.AdminProjectMapper;
import org.limbo.authc.admin.service.AdminAccountService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.limbo.authc.api.interfaces.apis.AuthenticationApi;
import org.limbo.authc.api.interfaces.apis.AuthorizationApi;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.beans.vo.PermissionPolicyVO;
import org.limbo.authc.api.interfaces.constants.PermissionAuthcPolicies;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.session.SessionAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author devil
 * @date 2020/3/11
 */
@Slf4j
@Component
public class AuthenticationDubboConsumer {

    @Delegate
    @Reference(version = "1.0.0", filter = {"consumerAuthcFilter"})
    private AuthenticationApi authenticationApi;

    @Reference(version = "1.0.0", filter = {"consumerAuthcFilter"})
    private AuthorizationApi authorizationApi;

    @Autowired
    private AdminProjectMapper adminProjectMapper;

    @Autowired
    private AdminAccountProjectMapper adminAccountProjectMapper;

    @Autowired
    private AdminAccountService adminAccountService;

    public Response<SessionAccount> login(AccountVO.LoginParam param) {
        Response<AccountVO> authcResponse = authenticationApi.authenticate(param);

        if (authcResponse.ok()) {
            // 账户信息
            AccountVO account = authcResponse.getData();
            SessionAccount sessionAccount = new SessionAccount();
            sessionAccount.setAccountId(account.getAccountId());
            sessionAccount.setAccountNick(account.getNick());
            sessionAccount.setAccountProjectId(account.getProjectId());
            sessionAccount.setIsSuperAdmin(account.getIsSuperAdmin());

            // 选中一个项目
            List<AdminProjectPO> projects = null;
            if (account.getIsSuperAdmin()) { // 管理端超级管理员是没有 account_project数据的 只需要把所有未删除项目筛选出来就行
                projects = adminProjectMapper.selectList(Wrappers.emptyWrapper());
            } else {
                projects = adminAccountProjectMapper.getByAccount(account.getAccountId());
            }
            if (CollectionUtils.isNotEmpty(projects)) {
                sessionAccount.setCurrentProjectId(projects.get(0).getProjectId());
                sessionAccount.setCurrentProjectName(projects.get(0).getProjectName());
            }

            return Response.ok(sessionAccount);
        } else {
            return Response.unauthenticated(authcResponse.getMsg());
        }
    }

    @Transactional
    public Response<AccountVO> registerAccount(AccountVO.RegisterParam param) {
        Response<AccountVO> response = authenticationApi.prepareRegister(param);
        if (response.ok()) {
            // 保存本地数据
            AdminAccountPO account = new AdminAccountPO();
            account.setAccountId(response.getData().getAccountId());
            account.setProjectId(response.getData().getProjectId());
            account.setIsSuperAdmin(response.getData().getIsSuperAdmin());
            adminAccountService.save(account);

            // 为角色授权默认权限
            AuthorizationVO.AccountGrantParam grantParam = new AuthorizationVO.AccountGrantParam();
            grantParam.setProjectId(account.getProjectId());
            grantParam.setAccountId(account.getAccountId());
            grantParam.setPermPolicies(createDefaultPermPolicies());
            authorizationApi.updateGrant(grantParam);

            // 修改账户状态为激活
            Response<Boolean> commitResponse =authenticationApi.commitRegister(new AccountVO.DeleteParam(account.getProjectId(), account.getAccountId()));
            Verifies.verify(commitResponse.ok() && commitResponse.getData(), "激活用户失败");
        }
        return response;
    }

    /**
     * <p>创建账户时，为账户赋予默认权限策略。约定基础权限的权限code不变(在初始化DB脚本中设置)，基础权限有如下几个：</p>
     * <ol>
     *    <li>authc.session  会话权限，可用于读取会话、授权菜单、授权项目、切换项目、注销登录。</li>
     * </ol>
     */
    private List<PermissionPolicyVO> createDefaultPermPolicies() {
        List<PermissionPolicyVO> policies = new ArrayList<>();
        policies.add(new PermissionPolicyVO("authc.session", PermissionAuthcPolicies.ALLOWED));
        return policies;
    }

}
