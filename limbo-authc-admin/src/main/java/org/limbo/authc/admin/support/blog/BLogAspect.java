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

package org.limbo.authc.admin.support.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.limbo.authc.admin.beans.po.AdminBLogPO;
import org.limbo.authc.admin.service.AdminBLogService;
import org.limbo.authc.admin.support.autoconfig.AuthcProperties;
import org.limbo.authc.admin.support.session.AdminSession;
import org.limbo.authc.admin.support.session.RedisSessionDAO;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.support.spring.SpringBeanContext;
import org.limbo.authc.session.SessionAccount;
import org.limbo.authc.session.utils.HiJackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brozen
 * @date 2020/3/23 9:38 AM
 * @email brozen@qq.com
 */
@Aspect
@Order
@Slf4j
public class BLogAspect {

    private LogNameExpressionEvaluator evaluator = new LogNameExpressionEvaluator();

    @Autowired
    private AuthcProperties authcProperties;

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    /**
     * 声明切点，org.limbo.authc.admin.controller 包下所有类中，带有 @BLog 注解的方法
     */
    @Pointcut("@annotation(org.limbo.authc.admin.support.blog.BLog)")
    public void logMethod() {
    }

    @Around("logMethod()")
    public Object around(ProceedingJoinPoint jointPoint) throws Throwable {

        Object[] args = jointPoint.getArgs();
        Response<?> apiResult = ((Response<?>) jointPoint.proceed(args));
        // 返回状态非200时不处理
        if (!apiResult.ok()) {
            return apiResult;
        }

        Object result = apiResult.getData();
        try {
            Object root = jointPoint.getTarget();
            MethodSignature signature = (MethodSignature) jointPoint.getSignature();
            Method method = signature.getMethod();
            BLog blog = method.getAnnotation(BLog.class);
            if (blog != null) {
                // 尝试读取session
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                AdminSession session = Optional.of(request)
                        .map(req -> req.getHeader(authcProperties.getSession().getHeaderName()))
                        .map(id -> redisSessionDAO.readSessionMayNull(id)).orElse(null);

                BLogRecordContext logContext = new BLogRecordContext(blog, root, method, args, result, request, session);
                CompletableFuture.runAsync(() -> recordBLog(logContext))
                        .exceptionally(e -> {
                            log.error("业务日志切面中发生异常：", e);
                            return null;
                        });
            }
        } catch (Throwable e) {
            log.error("业务日志切面中发生异常：", e);
        }

        return apiResult;
    }

    private void recordBLog(BLogRecordContext logContext) {
        BLog blog = logContext.getBLog();
        Object[] args = logContext.getArgs();

        AdminBLogPO record = new AdminBLogPO();
        record.setIp(getIp(logContext.getRequest()));

        // 记录会话信息、操作用户信息
        if (logContext.getSession() != null) {
            SessionAccount account = logContext.getSession().getAccount();
            record.setProjectId(account.getCurrentProjectId());
            record.setProjectName(account.getCurrentProjectName());
            record.setAccountId(account.getAccountId());
            record.setAccountNick(account.getAccountNick());

            if (blog.recordSession()) {
                record.setSession(HiJackson.toJson(args));
            }
        } else {
            throw new IllegalStateException("会话不存在！");
        }

        record.setLogName(resolveLogName(logContext));
        record.setLogType(blog.type().name());

        if (blog.recordParam() && args != null) {
            record.setParam(HiJackson.toJson(args));
        }


        try {
            SpringBeanContext.getBeanByType(AdminBLogService.class)
                    .orElseThrow(() -> new IllegalStateException("找不到 AdminBLogMapper"))
                    .addLog(record);
        } catch (Throwable e) {
            log.error("保存日志失败！", e);
        }
    }

    /**
     * 处理业务日志名称，优先使用value，其次name，最后考虑使用expression计算；
     * expression是一个SpEL，默认的参数有：
     *      args        参数数组
     *      arg0 ~ arg3 参数数组的对应下标的元素
     *      session     会话对象AdminSession
     *      account     会话中的SessionAccount
     *      result      接口方法调用的返回值是Response类型，这个result是Response中的data
     */
    private String resolveLogName(BLogRecordContext logContext) {
        BLog blog = logContext.getBLog();
        if (StringUtils.isNotBlank(blog.value())) {
            return blog.value();
        }

        if (StringUtils.isNotBlank(blog.name())) {
            return blog.name();
        }

        if (StringUtils.isNotBlank(blog.expression())) {
            EvaluationContext evaluationContext = logContext.toEvaluationContext();
            return evaluator.name(blog.expression(), logContext, logContext.method, evaluationContext);
        }

        throw new IllegalStateException("无法解析日志name！");
    }


    @Data
    @AllArgsConstructor
    private class BLogRecordContext {
        private BLog bLog;
        private Object target;  // 调用method的对象
        private Method method;
        private Object[] args;
        private Object result;  // 返回值

        private HttpServletRequest request;
        private AdminSession session;

        private EvaluationContext toEvaluationContext() {
            MethodBasedEvaluationContext evaluationContext
                    = new MethodBasedEvaluationContext(this, method, args, new DefaultParameterNameDiscoverer());
            evaluationContext.setVariable("args", args);
            evaluationContext.setVariable("session", session);
            evaluationContext.setVariable("account", session == null ? null : session.getAccount());
            evaluationContext.setVariable("result", result);

            for (int i = 0; i < args.length; i++) {
                if (i > 3) {
                    break;
                }
                evaluationContext.setVariable("arg" + i, args[i]);
            }
            return evaluationContext;
        }
    }


    private String getIp(HttpServletRequest request) {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        } else {
            return request.getHeader("x-forwarded-for");
        }
    }

}
