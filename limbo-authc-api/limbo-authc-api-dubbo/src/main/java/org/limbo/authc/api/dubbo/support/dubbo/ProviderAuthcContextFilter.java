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

package org.limbo.authc.api.dubbo.support.dubbo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.limbo.authc.api.interfaces.apis.ProjectApi;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.po.ProjectPO;
import org.limbo.authc.api.interfaces.constants.DubboContants;
import org.limbo.authc.core.dao.ProjectMapper;
import org.limbo.authc.api.interfaces.utils.verify.ParamException;
import org.limbo.authc.api.interfaces.support.spring.SpringBeanContext;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dubbo全局拦截器，用于读取隐式参数ProjectId、处理异常
 *
 * 如果是第三方项目，修改其传入的ProjectId为它自己的ProjectId
 * 如果是Admin项目，如果传入了ProjectId就用传入的，否则用Admin的ProjectId
 *
 * @author Brozen
 * @date 2020/3/9 4:22 PM
 * @email brozen@qq.com
 */
@Activate(group = { CommonConstants.PROVIDER })
public class ProviderAuthcContextFilter extends ListenableFilter {

    public ProviderAuthcContextFilter() {
        super.listener = new AuthcExceptionListener();
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 只有admin项目能操作project接口
        if (invoker.getInterface() == ProjectApi.class) {
            if (isAdminProjectInvoke(invocation)) {
                return invoker.invoke(invocation);
            } else {
                return AsyncRpcResult.newDefaultAsyncResult(Response.unauthorized("无权执行此操作！"), invocation);
            }
        } else {
            return invokeOtherApi(invoker, invocation);
        }
    }

    /**
     * 调用非ProjectApi接口时需要校验projectId、projectCode隐式参数是否存在，不存在禁止访问
     */
    private Result invokeOtherApi(Invoker<?> invoker, Invocation invocation) throws RpcException {

        Long projectId = StringUtils.isBlank(invocation.getAttachment(DubboContants.Attachments.PROJECT_ID)) ? null
                : Long.valueOf(invocation.getAttachment(DubboContants.Attachments.PROJECT_ID));
        String projectCode = invocation.getAttachment(DubboContants.Attachments.PROJECT_CODE);
        String secret = invocation.getAttachment(DubboContants.Attachments.PROJECT_SECRET);

        ProjectPO project = getProject(projectId, projectCode);
        if (project == null) {
            return AsyncRpcResult.newDefaultAsyncResult(Response.unauthorized("未知的项目: project不存在!"), invocation);
        }
        if (!project.getProjectSecret().equals(secret)) {
            return AsyncRpcResult.newDefaultAsyncResult(Response.unauthorized("无权执行此操作！"), invocation);
        }

        // 将这个参数放入dubbo中，某些服务会用到
        RpcContext.getContext().setAttachment(DubboContants.Attachments.PROJECT_ID, String.valueOf(project.getProjectId()));
        RpcContext.getContext().setAttachment(DubboContants.Attachments.PROJECT_CODE, project.getProjectCode());

        if (isAdminProjectInvoke(invocation)) {
            // 如果是admin项目的话，可以在参数中使用别的项目的projectId
            for (Object arg : invocation.getArguments()) {
                if ((arg instanceof Param)) {
                    Param param = (Param) arg;
                    // 如果参数中不存在projectId 则用admin项目的
                    if (param.getProjectId() == null) {
                        param.setProjectId(project.getProjectId());
                    }
                }
            }
        } else {
            // 如果是第三方项目 只能使用其自己的projectId 从dubbo配置中获取
            for (Object arg : invocation.getArguments()) {
                if ((arg instanceof Param)) {
                    Param param = (Param) arg;
                    param.setProjectId(project.getProjectId());
                }
            }

        }

        return invoker.invoke(invocation);
    }

    /**
     * 是否是admin项目进行调用
     * 调用ProjecApi接口需要校验是否为admin项目调用，其他项目禁止调用ProjectApi中的接口；
     * 通过隐式参数中的密码来确定是否admin项目
     */
    private boolean isAdminProjectInvoke(Invocation invocation) {
        String remoteCertificate = invocation.getAttachment(DubboContants.Attachments.ADMIN_CERTIFICATE);
        String localCertificate = SpringBeanContext.getProperty("authc.api.certificate");
        return StringUtils.isBlank(localCertificate) || StringUtils.equals(localCertificate, remoteCertificate);
    }

    private ProjectPO getProject(Long projectId, String projectCode) {
        ProjectMapper projectMapper = SpringBeanContext.getBeanByType(ProjectMapper.class).get();
        Objects.requireNonNull(projectMapper);

        if (projectId == null) {
            if (StringUtils.isBlank(projectCode)) {
                return null;
            }
            return projectMapper.selectOne(Wrappers.<ProjectPO>lambdaQuery().eq(ProjectPO::getProjectCode, projectCode.trim()));
        } else {
            return projectMapper.selectById(projectId);
        }
    }

    /**
     * 异常处理，针对ParamException和其他RuntimeException处理，封装为对应code的消息来返回，
     * 受检异常和Error暂不处理
     */
    @Slf4j
    static class AuthcExceptionListener implements Listener {

        @Override
        public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
            if (!appResponse.hasException()) {
                return;
            }

            Throwable exception = appResponse.getException();
            if (exception instanceof ParamException) {
                appResponse.setValue(Response.paramError(exception.getMessage()));
                appResponse.setException(null);
            } else if (exception instanceof ConstraintViolationException) {
                String errorMsg = parseValidationException((ConstraintViolationException) exception);
                appResponse.setValue(Response.paramError(errorMsg));
                appResponse.setException(null);
            } else if (exception instanceof RuntimeException) {
                appResponse.setValue(Response.serviceError(exception.getMessage()));
                log.error("Dubbo服务异常 " + invoker.getInterface().getName() + "." + invocation.getMethodName(), exception);
            }
        }

        @Override
        public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {

        }

        private String parseValidationException(ConstraintViolationException e) {
            Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

            // key-错误字段路径 value-错误信息
            Map<String, String> errorMap = violations.stream()
                    .collect(Collectors.toMap(
                            violation -> violation.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    ));

            // 错误信息拼接得到的字符串
            return StringUtils.join(
                    violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()),
                    ",");
        }
    }

}
