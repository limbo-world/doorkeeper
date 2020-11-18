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

package org.limbo.doorkeeper.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.po.ProjectPO;
import org.limbo.authc.api.interfaces.beans.vo.ProjectVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 3:42 PM
 * @email brozen@qq.com
 */
public interface ProjectService extends IService<ProjectPO> {

    ProjectVO addProject(ProjectVO.AddParam param, Boolean isActivated);

    Integer updateProject(ProjectVO.UpdateParam param);

    ProjectVO deleteProject(Long projectId);

    ProjectVO get(Long projectId);

    ProjectPO get(String projectCode);

    List<ProjectVO> listProject();

    Page<ProjectVO> queryProjectPage(ProjectVO.QueryParam param);
}
