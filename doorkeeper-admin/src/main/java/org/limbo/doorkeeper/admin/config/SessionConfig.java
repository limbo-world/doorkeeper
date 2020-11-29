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

import org.limbo.doorkeeper.admin.session.RedisSessionDAO;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author liuqingtong
 * @date 2020/11/24 16:14
 */
@Configuration
@EnableConfigurationProperties(DoorkeeperProperties.class)
//@AutoConfigureAfter(RedisConfig.class)
public class SessionConfig {

    @Autowired
    private DoorkeeperProperties doorkeeperProperties;

    @Bean
    public RedisSessionDAO sessionDAO(RedissonClient redissonClient) {
        DoorkeeperProperties.Session sessionProp = doorkeeperProperties.getSession();
        if (sessionProp != null && sessionProp.getSessionDuration() != null) {
            // 如果指定了会话过期时间
            Duration duration = sessionProp.getSessionDuration();
            return new RedisSessionDAO(redissonClient, duration.getSeconds(), TimeUnit.SECONDS);
        } else {
            return new RedisSessionDAO(redissonClient);
        }
    }

}
