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

package org.limbo.doorkeeper.admin.session.support;

/**
 * @author Devil
 * @date 2020/11/23 8:18 PM
 */
public class SessionException extends RuntimeException {

    private static String msg = "无有效会话";

    public SessionException() {
        super(msg);
    }

    public SessionException(String msg) {
        super(msg);
    }

    public SessionException(Throwable cause) {
        super(msg, cause);
    }

    public SessionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
