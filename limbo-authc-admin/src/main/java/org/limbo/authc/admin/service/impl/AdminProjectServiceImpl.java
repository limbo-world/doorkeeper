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

package org.limbo.authc.admin.service.impl;

import org.limbo.authc.admin.dubbo.consumers.ProjectDubboConsumer;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.limbo.authc.admin.beans.po.AdminProjectPO;
import org.limbo.authc.admin.dao.AdminProjectMapper;
import org.limbo.authc.admin.service.AdminProjectService;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.ProjectVO;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author devil
 * @date 2020/3/13
 */
@Slf4j
@Service
public class AdminProjectServiceImpl extends ServiceImpl<AdminProjectMapper, AdminProjectPO> implements AdminProjectService {

    @Autowired
    private ProjectDubboConsumer projectDubboConsumer;

    @Override
    @Transactional
    public Response<ProjectVO> add(ProjectVO.AddParam param) {
        Response<ProjectVO> response = projectDubboConsumer.prepareAdd(param);
        if (response.ok()) {
            // 本地项目插入project数据
            AdminProjectPO adminProjectPO = new AdminProjectPO();
            adminProjectPO.setProjectId(response.getData().getProjectId());
            adminProjectPO.setProjectName(response.getData().getProjectName());
            baseMapper.insert(adminProjectPO);

            param.setProjectId(adminProjectPO.getProjectId());
            projectDubboConsumer.commitAdd(param);
        }
        return response;
    }

    @Override
    public Response<AdminProjectPO> update(ProjectVO.UpdateParam param) {
        // 更新本地数据
        AdminProjectPO adminProjectPO = new AdminProjectPO();
        adminProjectPO.setProjectId(param.getProjectId());
        adminProjectPO.setProjectName(param.getProjectName());
        baseMapper.updateById(adminProjectPO);

        Response<Integer> response = projectDubboConsumer.update(param);
        Verifies.verify(response.ok() && response.getData() >= 1, response.getMsg());

        return Response.ok(adminProjectPO);
    }

    @Override
    @Transactional
    public Response<ProjectVO> delete(Long projectId) {
        // 删除本地项目
        baseMapper.deleteById(projectId);
        Response<ProjectVO> response = projectDubboConsumer.delete(new ProjectVO.DeleteParam(projectId));
        Verifies.verify(response.ok(), "项目删除失败");
        return response;
    }
}
