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

package org.limbo.doorkeeper.server.useless;

import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.core.domain.component.path.AntPathMatcher;

/**
 * @author Devil
 * @since 2021/6/1 3:46 下午
 */
public class PatternMatcher {

    private static final ThreadLocal<AntPathMatcher> PATH_MATCHER = ThreadLocal.withInitial(AntPathMatcher::new);

    /**
     * ant风格的路径pattern
     */
    private final String pattern;

    public PatternMatcher(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 判断path是否符合ant风格的pattern
     *
     * @param path    访问的路径
     * @return 是否匹配
     */
    public boolean pathMatch(String path) {
        AntPathMatcher antPathMatcher = PATH_MATCHER.get();
        return antPathMatcher.match(StringUtils.trim(pattern), StringUtils.trim(path));
    }

}
