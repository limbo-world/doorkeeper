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

/**
 * API请求 业务码
 *
 * @author Brozen
 * @date 2020/3/5 10:51 AM
 * @email brozen@qq.com
 */
public enum ResponseCode {

    OK(200),

    PARAM_ERROR(400), // 参数异常 bad request
    UNAUTHENTICATED(401), // 请求要求用户的身份认证
    FORBIDDEN(403), // 权限不够
    NOT_FOUND(404),

    SERVICE_ERROR(500),;


    private int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
