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

package org.limbo.doorkeeper.server.constants;

import org.limbo.doorkeeper.api.constants.IEnum;

/**
 * @Author Devil
 * @Date 2020/12/1 11:18 上午
 */
public enum BusinessType implements IEnum<String> {

    ACCOUNT("账户"),
    ACCOUNT_ROLE("账户角色"),
    API("Api"),
    PERMISSION_API("权限Api"),
    PERMISSION("权限"),
    PROJECT("项目"),
    ROLE("角色"),
    ROLE_PERMISSION("角色权限"),
    ;

    private String type;

    BusinessType(String type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return type;
    }

    public static BusinessType parse(String p) {
        for (BusinessType type : values()) {
            if (type.is(p)) {
                return type;
            }
        }
        return null;
    }
}
