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
import org.aspectj.lang.reflect.MethodSignature;
import org.limbo.doorkeeper.server.dao.ProjectLogMapper;
import org.limbo.doorkeeper.server.entity.ProjectLog;
import org.limbo.doorkeeper.server.utils.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Author Devil
 * @Date 2020/12/1 11:23 上午
 */
@Aspect
@Order
@Slf4j
public class PLogAspect {

    @Autowired
    private ProjectLogMapper projectLogMapper;

    /**
     * 声明切点 带有 @PLog 注解的方法
     */
    @Pointcut("@annotation(org.limbo.doorkeeper.server.support.plog.PLog)")
    public void logMethod() {
    }

    @Around("logMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PLog pLog = method.getAnnotation(PLog.class);

        Object[] params = joinPoint.getArgs();
        Annotation[][] annotations = method.getParameterAnnotations();
        String[] parameterNames = signature.getParameterNames();

        Long projectId = null;
        Long accountId = null;
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof PLogParam) {
                PLogParam PLogParam = (PLogParam) param;
                projectId = PLogParam.getProjectId();
                accountId = PLogParam.getAccountId();
            } else {
                Annotation[] paramAnn = annotations[i];
                for (Annotation annotation : paramAnn) {
                    if (annotation.annotationType().equals(PLogTag.class)) {
                        PLogTag pLogTag = (PLogTag) annotation;
                        switch (pLogTag.value()) {
                            case PLogConstants.PROJECT_ID:
                                projectId = (Long) param;
                                break;
                            case PLogConstants.ACCOUNT_ID:
                                accountId = (Long) param;
                                break;
                            case PLogConstants.CONTENT:
                                content.append(parameterNames[i]).append(" : ").append(JacksonUtil.toJSONString(param)).append("\n");
                                break;
                        }
                        break;
                    }
                }
            }
        }

        ProjectLog projectLog = new ProjectLog();
        projectLog.setProjectId(projectId);
        projectLog.setAccountId(accountId);
        projectLog.setContent(content.toString());
        projectLog.setOperateType(pLog.operateType());
        projectLog.setBusinessType(pLog.businessType());
        projectLogMapper.insert(projectLog);

        return joinPoint.proceed(params);
    }
}
