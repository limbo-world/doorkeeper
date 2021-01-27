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

import org.limbo.doorkeeper.api.constants.Logic;

/**
 * @author Devil
 * @date 2021/1/27 7:19 下午
 */
public class LogicChecker {

    /**
     * 检测是否满足条件
     * @param all 全部待检测条件数量
     * @param satisfied 满足条件的数量
     */
    public static boolean isSatisfied(Logic logic, int all, int satisfied) {
        switch (logic) {
            case ALL:
                return all == satisfied;
            case ONE:
                return satisfied > 0;
            case MORE:
                return satisfied > all - satisfied;
            default:
                throw new IllegalArgumentException("无效的判断逻辑");
        }
    }

}
