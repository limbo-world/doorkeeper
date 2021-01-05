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

/**
 * @author Devil
 * @date 2020/12/31 3:01 下午
 */
public enum BatchMethod implements IEnum<String> {

    GET, POST, PUT, DELETE;

    @Override
    public String getValue() {
        return toString();
    }

    public static BatchMethod parse(String p) {
        for (BatchMethod value : values()) {
            if (value.is(p)) {
                return value;
            }
        }
        return null;
    }
}
