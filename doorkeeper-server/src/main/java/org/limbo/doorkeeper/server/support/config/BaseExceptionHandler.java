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

package org.limbo.doorkeeper.server.support.config;

import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.server.support.authc.AuthenticationException;
import org.limbo.doorkeeper.server.support.session.exception.SessionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Devil
 * @date 2020/11/24 10:43 AM
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = { BindException.class })
    public Response handBind(BindException e) {
        return Response.paramError(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public Response handBind(MethodArgumentNotValidException e) {
        return Response.paramError(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(value = { ParamException.class })
    public Response handVerify(ParamException e) {
        log.info("参数异常 {}", e.getMessage());
        return Response.paramError(e.getMessage());
    }

    @ExceptionHandler(value = { SessionException.class })
    public Response handSession(SessionException e) {
        return Response.unauthenticated(e.getMessage());
    }

    @ExceptionHandler(value = { AuthenticationException.class })
    public Response handAuthentication(AuthenticationException e) {
        return Response.unauthorized(e.getMessage());
    }

    @ExceptionHandler(value = { Throwable.class })
    public Response unknownException(Throwable e) {
        log.error("系统异常: {}", e);
        return Response.serviceError(e.getMessage());
    }
}
