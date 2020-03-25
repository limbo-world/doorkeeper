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

package org.limbo.authc.admin.support.config;

import lombok.extern.slf4j.Slf4j;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.exception.AuthcException;
import org.limbo.authc.api.interfaces.utils.verify.ParamException;
import org.limbo.authc.session.SessionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

/**
 * 异常信息统一处理类
 * 
 * @author Devil
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

	@ExceptionHandler(SQLException.class)
	public ModelAndView handSql(Exception ex){
		log.info("SQL Exception {}", ex.getMessage());
		ModelAndView mv = new ModelAndView();
		mv.addObject("message", ex.getMessage());
		mv.setViewName("/sql_error.html");
		return mv;
	}

	@ExceptionHandler(value = { ParamException.class })
	public Response handVerify(ParamException e) {
		log.info("校验异常 {}", e.getMessage());
		return new Response(e.getMessage());
	}

	@ExceptionHandler(value = { SessionException.class })
	public Response handSession(SessionException e) {
		return Response.unauthenticated(e.getMessage());
	}

    @ExceptionHandler(value = { AuthcException.class })
    public Response handAuthc(AuthcException e) {
        return Response.unauthorized(e.getMessage());
    }

	@ExceptionHandler(value = { Throwable.class })
	public Response unknownException(Throwable e) {
		log.error("系统异常: {}", e);
		return Response.serviceError(e.getMessage());
	}

}