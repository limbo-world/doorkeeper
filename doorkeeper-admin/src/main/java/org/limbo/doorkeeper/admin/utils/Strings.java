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

package org.limbo.doorkeeper.admin.utils;

/**
 * @author liuqingtong
 * @date 2020/11/25 9:39
 */
public class Strings {

    /**
     * 如果为null，返回空字符串，否则表现与{@linkplain String#valueOf(Object)}一致
     *
     * @see String#valueOf(Object)
     */
    public static String valueOf(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

}
