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

package org.limbo.doorkeeper.admin.session.validate;

import org.limbo.doorkeeper.admin.config.DoorkeeperProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author devil
 * @date 2020/3/11
 */
public class SessionValidatorFactory {

    /**
     * 用来缓存之前的实例
     */
    private static Map<String, SessionValidator> validators = new ConcurrentHashMap<>(16);

    public static SessionValidator create(DoorkeeperProperties doorkeeperProperties, HttpServletRequest request) {
        // 目前只用header校验
        String headerVName = HeaderSessionValidator.class.getName();
        validators.computeIfAbsent(headerVName, k -> new HeaderSessionValidator(doorkeeperProperties));
        return validators.get(headerVName);
    }
}
