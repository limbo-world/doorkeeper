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

package org.limbo.authc.api.interfaces.apis;

import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.ProjectVO;

import java.util.List;

/**
 * 项目API
 *
 * @author Brozen
 * @date 2020/3/7 4:33 PM
 * @email brozen@qq.com
 */
public interface ProjectApi {

    /**
     * 创建项目
     */
    Response<ProjectVO> add(ProjectVO.AddParam param);

    /**
     * 创建项目 补偿
     */
    Response<ProjectVO> prepareAdd(ProjectVO.AddParam param);

    /**
     * 激活项目
     */
    Response<Boolean> commitAdd(ProjectVO.AddParam param);

    /**
     * 获取全部项目
     */
    Response<List<ProjectVO>> list();

    /**
     * 分页查询
     */
    Response<Page<ProjectVO>> query(ProjectVO.QueryParam param);

    /**
     * 获取项目
     */
    Response<ProjectVO> get(ProjectVO.GetParam param);

    /**
     * 获取项目secret
     */
    Response<String> getSecret(ProjectVO.GetParam param);

    /**
     * 更新项目
     */
    Response<Integer> update(ProjectVO.UpdateParam param);

    /**
     * 删除项目
     */
    Response<ProjectVO> delete(ProjectVO.DeleteParam param);

}
