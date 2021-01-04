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

package org.limbo.doorkeeper.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.limbo.doorkeeper.api.constants.ResponseCode;

import java.io.Serializable;

/**
 * @author Brozen
 * @date 2020/3/5 10:51 AM
 * @email brozen@qq.com
 */
@Data
@NoArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = -7844608116500392515L;

    private int code;

    private String msg;

    private T data;

    public Response(T data) {
        this(ResponseCode.OK, null, data);
    }

    public Response(String msg) {
        this(ResponseCode.PARAM_ERROR, msg, null);
    }

    public Response(ResponseCode code, T data) {
        this(code.code(), null, data);
    }

    public Response(int code, T data) {
        this(code, null, data);
    }

    public Response(ResponseCode code, String msg) {
        this(code.code(), msg, null);
    }

    public Response(int code, String msg) {
        this(code, msg, null);
    }

    public Response(ResponseCode code, String msg, T data) {
        this(code.code(), msg, data);
    }

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 是否为ok状态
     */
    public boolean ok() {
        return this.code == ResponseCode.OK.code();
    }

    /**
     * 调用正常
     */
    public static <T> Response<T> success() {
        return new Response<>(null);
    }

    /**
     * 调用正常
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(data);
    }

    /**
     * 参数错误
     */
    public static <T> Response<T> paramError(String msg) {
        return new Response<>(ResponseCode.PARAM_ERROR, msg);
    }

    /**
     * 未认证，未登录
     */
    public static <T> Response<T> unauthenticated(String msg) {
        return new Response<>(ResponseCode.UNAUTHORIZED, msg);
    }

    /**
     * 未授权，无权限
     */
    public static <T> Response<T> unauthorized(String msg) {
        return new Response<>(ResponseCode.UNAUTHORIZED, msg);
    }

    /**
     * 服务器内部错误
     */
    public static <T> Response<T> serviceError(String msg) {
        return new Response<>(ResponseCode.SERVICE_ERROR, msg);
    }
}
