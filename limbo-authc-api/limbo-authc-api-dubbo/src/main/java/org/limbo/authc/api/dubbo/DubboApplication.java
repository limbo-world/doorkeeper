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

package org.limbo.authc.api.dubbo;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.limbo.authc.api.interfaces.support.spring.SpringBeanContext;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Brozen
 * @date 2020/3/5 10:48 AM
 * @email brozen@qq.com
 */
@SpringBootApplication(scanBasePackages = {"org.limbo.authc.core"})
@EnableNacosConfig
@EnableTransactionManagement
@MapperScan("org.limbo.authc.core.dao")
@EnableDubbo(scanBasePackages = "org.limbo.authc.api.dubbo.providers")
public class DubboApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DubboApplication.class)
                .web(WebApplicationType.NONE)
                .build()
                .run(args);
    }

    @Configuration
    public static class Config {

        @Bean
        @Lazy(false)
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public SpringBeanContext springBeanContext () {
            return new SpringBeanContext();
        }


        @Bean
        public PaginationInterceptor paginationInterceptor() {
            return new PaginationInterceptor();
        }

    }

}
