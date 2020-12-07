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

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.*;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectAccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectMapper;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.entity.ProjectAccount;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.service.AccountRoleService;
import org.limbo.doorkeeper.server.service.AccountService;
import org.limbo.doorkeeper.server.service.ProjectAccountService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Devil
 * @Date 2020/12/3 3:30 下午
 */
@Service
public class ProjectAccountServiceImpl implements ProjectAccountService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectAccountMapper projectAccountMapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private AccountRoleService accountRoleService;
    @Autowired
    private ProjectAccountService projectAccountService;

    @Override
    public List<ProjectAccountVO> list(ProjectAccountQueryParam param) {
        List<ProjectAccountVO> accountProjects = projectAccountMapper.selectVOS(param);
        return CollectionUtils.isEmpty(accountProjects) ? new ArrayList<>() : accountProjects;
    }

    @Override
    public Page<ProjectAccountVO> page(ProjectAccountQueryParam param) {
        long count = projectAccountMapper.pageVOCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(projectAccountMapper.pageVOS(param));
        }
        return param;
    }

    @Override
    public Page<ProjectAccountVO> pageAllAccount(ProjectAccountQueryParam param) {
        Integer count = accountMapper.selectCount(Wrappers.emptyWrapper());
        param.setTotal(count);
        if (count > 0) {
            List<ProjectAccountVO> projectAccountVOS = projectAccountMapper.pageAllAccountVOS(param);
            param.setData(projectAccountVOS);
        }

        return param;
    }

    @Override
    @Transactional
    public AccountVO save(Long currentAccountId, ProjectAccountAddParam param) {
        return accountService.addAccount(param.getProjectId(), currentAccountId,
                EnhancedBeanUtils.createAndCopy(param, AccountAddParam.class), false);
    }

    @Override
    @Transactional
    public void update(Long currentAccountId, ProjectAccountUpdateParam param) {
        ProjectAccount projectAccount = projectAccountMapper.selectById(param.getProjectAccountId());
        AccountUpdateParam accountUpdateParam = new AccountUpdateParam();
        accountUpdateParam.setNickname(param.getNickname());
        accountUpdateParam.setAccountDescribe(param.getAccountDescribe());
        accountUpdateParam.setAccountId(projectAccount.getAccountId());
        accountUpdateParam.setIsAdmin(param.getIsAdmin());
        accountService.update(projectAccount.getProjectId(), currentAccountId, accountUpdateParam, false);
    }

    @Override
    @Transactional
    public void batchJoinProject(ProjectAccountBatchUpdateParam param) {
        for (Long accountId : param.getAccountIds()) {
            projectAccountService.joinProject(param.getProjectId(), accountId, false);
        }
    }

    @Override
    @Transactional
    public void joinProject(Long projectId, Long accountId, boolean isAdmin) {
        ProjectAccount projectAccount = new ProjectAccount();
        projectAccount.setProjectId(projectId);
        projectAccount.setAccountId(accountId);
        projectAccount.setIsAdmin(isAdmin);
        try {
            projectAccountMapper.insert(projectAccount);
        } catch (DuplicateKeyException e) {
            return;
        }

        // 找到项目中需要默认添加的角色
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getProjectId, projectAccount.getProjectAccountId())
                .eq(Role::getIsDefault, true)
        );

        if (CollectionUtils.isNotEmpty(roles)) {
            List<AccountRoleAddParam> accountRoles = new ArrayList<>();
            for (Role role : roles) {
                AccountRoleAddParam accountRole = new AccountRoleAddParam();
                accountRole.setAccountId(accountId);
                accountRole.setRoleId(role.getRoleId());
                accountRoles.add(accountRole);
            }
            accountRoleService.batchSave(projectId, accountRoles);
        }
    }

}