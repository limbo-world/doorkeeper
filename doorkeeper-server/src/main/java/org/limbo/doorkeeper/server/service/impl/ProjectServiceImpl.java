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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.po.ProjectPO;
import org.limbo.authc.api.interfaces.beans.vo.ProjectVO;
import org.limbo.authc.api.interfaces.utils.EnhancedBeanUtils;
import org.limbo.authc.api.interfaces.utils.UUIDUtils;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.core.dao.ProjectMapper;
import org.limbo.authc.core.service.ProjectService;
import org.limbo.authc.core.utils.MyBatisPlusUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 3:42 PM
 * @email brozen@qq.com
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectPO> implements ProjectService {

    @Override
    @Transactional
    public ProjectVO addProject(ProjectVO.AddParam param, Boolean isActivated) {
        if (StringUtils.isNotBlank(param.getProjectCode())) {
            ProjectPO dbProject = baseMapper.selectOne(Wrappers.<ProjectPO>lambdaQuery()
                    .eq(ProjectPO::getProjectCode, param.getProjectCode())
                    .eq(ProjectPO::getIsDeleted, false)
                    .eq(ProjectPO::getIsActivated, true));
            Verifies.isNull(dbProject, "项目编码已存在");
        } else {
            param.setProjectCode(UUIDUtils.get());
        }
        param.setProjectSecret(UUIDUtils.get());

        ProjectPO project = EnhancedBeanUtils.createAndCopy(param, ProjectPO.class);
        project.setIsActivated(isActivated);

        baseMapper.replace(project);
        return EnhancedBeanUtils.createAndCopy(project, ProjectVO.class);
    }

    @Override
    @Transactional
    public Integer updateProject(ProjectVO.UpdateParam param) {
        ProjectPO project = baseMapper.selectById(param.getProjectId());
        Verifies.notNull(project, "项目不存在");

        LambdaUpdateWrapper<ProjectPO> update = Wrappers.<ProjectPO>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getProjectCode()), ProjectPO::getProjectCode, param.getProjectCode())
                .set(StringUtils.isNotBlank(param.getProjectName()), ProjectPO::getProjectName, param.getProjectName())
                .set(StringUtils.isNotBlank(param.getProjectDesc()), ProjectPO::getProjectDesc, param.getProjectDesc())
                .eq(ProjectPO::getProjectId, param.getProjectId());

        return baseMapper.update(null, update);
    }

    @Override
    @Transactional
    public ProjectVO deleteProject(Long projectId) {
        ProjectVO project = get(projectId);
        // 假删除，如果是真删除，需要关联删除账户、角色、菜单、权限
        baseMapper.update(null, Wrappers.<ProjectPO>lambdaUpdate()
                .set(ProjectPO::getIsDeleted, true)
                .eq(ProjectPO::getProjectId, projectId)
        );
        return project;
    }

    @Override
    public ProjectVO get(Long projectId) {
        ProjectPO project = baseMapper.selectOne(columnNoSecret()
                .eq(ProjectPO::getProjectId, projectId)
                .eq(ProjectPO::getIsDeleted, false)
                .eq(ProjectPO::getIsActivated, true));
        return EnhancedBeanUtils.createAndCopy(project, ProjectVO.class);
    }

    @Override
    public ProjectPO get(String projectCode) {
        return baseMapper.selectOne(Wrappers.<ProjectPO>lambdaQuery()
                .eq(ProjectPO::getProjectCode, projectCode)
                .eq(ProjectPO::getIsDeleted, false)
                .eq(ProjectPO::getIsActivated, true)
        );
    }

    @Override
    public List<ProjectVO> listProject() {
        List<ProjectPO> allProjects = baseMapper.selectList(columnNoSecret()
                .eq(ProjectPO::getIsDeleted, false)
                .eq(ProjectPO::getIsActivated, true));
        return EnhancedBeanUtils.createAndCopyList(allProjects, ProjectVO.class);
    }

    @Override
    public Page<ProjectVO> queryProjectPage(ProjectVO.QueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProjectPO> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<ProjectPO> condition = columnNoSecret()
                .like(StringUtils.isNotBlank(param.getProjectName()), ProjectPO::getProjectName, param.getProjectName())
                .eq(ProjectPO::getIsDeleted, false)
                .eq(ProjectPO::getIsActivated, true);
        mpage = baseMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), ProjectVO.class));
        return param;
    }

    // 没有secret
    private LambdaQueryWrapper<ProjectPO> columnNoSecret() {
        return Wrappers.<ProjectPO>lambdaQuery().select(
                ProjectPO::getProjectId,
                ProjectPO::getProjectCode,
                ProjectPO::getProjectName,
                ProjectPO::getProjectDesc,
                ProjectPO::getGmtCreated,
                ProjectPO::getGmtModified
        );
    }
}
