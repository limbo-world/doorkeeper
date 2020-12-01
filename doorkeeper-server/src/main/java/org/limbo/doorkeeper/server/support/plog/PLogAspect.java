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

package org.limbo.doorkeeper.server.support.plog;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * @Author Devil
 * @Date 2020/12/1 11:23 上午
 */
@Aspect
@Order
@Slf4j
public class PLogAspect {

    /**
     * 声明切点 带有 @PLog 注解的方法
     */
    @Pointcut("@annotation(org.limbo.doorkeeper.server.support.plog.PLog)")
    public void logMethod() {
    }

    @Around("logMethod()")
    public Object around(ProceedingJoinPoint jointPoint) throws Throwable {
        Object[] args = jointPoint.getArgs();
        return jointPoint.proceed(args);
    }
}
