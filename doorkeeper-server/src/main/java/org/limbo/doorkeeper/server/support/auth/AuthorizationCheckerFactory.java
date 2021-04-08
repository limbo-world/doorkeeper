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

package org.limbo.doorkeeper.server.support.auth;

import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.server.dal.dao.PolicyDao;
import org.limbo.doorkeeper.server.dal.mapper.*;
import org.limbo.doorkeeper.server.support.auth.policies.PolicyCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 授权校验器
 *
 * @author Devil
 * @date 2021/3/31 2:04 下午
 */
@Slf4j
@Component
public class AuthorizationCheckerFactory {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PolicyDao policyDao;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceUriMapper resourceUriMapper;

    /**
     * 策略检查器工厂
     */
    @Autowired
    private PolicyCheckerFactory policyCheckerFactory;


    public AuthorizationChecker createChecker() {
        AuthorizationChecker authorizationChecker = new AuthorizationChecker();
        authorizationChecker.setPermissionMapper(permissionMapper);
        authorizationChecker.setPolicyDao(policyDao);
        authorizationChecker.setClientMapper(clientMapper);
        authorizationChecker.setPermissionResourceMapper(permissionResourceMapper);
        authorizationChecker.setResourceMapper(resourceMapper);
        authorizationChecker.setResourceUriMapper(resourceUriMapper);
        authorizationChecker.setPolicyCheckerFactory(policyCheckerFactory);
        return authorizationChecker;
    }

}
