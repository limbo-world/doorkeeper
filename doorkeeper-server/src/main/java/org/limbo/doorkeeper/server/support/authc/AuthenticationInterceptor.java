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

package org.limbo.doorkeeper.server.support.authc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.model.param.AuthenticationCheckParam;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.api.model.vo.SessionVO;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectAccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.entity.Project;
import org.limbo.doorkeeper.server.entity.ProjectAccount;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.limbo.doorkeeper.server.support.session.RedisSessionDAO;
import org.limbo.doorkeeper.server.support.session.exception.SessionException;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2020/11/24 19:29
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final RedisSessionDAO redisSessionDAO;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ProjectAccountMapper projectAccountMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProjectMapper projectMapper;

    private List<String> adminPatterns;

    public AuthenticationInterceptor(RedisSessionDAO redisSessionDAO, List<String> adminPatterns) {
        this.redisSessionDAO = redisSessionDAO;
        this.adminPatterns = adminPatterns;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 判断 url 是否有对应权限
        String sessionId = request.getHeader(DoorkeeperConstants.TOKEN_HEADER);
        SessionVO adminSession = redisSessionDAO.readSessionMayNull(sessionId);
        if (adminSession == null) {
            throw new SessionException("无有效会话");
        }

        AuthenticationCheckParam param = new AuthenticationCheckParam();
        param.setAccountId(adminSession.getAccount().getAccountId());
        param.setMethod(request.getMethod());
        param.setPath(request.getServletPath());

        String projectIdStr = request.getHeader(DoorkeeperConstants.PROJECT_HEADER);
        Verifies.notBlank(projectIdStr, "当前操作项目为空");
        return accessAllowed(Long.valueOf(projectIdStr), param);
    }

    /**
     * @param projectId 当前操作的项目
     * @param param
     * @return
     */
    private Boolean accessAllowed(Long projectId, AuthenticationCheckParam param) {

        Account account = accountMapper.selectById(param.getAccountId());
        if (account == null) {
            return false;
        }

        ProjectAccount projectAccount = projectAccountMapper.selectOne(Wrappers.<ProjectAccount>lambdaQuery()
                .eq(ProjectAccount::getProjectId, projectId)
                .eq(ProjectAccount::getAccountId, param.getAccountId())
        );
        if (projectAccount == null) {
            return false;
        }

        boolean match = false;
        for (String adminPattern : adminPatterns) {
            if (authenticationService.pathMatch(adminPattern, param.getPath())) {
                match = true;
                break;
            }
        }

        if (match) { // 判断是否特定路径 特定路径只能由管理端成员 或拥有特定权限才能访问
            Project project = projectMapper.selectById(projectId);
            if (project == null) {
                return false;
            }
            // 如果当前项目不为管理端项目
            if (!project.getIsAdminProject()) {
                return false;
            }

            // 判断操作用户是否为管理端管理员
            List<Project> projects = projectMapper.selectList(Wrappers.<Project>lambdaQuery()
                    .eq(Project::getIsAdminProject, true)
            );

            boolean isAdmin = false;
            if (CollectionUtils.isNotEmpty(projects)) {
                List<ProjectAccount> projectAccounts = projectAccountMapper.selectList(Wrappers.<ProjectAccount>lambdaQuery()
                        .eq(ProjectAccount::getAccountId, param.getAccountId())
                        .in(ProjectAccount::getProjectId, projects.stream().map(Project::getProjectId).collect(Collectors.toList()))
                );
                for (ProjectAccount pa : projectAccounts) {
                    if (pa.getIsAdmin()) {
                        isAdmin = true;
                        break;
                    }
                }
            }

            if (isAdmin) {
                return true;
            }

        } else {

            // 管理员拥有全部权限
            if (projectAccount.getIsAdmin()) {
                return true;
            }

        }

        // 拿到用户管理端权限
        AccountGrantVO grants = authenticationService.getGrantedAdminPermissions(projectId, account.getAccountId());
        // 如果没有授权信息 或 授权访问的API列表为空，则禁止访问
        if (grants == null || CollectionUtils.isEmpty(grants.getAllowedPermissions())) {
            return false;
        }

        // 首先检测refused
        List<PermissionVO> refusedPermissions = grants.getRefusedPermissions();
        if (CollectionUtils.isNotEmpty(refusedPermissions)) {
            // 如果存在请求方法与请求路径匹配，说明禁止访问
            if (refusedPermissions.stream().anyMatch(permission -> authenticationService.permissionMatch(permission, param))) {
                return false;
            }
        }

        // 然后检测allowed
        List<PermissionVO> allowedPermissions = grants.getAllowedPermissions();
        // 如果存在请求方法与请求路径匹配，说明允许访问
        return allowedPermissions.stream().anyMatch(permission -> authenticationService.permissionMatch(permission, param));

    }

}
