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

package org.limbo.doorkeeper.infrastructure.exception;

/**
 * 权限异常 无权访问等情况
 *
 * @author Devil
 * @since 2020/11/30 10:46 AM
 */
public class AuthorizationException extends RuntimeException {

    public static final String msg = "无权访问";

    public AuthorizationException() {
        super(msg);
    }

    public AuthorizationException(String msg) {
        super(msg);
    }

    public AuthorizationException(Throwable cause) {
        super(msg, cause);
    }

    public AuthorizationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
