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

package org.limbo.doorkeeper.api.client.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.client.ProjectClient;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 3:34 PM
 */
@Slf4j
@Component
public class ProjectClientFallback extends Fallback implements FallbackFactory<ProjectClient> {
    @Override
    public ProjectClient create(Throwable throwable) {
        log.error("服务调用失败", throwable);
        return new ProjectClient() {
            @Override
            public Response<ProjectVO> addProject(ProjectAddParam project) {
                return serviceUnavailable();
            }

            @Override
            public Response<List<ProjectVO>> getAll() {
                return serviceUnavailable();
            }

            @Override
            public Response<String> getProjectSecret(Long projectId) {
                return serviceUnavailable();
            }

            @Override
            public Response<ProjectVO> getProject(Long projectId) {
                return serviceUnavailable();
            }

            @Override
            public Response<Integer> updateProject(Long projectId, ProjectUpdateParam project) {
                return serviceUnavailable();
            }

            @Override
            public Response<Page<ProjectVO>> getProjects(ProjectQueryParam param) {
                return serviceUnavailable();
            }
        };
    }
}
