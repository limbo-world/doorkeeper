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

package org.limbo.doorkeeper.server.support.auth.checker;

import lombok.Setter;
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationCheckParam;
import org.limbo.doorkeeper.server.dao.ResourceMapper;
import org.limbo.doorkeeper.server.entity.Resource;

import java.util.Collections;
import java.util.List;

/**
 * @author brozen
 * @date 2021/1/18
 */
public class NameAuthorizationChecker<P extends AuthorizationCheckParam<String>> extends AbstractAuthorizationChecker<P, String> {

    @Setter
    private ResourceMapper resourceMapper;

    public NameAuthorizationChecker(P checkParam) {
        super(checkParam);
    }

    /**
     * {@inheritDoc}<br/>
     *
     * 根据资源名称来找到资源
     *
     * @param resourceName 资源名称
     * @return
     */
    @Override
    protected List<Resource> assignCheckingResources(String resourceName) {
        return Collections.singletonList(resourceMapper.getByName(client.getRealmId(), client.getClientId(), resourceName));
    }

}
