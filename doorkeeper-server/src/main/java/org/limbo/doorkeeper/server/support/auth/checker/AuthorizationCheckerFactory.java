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

import org.limbo.doorkeeper.api.model.param.auth.AuthorizationCheckParam;
import org.limbo.doorkeeper.server.dal.dao.PermissionDao;
import org.limbo.doorkeeper.server.dal.dao.PolicyDao;
import org.limbo.doorkeeper.server.dal.dao.ResourceDao;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.PermissionResourceMapper;
import org.limbo.doorkeeper.server.dal.mapper.ResourceUriMapper;
import org.limbo.doorkeeper.server.support.auth.policies.PolicyCheckerFactory;
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
    private PermissionDao permissionDao;

    @Autowired
    private PolicyDao policyDao;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private ResourceUriMapper resourceUriMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private PolicyCheckerFactory policyCheckerFactory;

    /**
     * 创建一个根据资源名进行授权校验的checker
     * @param checkParam
     * @param <P> Map<String, String>
     */
    public <P extends AuthorizationCheckParam<String>> AuthorizationChecker<P, String> newNameAuthorizationChecker(P checkParam) {
        NameAuthorizationChecker<P> checker = new NameAuthorizationChecker<>(checkParam);
        setSpringBeans(checker);
        checker.setResourceDao(resourceDao);
        return checker;
    }

    /**
     * 创建一个根据资源uri进行授权校验的checker
     * @param checkParam
     * @param <P> Map<String, String>
     */
    public <P extends AuthorizationCheckParam<String>> AuthorizationChecker<P, String> newUriAuthorizationChecker(P checkParam) {
        UriAuthorizationChecker<P> checker = new UriAuthorizationChecker<>(checkParam);
        setSpringBeans(checker);
        checker.setResourceDao(resourceDao);
        checker.setResourceUriMapper(resourceUriMapper);
        return checker;
    }

    /**
     * 创建一个根据资源tag进行授权校验的checker
     * @param checkParam
     * @param <P> Map<String, String>
     */
    public <P extends AuthorizationCheckParam<Map<String, String>>> AuthorizationChecker<P, Map<String, String>> newTagAuthorizationChecker(P checkParam) {
        TagAuthorizationChecker<P> checker = new TagAuthorizationChecker<>(checkParam);
        setSpringBeans(checker);
        checker.setResourceDao(resourceDao);
        return checker;
    }

    /**
     * 设置{@link AbstractAuthorizationChecker}中需要的Spring Bean
     */
    private void setSpringBeans(AbstractAuthorizationChecker<?, ?> checker) {
        checker.setPermissionDao(permissionDao);
        checker.setPolicyDao(policyDao);
        checker.setClientMapper(clientMapper);
        checker.setPermissionResourceMapper(permissionResourceMapper);
        checker.setPolicyCheckerFactory(policyCheckerFactory);
    }

}
