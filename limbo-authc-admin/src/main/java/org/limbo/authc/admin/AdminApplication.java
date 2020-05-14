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

package org.limbo.authc.admin;

import org.limbo.authc.admin.support.config.WebConfig;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.limbo.authc.admin.support.autoconfig.AuthcProperties;
import org.limbo.authc.admin.support.config.RedisConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Brozen
 * @date 2020/3/9 3:41 PM
 * @email brozen@qq.com
 *
 * -Dserver.port=8995
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableNacosConfig
@EnableTransactionManagement
@EnableConfigurationProperties({AuthcProperties.class})
@ComponentScan(basePackageClasses = AdminApplication.class)
@MapperScan("org.limbo.authc.admin.dao")
@EnableDubbo(scanBasePackages = "org.limbo.authc.admin.dubbo")
@Import({WebConfig.class, RedisConfiguration.class})
public class AdminApplication {

    public static void main(String[] args) {
        // 不使用 Sentinel 还需要注释 SentinelConfiguration
        // 不使用 Sentinel Dashboard可以注释这一行，也可以通过启动参数指定 -Dcsp.sentinel.dashboard.server=localhost:9001
//        System.setProperty("csp.sentinel.dashboard.server", "localhost:9001");

        new SpringApplicationBuilder(AdminApplication.class)
                .web(WebApplicationType.SERVLET)
                .build()
                .run(args);
    }

}
