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

package org.limbo.doorkeeper.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.admin.dao.AdminMapper;
import org.limbo.doorkeeper.admin.dao.AdminProjectMapper;
import org.limbo.doorkeeper.admin.entity.AdminProject;
import org.limbo.doorkeeper.admin.model.param.AdminProjectQueryParam;
import org.limbo.doorkeeper.admin.model.param.AdminProjectUpdateParam;
import org.limbo.doorkeeper.admin.service.AdminProjectService;
import org.limbo.doorkeeper.admin.utils.Verifies;
import org.limbo.doorkeeper.api.client.AccountClient;
import org.limbo.doorkeeper.api.client.ProjectClient;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2020/11/24 10:25 AM
 */
@Service
public class AdminProjectServiceImpl implements AdminProjectService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminProjectMapper adminProjectMapper;
    @Autowired
    private ProjectClient projectClient;
    @Autowired
    private AccountClient accountClient;

    @Override
    public List<AdminProject> getByAccount(Long accountId) {
        Response<AccountVO> accountVOResponse = accountClient.get(accountId);
        Verifies.verify(accountVOResponse.ok(), String.format("获取远程账户失败 %s", accountVOResponse.getMsg()));

        AccountVO accountVO = accountVOResponse.getData();
        Verifies.notNull(accountVO, "管理员不存在");
        if (accountVO.getIsAdmin()) {
            Response<List<ProjectVO>> all = projectClient.getAll();
            List<AdminProject> projects = new ArrayList<>();
            for (ProjectVO projectVO : all.getData()) {
                AdminProject project = new AdminProject();
                project.setAccountId(accountId);
                project.setProjectId(projectVO.getProjectId());
                project.setProjectName(projectVO.getProjectName());
                projects.add(project);
            }
            return projects;
        }
        return adminProjectMapper.getByAccount(accountId);
    }

    @Override
    public List<AdminProject> list(AdminProjectQueryParam param) {
        Response<AccountVO> accountVOResponse = accountClient.get(param.getAccountId());
        Verifies.verify(accountVOResponse.ok(), String.format("获取远程账户失败 %s", accountVOResponse.getMsg()));

        AccountVO accountVO = accountVOResponse.getData();
        Verifies.notNull(accountVO, "管理员不存在");
        if (accountVO.getIsAdmin()) {
            Response<List<ProjectVO>> all = projectClient.getAll();
            List<AdminProject> projects = new ArrayList<>();
            for (ProjectVO projectVO : all.getData()) {
                AdminProject project = new AdminProject();
                project.setAccountId(param.getAccountId());
                project.setProjectId(projectVO.getProjectId());
                project.setProjectName(projectVO.getProjectName());
                projects.add(project);
            }
            return projects;
        }
        return adminProjectMapper.selectList(Wrappers.<AdminProject>lambdaQuery()
                .eq(param.getProjectId() != null, AdminProject::getProjectId, param.getProjectId())
                .eq(AdminProject::getAccountId, param.getAccountId())
        );
    }

    @Override
    public AdminProject getByAccountProject(Long accountId, Long projectId) {
        return adminProjectMapper.getByAccountProject(accountId, projectId);
    }

    @Override
    public void update(AdminProjectUpdateParam param) {
        if (CollectionUtils.isNotEmpty(param.getDeleteAdminProjectIds())) {
            adminProjectMapper.deleteBatchIds(param.getDeleteAdminProjectIds());
        }
        if (CollectionUtils.isNotEmpty(param.getAddAdminProjects())) {
            adminProjectMapper.batchInsertIgnore(param.getAddAdminProjects());
        }
    }
}
