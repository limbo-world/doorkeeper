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

/**
 * API请求错误码
 *
 * @author Brozen
 * @date 2020/3/5 10:51 AM
 * @email brozen@qq.com
 */
public enum ResponseCode {

    OK(200),

    PARAM_ERROR(400),
    UNAHTENTICATED(401),
    UNAUTHORIZED(403),
    NOT_FOUND(404),

    SERVICE_ERROR(500),
    ;


    private int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
