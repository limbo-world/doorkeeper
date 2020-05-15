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

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.limbo.authc.api.interfaces.constants.DubboContants;
import org.limbo.authc.api.interfaces.support.spring.SpringBeanContext;

import java.util.function.Supplier;

/**
 * @author Brozen
 * @date 2020/3/11 4:15 PM
 * @email brozen@qq.com
 */
//@Activate(group = CommonConstants.CONSUMER)
@Activate
public class ConsumerAthcContextFilter extends ListenableFilter {

    private static Supplier<String> adminCertificateSupplier = () -> SpringBeanContext.getProperty("authc.api.certificate");
    private static Supplier<String> projectIdSupplier = () -> SpringBeanContext.getProperty("authc.project-id");
    private static Supplier<String> projectCodeSupplier = () -> SpringBeanContext.getProperty("authc.project-code");
    private static Supplier<String> projectSecretSupplier = () -> SpringBeanContext.getProperty("authc.project-secret");

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        invocation.setAttachment(
                DubboContants.Attachments.ADMIN_CERTIFICATE,
                adminCertificateSupplier.get()
        );
        invocation.setAttachment(
                DubboContants.Attachments.PROJECT_ID,
                projectIdSupplier.get()
        );
        invocation.setAttachment(
                DubboContants.Attachments.PROJECT_CODE,
                projectCodeSupplier.get()
        );
        invocation.setAttachment(
                DubboContants.Attachments.PROJECT_SECRET,
                projectSecretSupplier.get()
        );

        return invoker.invoke(invocation);
    }

    public static void setAdminCertificateSupplier(Supplier<String> adminCertificateSupplier) {
        ConsumerAthcContextFilter.adminCertificateSupplier = adminCertificateSupplier;
    }

    public static void setProjectIdSupplier(Supplier<String> projectIdSupplier) {
        ConsumerAthcContextFilter.projectIdSupplier = projectIdSupplier;
    }

    public static void setProjectCodeSupplier(Supplier<String> projectCodeSupplier) {
        ConsumerAthcContextFilter.projectCodeSupplier = projectCodeSupplier;
    }

    public static void setProjectSecretSupplier(Supplier<String> projectSecretSupplier) {
        ConsumerAthcContextFilter.projectSecretSupplier = projectSecretSupplier;
    }
}
