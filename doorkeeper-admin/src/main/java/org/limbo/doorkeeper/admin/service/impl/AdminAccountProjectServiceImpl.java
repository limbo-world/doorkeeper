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
import org.limbo.doorkeeper.admin.dao.AdminAccountMapper;
import org.limbo.doorkeeper.admin.dao.AdminAccountProjectMapper;
import org.limbo.doorkeeper.admin.entity.AdminAccount;
import org.limbo.doorkeeper.admin.entity.AdminAccountProject;
import org.limbo.doorkeeper.admin.model.param.AdminProjectQueryParam;
import org.limbo.doorkeeper.admin.service.AdminAccountProjectService;
import org.limbo.doorkeeper.admin.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.api.client.ProjectClient;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2020/11/24 10:25 AM
 */
@Service
public class AdminAccountProjectServiceImpl implements AdminAccountProjectService {

    @Autowired
    private AdminAccountMapper adminAccountMapper;
    @Autowired
    private AdminAccountProjectMapper adminAccountProjectMapper;
    @Autowired
    private ProjectClient projectClient;

    @Override
    public List<AdminAccountProject> getByAccount(Long accountId) {
        AdminAccount adminAccount = adminAccountMapper.selectById(accountId);
        if (adminAccount.getIsAdmin()) {
            Response<List<ProjectVO>> all = projectClient.getAll();
            List<AdminAccountProject> projects = new ArrayList<>();
            for (ProjectVO projectVO : all.getData()) {
                AdminAccountProject project = new AdminAccountProject();
                project.setAccountId(adminAccount.getAccountId());
                project.setProjectId(projectVO.getProjectId());
                project.setProjectName(projectVO.getProjectName());
                projects.add(project);
            }
            return projects;
        }
        return adminAccountProjectMapper.getByAccount(accountId);
    }

    @Override
    public List<AdminAccountProject> list(AdminProjectQueryParam param) {
        return adminAccountProjectMapper.selectList(Wrappers.<AdminAccountProject>lambdaQuery()
                .eq(param.getProjectId() != null, AdminAccountProject::getProjectId, param.getProjectId())
                .eq(param.getAccountId() != null, AdminAccountProject::getAccountId, param.getAccountId())
        );
    }

    @Override
    public AdminAccountProject getByAccountProject(Long accountId, Long projectId) {
        return adminAccountProjectMapper.getByAccountProject(accountId, projectId);
    }

    @Override
    @Transactional
    public void updateAccountProjects(Long accountId, List<Long> projectIds) {
        // 先删后增
        adminAccountProjectMapper.delete(Wrappers.<AdminAccountProject>lambdaQuery().eq(AdminAccountProject::getAccountId, accountId));

        if (CollectionUtils.isEmpty(projectIds)) return;

        List<AdminAccountProject> pos = new ArrayList<>();
        for (Long projectId : projectIds) {
            AdminAccountProject po = new AdminAccountProject();
            po.setAccountId(accountId);
            po.setProjectId(projectId);
//            po.setProjectName(projectId); // todo
            pos.add(po);
        }

        MyBatisPlusUtils.batchSave(pos, AdminAccountProject.class);
    }
}
