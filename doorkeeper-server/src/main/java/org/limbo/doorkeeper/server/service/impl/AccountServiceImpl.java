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

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.AccountQueryParam;
import org.limbo.doorkeeper.api.model.param.AccountRoleAddParam;
import org.limbo.doorkeeper.api.model.param.AccountUpdateParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.server.constants.BusinessType;
import org.limbo.doorkeeper.server.constants.OperateType;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectAccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectMapper;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.entity.Project;
import org.limbo.doorkeeper.server.entity.ProjectAccount;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.service.AccountRoleService;
import org.limbo.doorkeeper.server.service.AccountService;
import org.limbo.doorkeeper.server.support.plog.PLog;
import org.limbo.doorkeeper.server.support.plog.PLogConstants;
import org.limbo.doorkeeper.server.support.plog.PLogTag;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brozen
 * @date 2020/3/9 10:58 AM
 * @email brozen@qq.com
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AccountRoleService accountRoleService;

    @Autowired
    private ProjectAccountMapper projectAccountMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    @Transactional
    @PLog(operateType = OperateType.CREATE, businessType = BusinessType.ACCOUNT)
    public AccountVO addAccount(@PLogTag(PLogConstants.CONTENT) Long currentProjectId, Long currentAccountId,
                                @PLogTag(PLogConstants.CONTENT) AccountAddParam param) {

        // 判断账号是否已经存在
        Account po = EnhancedBeanUtils.createAndCopy(param, Account.class);
        try {
            accountMapper.insert(po);
        } catch (DuplicateKeyException e) {
            po = accountMapper.selectOne(Wrappers.<Account>lambdaQuery()
                    .eq(Account::getUsername, param.getUsername())
            );
        }

        // 绑定到对应的项目
        ProjectAccount projectAccount = new ProjectAccount();
        projectAccount.setProjectId(currentProjectId);
        projectAccount.setAccountId(po.getAccountId());
        projectAccount.setIsAdmin(param.getIsAdmin());
        if (!canSetAdminParam(currentAccountId, currentProjectId)) {
            projectAccount.setIsAdmin(false);
        }
        try {
            projectAccountMapper.insert(projectAccount);
        } catch (DuplicateKeyException e) {
            throw new ParamException("项目已绑定对应账户");
        }

        // 找到项目中需要默认添加的角色
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getProjectId, currentProjectId)
                .eq(Role::getIsDefault, true)
        );

        if (CollectionUtils.isNotEmpty(roles)) {
            List<AccountRoleAddParam> accountRoles = new ArrayList<>();
            for (Role role : roles) {
                AccountRoleAddParam accountRole = new AccountRoleAddParam();
                accountRole.setAccountId(po.getAccountId());
                accountRole.setRoleId(role.getRoleId());
                accountRoles.add(accountRole);
            }
            accountRoleService.batchSave(currentProjectId, accountRoles);
        }

        return EnhancedBeanUtils.createAndCopy(po, AccountVO.class);
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.UPDATE, businessType = BusinessType.ACCOUNT)
    public Integer update(@PLogTag(PLogConstants.CONTENT) Long currentProjectId, Long currentAccountId,
                          @PLogTag(PLogConstants.CONTENT) AccountUpdateParam param) {

        int update = accountMapper.update(null, Wrappers.<Account>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getAccountDescribe()), Account::getAccountDescribe, param.getAccountDescribe())
                .set(StringUtils.isNotBlank(param.getNickname()), Account::getNickname, param.getNickname())
                .eq(Account::getAccountId, param.getAccountId())
        );

        if (canSetAdminParam(currentAccountId, currentProjectId)) {
            projectAccountMapper.update(null, Wrappers.<ProjectAccount>lambdaUpdate()
                    .eq(ProjectAccount::getProjectId, currentProjectId)
                    .eq(ProjectAccount::getAccountId, param.getAccountDescribe())
                    .set(ProjectAccount::getIsAdmin, param.getIsAdmin())
            );
        }

        return update;
    }

    /**
     * 是否可以设置admin属性
     * @param accountId 操作用户
     * @param projectId 操作项目
     * @return
     */
    private boolean canSetAdminParam(Long accountId, Long projectId) {
        // 判断操作用户是否为管理端管理员
        List<Project> projects = projectMapper.selectList(Wrappers.<Project>lambdaQuery()
                .eq(Project::getIsAdminProject, true)
        );
        boolean isAdmin = false;
        List<Long> adminProjectIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(projects)) {
            adminProjectIds = projects.stream().map(Project::getProjectId).collect(Collectors.toList());
            List<ProjectAccount> projectAccounts = projectAccountMapper.selectList(Wrappers.<ProjectAccount>lambdaQuery()
                    .eq(ProjectAccount::getAccountId, accountId)
                    .in(ProjectAccount::getProjectId, adminProjectIds)
            );
            for (ProjectAccount projectAccount : projectAccounts) {
                if (projectAccount.getIsAdmin()) {
                    isAdmin = true;
                    break;
                }
            }
        }

        // 管理端管理员 并且项目不为管理端 可以设置
        return !adminProjectIds.contains(projectId);
    }

    @Override
    public Page<AccountVO> queryPage(Long projectId, AccountQueryParam param) {
        param.setProjectId(projectId);
        long count = accountMapper.pageVOCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(accountMapper.pageVOS(param));
        }
        return param;
    }

    @Override
    public List<AccountVO> list(Long projectId) {
        // todo
        List<Account> accounts = accountMapper.selectList(Wrappers.emptyWrapper());
        return EnhancedBeanUtils.createAndCopyList(accounts, AccountVO.class);
    }

    @Override
    public AccountVO get(Long accountId) {
        Account account = accountMapper.selectOne(Wrappers.<Account>lambdaQuery()
                .eq(Account::getAccountId, accountId)
        );
        return account == null ? null : EnhancedBeanUtils.createAndCopy(account, AccountVO.class);
    }

}
