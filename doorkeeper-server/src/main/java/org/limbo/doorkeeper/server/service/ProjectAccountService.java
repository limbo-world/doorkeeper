/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.ProjectAccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;

import java.util.List;

/**
 * @Author Devil
 * @Date 2020/12/3 3:30 下午
 */
public interface ProjectAccountService {

    /**
     * 列表查询项目账户关系
     */
    List<ProjectAccountVO> list(ProjectAccountQueryParam param);

    /**
     * 分页查询项目账户关系
     */
    Page<ProjectAccountVO> page(ProjectAccountQueryParam param);
}
