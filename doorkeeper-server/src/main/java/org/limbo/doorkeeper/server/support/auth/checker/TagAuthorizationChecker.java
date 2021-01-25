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
import org.limbo.doorkeeper.api.model.vo.ResourceVO;
import org.limbo.doorkeeper.server.dal.dao.ResourceDao;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author brozen
 * @date 2021/1/18
 */
public class TagAuthorizationChecker<P extends AuthorizationCheckParam<Map<String, String>>>
        extends AbstractAuthorizationChecker<P, Map<String, String>> {

    @Setter
    private ResourceDao resourceDao;

    public TagAuthorizationChecker(P checkParam) {
        super(checkParam);
    }

    @Override
    protected List<ResourceVO> assignCheckingResources(List<Map<String, String>> resourcesAssigner) {
        return resourceDao.getVOSByTagCombos(getClient().getRealmId(), getClient().getClientId(), resourcesAssigner, true);
    }

}
