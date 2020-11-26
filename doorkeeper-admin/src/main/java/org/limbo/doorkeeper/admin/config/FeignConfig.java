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

package org.limbo.doorkeeper.admin.config;

import feign.RequestInterceptor;
import org.limbo.doorkeeper.admin.session.AdminSession;
import org.limbo.doorkeeper.admin.session.RedisSessionDAO;
import org.limbo.doorkeeper.admin.support.feign.FeignHeaderTransferHystrixConcurrencyStrategy;
import org.limbo.doorkeeper.admin.utils.Strings;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * @author liuqingtong
 * @date 2020/11/25 9:09
 */
@Configuration
@EnableHystrix
@EnableFeignClients(basePackages = "org.limbo.doorkeeper.api.client")
@AutoConfigureAfter(RedisConfig.class)
public class FeignConfig {

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    @Autowired
    private DoorkeeperProperties doorkeeperProperties;

    @Bean("doorkeeperInterceptor")
    public RequestInterceptor getRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(DoorkeeperConstants.PROJECT_HEADER, Strings.valueOf(doorkeeperProperties.getProjectId()));
            requestTemplate.header(DoorkeeperConstants.PROJECT_SECRET_HEADER, doorkeeperProperties.getProjectSecret());
            Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .map(ServletRequestAttributes::getRequest)
                    .map(request -> {
                        String sessionId = request.getHeader(doorkeeperProperties.getSession().getHeaderName());
                        return redisSessionDAO.readSessionMayNull(sessionId);
                    })
                    .map(AdminSession::getAccount)
                    .ifPresent(account -> {
                        requestTemplate.header(DoorkeeperConstants.PROJECT_PARAM_HEADER, Strings.valueOf(account.getCurrentProjectId()));
                        requestTemplate.header(DoorkeeperConstants.ACCOUNT_HEADER, Strings.valueOf(account.getAccountId()));
                    });
        };
    }

    @Bean
    public FeignHeaderTransferHystrixConcurrencyStrategy feignHeaderTransferHystrixConcurrencyStrategy() {
        return new FeignHeaderTransferHystrixConcurrencyStrategy();
    }

}
