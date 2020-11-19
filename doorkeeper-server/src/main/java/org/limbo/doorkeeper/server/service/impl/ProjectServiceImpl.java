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
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.limbo.doorkeeper.server.dao.ProjectMapper;
import org.limbo.doorkeeper.server.entity.Project;
import org.limbo.doorkeeper.server.service.ProjectService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.UUIDUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    @Transactional
    public ProjectVO addProject(ProjectAddParam param, Boolean isActivated) {
        Project project = EnhancedBeanUtils.createAndCopy(param, Project.class);
        project.setIsActivated(isActivated);
        project.setCode(UUIDUtils.get());
        project.setSecret(UUIDUtils.get());

        projectMapper.replace(project);
        return EnhancedBeanUtils.createAndCopy(project, ProjectVO.class);
    }

    @Override
    @Transactional
    public Integer updateProject(ProjectUpdateParam param) {
        Project project = projectMapper.selectById(param.getProjectId());
        Verifies.notNull(project, "项目不存在");

        LambdaUpdateWrapper<Project> update = Wrappers.<Project>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getName()), Project::getName, param.getName())
                .set(StringUtils.isNotBlank(param.getDescribe()), Project::getDescribe, param.getDescribe())
                .eq(Project::getProjectId, param.getProjectId());

        return projectMapper.update(null, update);
    }

    @Override
    @Transactional
    public Project deleteProject(Long projectId) {
        Project project = get(projectId);
        // 假删除，如果是真删除，需要关联删除账户、角色、菜单、权限
        projectMapper.update(null, Wrappers.<Project>lambdaUpdate()
                .set(Project::getIsDeleted, true)
                .eq(Project::getProjectId, projectId)
        );
        return project;
    }

    @Override
    public Project get(Long projectId) {
        return projectMapper.selectOne(columnNoSecret()
                .eq(Project::getProjectId, projectId)
                .eq(Project::getIsDeleted, false)
                .eq(Project::getIsActivated, true));
    }

    @Override
    public Project get(String projectCode) {
        return projectMapper.selectOne(Wrappers.<Project>lambdaQuery()
                .eq(Project::getCode, projectCode)
                .eq(Project::getIsDeleted, false)
                .eq(Project::getIsActivated, true)
        );
    }

    @Override
    public List<ProjectVO> listProject() {
        List<Project> allProjects = projectMapper.selectList(columnNoSecret()
                .eq(Project::getIsDeleted, false)
                .eq(Project::getIsActivated, true));
        return EnhancedBeanUtils.createAndCopyList(allProjects, ProjectVO.class);
    }

    @Override
    public Page<ProjectVO> queryProjectPage(ProjectQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Project> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<Project> condition = columnNoSecret()
                .like(StringUtils.isNotBlank(param.getName()), Project::getName, param.getName())
                .eq(Project::getIsDeleted, false)
                .eq(Project::getIsActivated, true);
        mpage = projectMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), ProjectVO.class));
        return param;
    }

    // 没有secret
    private LambdaQueryWrapper<Project> columnNoSecret() {
        return Wrappers.<Project>lambdaQuery().select(
                Project::getProjectId,
                Project::getCode,
                Project::getName,
                Project::getDescribe,
                Project::getGmtCreated,
                Project::getGmtModified
        );
    }
}
