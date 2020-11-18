/*
 *
 *  *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.limbo.doorkeeper.server.constants;

/**
 * 单独设置权限时的策略
 *
 * @author Brozen
 * @date 2020/3/10 3:55 PM
 * @email brozen@qq.com
 */
public enum PermissionAuthcPolicies implements IEnum<String> {

    /**
     * 允许通过
     */
    ALLOWED("ALLOWED"),

    /**
     * 不允许通过
     */
    REFUSED("REFUSED"),
    ;

    private String policy;

    PermissionAuthcPolicies(String policy) {
        this.policy = policy;
    }

    @Override
    public String getValue() {
        return policy;
    }

    public static PermissionAuthcPolicies parse(String p) {
        for (PermissionAuthcPolicies policy : values()) {
            if (policy.is(p)) {
                return policy;
            }
        }
        return null;
    }
}
