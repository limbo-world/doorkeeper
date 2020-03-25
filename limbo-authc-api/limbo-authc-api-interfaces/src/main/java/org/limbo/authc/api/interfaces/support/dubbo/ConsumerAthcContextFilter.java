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

package org.limbo.authc.api.interfaces.support.dubbo;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.limbo.authc.api.interfaces.constants.DubboContants;
import org.limbo.authc.api.interfaces.support.spring.SpringBeanContext;

/**
 * @author Brozen
 * @date 2020/3/11 4:15 PM
 * @email brozen@qq.com
 */
//@Activate(group = CommonConstants.CONSUMER)
@Activate
public class ConsumerAthcContextFilter extends ListenableFilter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        invocation.setAttachment(
                DubboContants.Attachments.ADMIN_CERTIFICATE,
                SpringBeanContext.getProperty("authc.api.certificate")
        );
        invocation.setAttachment(
                DubboContants.Attachments.PROJECT_ID,
                SpringBeanContext.getProperty("authc.project-id")
        );
        invocation.setAttachment(
                DubboContants.Attachments.PROJECT_CODE,
                SpringBeanContext.getProperty("authc.project-code")
        );
        invocation.setAttachment(
                DubboContants.Attachments.PROJECT_SECRET,
                SpringBeanContext.getProperty("authc.project-secret")
        );

        return invoker.invoke(invocation);
    }
}
