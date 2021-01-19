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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.vo.AuthorizationCheckResult;
import org.limbo.doorkeeper.server.dao.ResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceUriMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.Resource;
import org.limbo.doorkeeper.server.entity.ResourceUri;
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationCheckParam;
import org.limbo.doorkeeper.server.utils.EasyAntPathMatcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author brozen
 * @date 2021/1/18
 */
public class UriAuthorizationChecker<P extends AuthorizationCheckParam<String>> extends AbstractAuthorizationChecker<P, String> {

    private static final ThreadLocal<EasyAntPathMatcher> PATH_MATCHER = ThreadLocal.withInitial(EasyAntPathMatcher::new);

    @Setter
    private ResourceMapper resourceMapper;

    @Setter
    private ResourceUriMapper resourceUriMapper;

    /**
     * client拥有的全部uri资源
     */
    private List<ResourceUri> clientUris;

    public UriAuthorizationChecker(P checkParam) {
        super(checkParam);
    }

    /**
     * {@inheritDoc}<br/>
     *
     * 会在开始检测之前先查询client的全部uri资源
     *
     * @return
     */
    @Override
    public AuthorizationCheckResult<String> check() {
        this.clientUris = resourceUriMapper.selectList(Wrappers.<ResourceUri>lambdaQuery()
                .eq(ResourceUri::getRealmId, client.getRealmId())
                .eq(ResourceUri::getClientId, client.getClientId())
        );

        return super.check();
    }

    /**
     * {@inheritDoc}<br/>
     *
     * 根据URI来找到资源
     *
     * @param uri 资源约束对象，资源URI
     * @return
     */
    @Override
    protected List<Resource> assignCheckingResources(String uri) {
        List<Long> resourceIds = this.clientUris.stream()
                .filter(clientUri -> pathMatch(clientUri.getUri(), uri))
                .map(ResourceUri::getResourceId)
                .collect(Collectors.toList());

        return resourceMapper.selectBatchIds(resourceIds);
    }


    /**
     * 判断path是否符合ant风格的pattern
     * @param pattern ant风格的路径pattern
     * @param path 访问的路径
     * @return 是否匹配
     */
    public boolean pathMatch(String pattern, String path) {
        EasyAntPathMatcher antPathMatcher = PATH_MATCHER.get();
        return antPathMatcher.match(StringUtils.trim(pattern), StringUtils.trim(path));
    }
}
