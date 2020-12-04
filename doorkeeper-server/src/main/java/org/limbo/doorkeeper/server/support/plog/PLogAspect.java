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
import org.limbo.doorkeeper.server.support.config.DoorkeeperProperties;
import org.limbo.doorkeeper.server.support.session.AbstractSessionDAO;
import org.limbo.doorkeeper.server.support.session.SessionAccount;
import org.limbo.doorkeeper.server.utils.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    protected AbstractSessionDAO sessionDAO;

    @Autowired
    protected DoorkeeperProperties doorkeeperProperties;

    /**
     * 声明切点 带有 @PLog 注解的方法
     */
    @Pointcut("@annotation(org.limbo.doorkeeper.server.support.plog.PLog)")
    public void logMethod() {
    }

    @Around("logMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = joinPoint.proceed(joinPoint.getArgs());
        doLog(joinPoint);
        return proceed;
    }

    private void doLog(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            PLog pLog = method.getAnnotation(PLog.class);

            Object[] params = joinPoint.getArgs();
            Annotation[][] annotations = method.getParameterAnnotations();
            String[] parameterNames = signature.getParameterNames();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            String token = request.getHeader(doorkeeperProperties.getSession().getHeaderName());
            SessionAccount account = sessionDAO.readSession(token).getAccount();

            Long projectId = account.getCurrentProject().getProjectId();
            Long accountId = account.getAccountId();
            StringBuilder content = new StringBuilder();

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                Annotation[] paramAnn = annotations[i];
                for (Annotation annotation : paramAnn) {
                    if (annotation.annotationType().equals(PLogTag.class)) {
                        PLogTag pLogTag = (PLogTag) annotation;
                        switch (pLogTag.value()) {
                            case PLogConstants.CONTENT:
                                content.append(parameterNames[i]).append(" : ").append(JacksonUtil.toJSONString(param)).append("\n");
                                break;
                        }
                        break;
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
        } catch (Exception e) {
            log.error("操作日志插入失败", e);
        }
    }
}
