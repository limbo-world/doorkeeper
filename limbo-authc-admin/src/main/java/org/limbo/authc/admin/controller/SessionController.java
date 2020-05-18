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

package org.limbo.authc.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.limbo.authc.admin.beans.po.AdminBLogPO;
import org.limbo.authc.admin.dubbo.consumers.AuthorizationDubboConsumer;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.limbo.authc.admin.beans.po.AdminAccountProjectPO;
import org.limbo.authc.admin.beans.po.AdminProjectPO;
import org.limbo.authc.admin.service.AdminAccountProjectService;
import org.limbo.authc.admin.service.AdminProjectService;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.admin.support.blog.BLogType;
import org.limbo.authc.admin.support.session.AdminSession;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AuthorizationVO;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.session.SessionAccount;
import org.limbo.authc.session.utils.HiJackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author devil
 * @date 2020/3/11
 */
@Slf4j
@RestController
@RequestMapping("/session")
public class SessionController extends BaseController {

    @Autowired
    private AuthorizationDubboConsumer authorizationDubboConsumer;

    @Autowired
    private AdminAccountProjectService adminAccountProjectService;

    @Autowired
    private AdminProjectService adminProjectService;

    @GetMapping
    public Response<AdminSession> session() {
        AdminSession session = getSession();
        session.getSecurityDigest().setPrivateKey(null);
        return Response.ok(session);
    }

    /**
     * 约定 项目管理 菜单的code值为001，在数据库初始化脚本中指定
     */
    private final String projectMenuCode = "001";

    @GetMapping("/menus")
    public Response<List<MenuVO>> menus() {
        SessionAccount sessionAccount = getSessionAccount();
        Long accountId = sessionAccount.getAccountId();
        Long accountProjectId = sessionAccount.getAccountProjectId();

        AuthorizationVO.GetMenusParam param = new AuthorizationVO.GetMenusParam(accountProjectId, accountId);
        Response<List<MenuVO>> response = authorizationDubboConsumer.getMenus(param);

        // 如果没有选择管理端项目 则移除项目菜单
        if (response.ok() && !accountProjectId.equals(sessionAccount.getCurrentProjectId())
                && CollectionUtils.isNotEmpty(response.getData())) {
            response.getData().removeIf(menu -> menu.getMenuCode().startsWith(projectMenuCode));
        }
        return response;
    }

    /**
     * 切换用户当前选择的项目
     */
    @PutMapping("/project/{projectId}")
    @BLog(expression = "'选中项目 '.concat(#account.getCurrentProjectName())", type = BLogType.UPDATE)
    public Response<AdminSession> switchProject(
            @PathVariable("projectId") @Valid @NotNull(message = "项目不存在") Long projectId) {
        AdminSession session = getSession();
        SessionAccount account = session.getAccount();

        // 检测是否有项目的操作权限
        if (BooleanUtils.isFalse(account.getIsSuperAdmin()) && adminAccountProjectService.count(Wrappers.<AdminAccountProjectPO>lambdaQuery()
                .eq(AdminAccountProjectPO::getProjectId, projectId)
                .eq(AdminAccountProjectPO::getAccountId, account.getAccountId())) <= 0) {
            return Response.paramError("无权操作此项目！");
        }

        // 刷新会话，因为不是原子操作，不一定成功，所以返回会话数据时需要重新读取一次
        AdminProjectPO project = adminProjectService.getById(projectId);
        Verifies.notNull(project, "项目不存在");

        account.setCurrentProjectId(project.getProjectId());
        account.setCurrentProjectName(project.getProjectName());
        sessionDAO.save(session);
        return Response.ok(getSession());
    }

    @GetMapping("/logout")
    public Response<Boolean> logout() {
        AdminSession session = getSession();
        sessionDAO.destroySession(session.getSessionId());

        // 记录注销日志
        AdminBLogPO log = new AdminBLogPO();
        log.setAccountId(session.getAccount().getAccountId());
        log.setAccountNick(session.getAccount().getAccountNick());
        log.setLogName("注销登录");
        log.setLogType(BLogType.LOGOUT.name());
        log.setIp(clientIp());
        log.setParam("[]");
        log.setSession(HiJackson.toJson(session));
        bLogService.addLog(log);

        return Response.ok(true);
    }

}
