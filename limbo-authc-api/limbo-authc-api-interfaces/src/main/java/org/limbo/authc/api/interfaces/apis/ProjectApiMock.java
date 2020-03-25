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
 * @author Brozen
 * @date 2020/3/13 2:54 PM
 * @email brozen@qq.com
 */
public class ProjectApiMock implements ProjectApi {
    @Override
    public Response<ProjectVO> add(ProjectVO.AddParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<ProjectVO> prepareAdd(ProjectVO.AddParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Boolean> commitAdd(ProjectVO.AddParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<List<ProjectVO>> list() {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Page<ProjectVO>> query(ProjectVO.QueryParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<ProjectVO> get(ProjectVO.GetParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<String> getSecret(ProjectVO.GetParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<Integer> update(ProjectVO.UpdateParam param) {
        return Response.serviceError("服务暂不可用");
    }

    @Override
    public Response<ProjectVO> delete(ProjectVO.DeleteParam param) {
        return Response.serviceError("服务暂不可用");
    }
}
