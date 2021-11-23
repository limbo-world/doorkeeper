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

package org.limbo.doorkeeper;

import org.junit.Test;
import org.limbo.doorkeeper.server.infrastructure.utils.EasyAntPathMatcher;

/**
 * @Author Devil
 * @since 2020/12/7 4:24 下午
 */
public class MatcherTest {

    @Test
    public void t() {
        EasyAntPathMatcher matcher = new EasyAntPathMatcher();
        System.out.println(matcher.match("/account/{\\d+}", "/account/123"));
        System.out.println(matcher.match("/account/{\\d+}", "/account/ccc"));
        System.out.println(matcher.match("/account/{id:\\d+}", "/account/123"));
        System.out.println(matcher.match("/account/{id:\\d+}", "/account/ccc"));
    }
}
