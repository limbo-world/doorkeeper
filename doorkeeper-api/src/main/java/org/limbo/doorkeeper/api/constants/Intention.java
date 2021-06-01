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

package org.limbo.doorkeeper.api.constants;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * 执行意图
 *
 * @author Devil
 * @date 2020/12/31 3:01 下午
 */
public enum Intention implements IEnum<String> {

    /**
     * 允许
     */
    ALLOW,

    /**
     * 拒绝
     */
    REFUSE,
    ;

    @Override
    public String getValue() {
        return toString();
    }

    public static Intention parse(String p) {
        for (Intention value : values()) {
            if (value.getValue().equalsIgnoreCase(p)) {
                return value;
            }
        }
        return null;
    }

    public boolean allow() {
        return Intention.ALLOW == this;
    }
}
