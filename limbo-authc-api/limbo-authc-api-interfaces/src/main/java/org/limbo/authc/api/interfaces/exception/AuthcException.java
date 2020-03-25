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

package org.limbo.authc.api.interfaces.exception;

/**
 * @author Brozen
 * @date 2020/3/9 2:32 PM
 * @email brozen@qq.com
 */
public class AuthcException extends RuntimeException {

    private static String msg = "授权认证失败";

    public AuthcException() {
        super(msg);
    }

    public AuthcException(String msg) {
        super(msg);
    }

    public AuthcException(Throwable cause) {
        super(msg, cause);
    }

    public AuthcException(String msg, Throwable cause) {
        super(msg, cause);
    }

}