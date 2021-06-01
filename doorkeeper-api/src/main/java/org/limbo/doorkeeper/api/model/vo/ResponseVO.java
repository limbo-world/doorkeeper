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

package org.limbo.doorkeeper.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = -7844608116500392515L;

    @Schema(description = "返回码")
    private int code;

    @Schema(description = "返回描述")
    private String msg;

    @Schema(description = "返回数据")
    private T data;

    public ResponseVO(T data) {
        this(ResponseCode.OK, "success", data);
    }

    public ResponseVO(ResponseCode code, T data) {
        this(code.code(), "success", data);
    }

    public ResponseVO(ResponseCode code, String msg) {
        this(code.code(), msg, null);
    }

    public ResponseVO(ResponseCode code, String msg, T data) {
        this(code.code(), msg, data);
    }

    public ResponseVO(int code, String msg, T data) {
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
    public static <T> ResponseVO<T> success() {
        return new ResponseVO<>(null);
    }

    /**
     * 调用正常
     */
    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>(data);
    }

    /**
     * 参数错误
     */
    public static <T> ResponseVO<T> paramError(String msg) {
        return new ResponseVO<>(ResponseCode.PARAM_ERROR, msg);
    }

    /**
     * 未认证，未登录
     */
    public static <T> ResponseVO<T> unauthenticated(String msg) {
        return new ResponseVO<>(ResponseCode.UNAUTHENTICATED, msg);
    }

    /**
     * 未授权，无权限
     */
    public static <T> ResponseVO<T> unauthorized(String msg) {
        return new ResponseVO<>(ResponseCode.FORBIDDEN, msg);
    }

    /**
     * 服务器内部错误
     */
    public static <T> ResponseVO<T> serviceError(String msg) {
        return new ResponseVO<>(ResponseCode.SERVICE_ERROR, msg);
    }
}
