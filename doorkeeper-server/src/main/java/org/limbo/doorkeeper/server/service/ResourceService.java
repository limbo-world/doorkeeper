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
import org.limbo.doorkeeper.api.model.param.ResourceAddParam;
import org.limbo.doorkeeper.api.model.param.ResourceBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.ResourceQueryParam;
import org.limbo.doorkeeper.api.model.param.ResourceUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;

/**
 * @author Devil
 * @date 2021/1/5 4:59 下午
 */
public interface ResourceService {

    ResourceVO add(ResourceAddParam param);

    void update(Long resourceId, ResourceUpdateParam param);

    void batchUpdate(ResourceBatchUpdateParam param);

    ResourceVO get(Long resourceId);

    Page<ResourceVO> page(ResourceQueryParam param);

}
