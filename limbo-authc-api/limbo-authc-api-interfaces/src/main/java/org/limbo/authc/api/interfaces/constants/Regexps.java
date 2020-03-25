/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.api.interfaces.constants;

import java.util.regex.Pattern;

/**
 * 常用正则
 *
 * @author Brozen
 * @date 2020/3/6 8:27 AM
 * @email brozen@qq.com
 */
public interface   Regexps {

    /**
     * 中国大陆手机号
     * 13x 14x 15x 16x 17x 18x 19x 网段
     */
    String MOBILE_PHONE_CN = "^1(3|4|5|6|7|8|9)\\d{9}$";

    /**
     * 邮箱地址
     */
    String EMAIL = "[a-zA-Z0-9_\\.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+";


    public static void main(String[] args) {
        String s = "15757777777";
        Pattern pattern = Pattern.compile(MOBILE_PHONE_CN);

        System.out.println(Pattern.matches(MOBILE_PHONE_CN, s));
    }
}
