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

package org.limbo.doorkeeper.server.support.auth2;

import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.PermissionResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceUriMapper;
import org.limbo.doorkeeper.server.service.PermissionService;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.limbo.doorkeeper.server.support.auth2.params.AuthorizationCheckParam;
import org.limbo.doorkeeper.server.support.auth2.params.BasicAuthorizationCheckParam;
import org.limbo.doorkeeper.server.support.auth2.policies.PolicyCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author brozen
 * @date 2021/1/18
 */
@Component
public class AuthorizationCheckerFactory {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceUriMapper resourceUriMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private PolicyCheckerFactory policyCheckerFactory;

    /**
     * 创建一个根据资源名进行授权校验的checker
     * @param checkParam {@link BasicAuthorizationCheckParam}
     * @param <P> Map<String, String>
     */
    public <P extends AuthorizationCheckParam<String>> AuthorizationChecker<P, String> newNameAuthorizationChecker(P checkParam) {
        NameAuthorizationChecker<P> checker = new NameAuthorizationChecker<>(checkParam);
        setSpringBeans(checker);
        checker.setResourceMapper(resourceMapper);
        return checker;
    }

    /**
     * 创建一个根据资源uri进行授权校验的checker
     * @param checkParam {@link BasicAuthorizationCheckParam}
     * @param <P> Map<String, String>
     */
    public <P extends AuthorizationCheckParam<String>> AuthorizationChecker<P, String> newUriAuthorizationChecker(P checkParam) {
        UriAuthorizationChecker<P> checker = new UriAuthorizationChecker<>(checkParam);
        setSpringBeans(checker);
        checker.setResourceMapper(resourceMapper);
        checker.setResourceUriMapper(resourceUriMapper);
        return checker;
    }

    /**
     * 创建一个根据资源tag进行授权校验的checker
     * @param checkParam {@link BasicAuthorizationCheckParam}
     * @param <P> Map<String, String>
     */
    public <P extends AuthorizationCheckParam<Map<String, String>>> AuthorizationChecker<P, Map<String, String>> newTagAuthorizationChecker(P checkParam) {
        TagAuthorizationChecker<P> checker = new TagAuthorizationChecker<>(checkParam);
        setSpringBeans(checker);
        return checker;
    }

    /**
     * 设置{@link AbstractAuthorizationChecker}中需要的Spring Bean
     */
    private void setSpringBeans(AbstractAuthorizationChecker<?, ?> checker) {
        checker.setPermissionService(permissionService);
        checker.setPolicyService(policyService);
        checker.setClientMapper(clientMapper);
        checker.setPermissionResourceMapper(permissionResourceMapper);
        checker.setPolicyCheckerFactory(policyCheckerFactory);
    }

}