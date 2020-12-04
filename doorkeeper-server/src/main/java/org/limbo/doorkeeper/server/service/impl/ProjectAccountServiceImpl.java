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
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectAccountAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectAccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectAccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectMapper;
import org.limbo.doorkeeper.server.entity.Project;
import org.limbo.doorkeeper.server.entity.ProjectAccount;
import org.limbo.doorkeeper.server.service.AccountService;
import org.limbo.doorkeeper.server.service.ProjectAccountService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        // 判断操作用户是否为管理端管理员
        List<Project> projects = projectMapper.selectList(Wrappers.<Project>lambdaQuery()
                .eq(Project::getIsAdminProject, true)
        );
        boolean isAdmin = false;
        List<Long> adminProjectIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(projects)) {
            adminProjectIds = projects.stream().map(Project::getProjectId).collect(Collectors.toList());
            List<ProjectAccount> projectAccounts = projectAccountMapper.selectList(Wrappers.<ProjectAccount>lambdaQuery()
                    .eq(ProjectAccount::getAccountId, currentAccountId)
                    .in(ProjectAccount::getProjectId, adminProjectIds)
            );
            for (ProjectAccount projectAccount : projectAccounts) {
                if (projectAccount.getIsAdmin()) {
                    isAdmin = true;
                    break;
                }
            }
        }

        // 不是管理端管理员 且当前项目为管理端项目 不能提交管理员属性
        if (!isAdmin && adminProjectIds.contains(param.getProjectId())) {
            param.setIsAdmin(false);
        }
        return accountService.addAccount(param.getProjectId(),
                EnhancedBeanUtils.createAndCopy(param, AccountAddParam.class), param.getIsAdmin());
    }

}
