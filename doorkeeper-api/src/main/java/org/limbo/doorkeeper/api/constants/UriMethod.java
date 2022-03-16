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

package org.limbo.doorkeeper.api.constants;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author Devil
 * @since 2021/1/11 4:25 下午
 */
public enum UriMethod implements IEnum<String> {
    UNKNOWN, ALL, GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE, CONNECT;

    @Override
    public String getValue() {
        return toString();
    }

    public static UriMethod parse(String p) {
        for (UriMethod value : values()) {
            if (value.getValue().equalsIgnoreCase(p)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
