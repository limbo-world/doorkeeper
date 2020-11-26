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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.dao.ProjectMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.entity.Project;
import org.limbo.doorkeeper.server.service.ProjectService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.UUIDUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 3:42 PM
 * @email brozen@qq.com
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    @Transactional
    public ProjectVO addProject(ProjectAddParam param) {
        Project project = EnhancedBeanUtils.createAndCopy(param, Project.class);
        if (StringUtils.isBlank(param.getProjectSecret())) {
            project.setProjectSecret(UUIDUtils.get());
        }
        try {
            projectMapper.insert(project);
        } catch (DuplicateKeyException e) {
            throw new ParamException("项目已存在");
        }

        // 创建超级管理员
        Account account = new Account();
        account.setProjectId(project.getProjectId());
        account.setUsername("admin");
        account.setIsSuperAdmin(true);
        account.setIsAdmin(true);

        accountMapper.insert(account);

        return EnhancedBeanUtils.createAndCopy(project, ProjectVO.class);
    }

    @Override
    @Transactional
    public Integer updateProject(ProjectUpdateParam param) {
        Project project = projectMapper.selectById(param.getProjectId());
        Verifies.notNull(project, "项目不存在");

        LambdaUpdateWrapper<Project> update = Wrappers.<Project>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getProjectName()), Project::getProjectName, param.getProjectName())
                .set(StringUtils.isNotBlank(param.getProjectSecret()), Project::getProjectSecret, param.getProjectSecret())
                .set(StringUtils.isNotBlank(param.getProjectDescribe()), Project::getProjectDescribe, param.getProjectDescribe())
                .eq(Project::getProjectId, param.getProjectId());

        return projectMapper.update(null, update);
    }

    @Override
    public String getSecret(Long projectId) {
        Project project = projectMapper.selectOne(Wrappers.<Project>lambdaQuery()
                .select(Project::getProjectSecret)
                .eq(Project::getProjectId, projectId)
        );
        return project.getProjectSecret();
    }

    @Override
    public List<ProjectVO> all() {
        List<Project> allProjects = projectMapper.selectList(columnNoSecret());
        return EnhancedBeanUtils.createAndCopyList(allProjects, ProjectVO.class);
    }

    @Override
    public Page<ProjectVO> queryProjectPage(ProjectQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Project> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<Project> condition = columnNoSecret()
                .like(StringUtils.isNotBlank(param.getProjectName()), Project::getProjectName, param.getProjectName());
        mpage = projectMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), ProjectVO.class));
        return param;
    }

    // 没有secret
    private LambdaQueryWrapper<Project> columnNoSecret() {
        return Wrappers.<Project>lambdaQuery().select(
                Project::getProjectId,
                Project::getProjectName,
                Project::getProjectDescribe,
                Project::getGmtCreated,
                Project::getGmtModified
        );
    }

}
