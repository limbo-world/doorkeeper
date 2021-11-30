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

package org.limbo.doorkeeper.server.adapter.http.config;

import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.dto.vo.ResponseVO;
import org.limbo.doorkeeper.server.infrastructure.exception.ParamException;
import org.limbo.doorkeeper.server.infrastructure.exception.AuthorizationException;
import org.limbo.doorkeeper.server.infrastructure.exception.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Devil
 * @since 2020/11/24 10:43 AM
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = { BindException.class })
    public ResponseVO handBind(BindException e) {
        return ResponseVO.paramError(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseVO handBind(MethodArgumentNotValidException e) {
        return ResponseVO.paramError(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(value = { ParamException.class })
    public ResponseVO handVerify(ParamException e) {
        log.info("参数异常 {}", e.getMessage());
        return ResponseVO.paramError(e.getMessage());
    }

    @ExceptionHandler(value = { AuthenticationException.class })
    public ResponseVO handAuthentication(AuthenticationException e) {
        return ResponseVO.unauthenticated(e.getMessage());
    }

    @ExceptionHandler(value = { AuthorizationException.class })
    public ResponseVO handAuthorization(AuthorizationException e) {
        return ResponseVO.unauthorized(e.getMessage());
    }

    @ExceptionHandler(value = { Throwable.class })
    public ResponseVO unknownException(Throwable e) {
        log.error("系统异常: {}", e);
        return ResponseVO.serviceError(e.getMessage());
    }
}
