///*
// * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
// *
// *   Licensed under the Apache License, Version 2.0 (the "License");
// *   you may not use this file except in compliance with the License.
// *   You may obtain a copy of the License at
// *
// *   	http://www.apache.org/licenses/LICENSE-2.0
// *
// *   Unless required by applicable law or agreed to in writing, software
// *   distributed under the License is distributed on an "AS IS" BASIS,
// *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *   See the License for the specific language governing permissions and
// *   limitations under the License.
// */
//
//package org.limbo.doorkeeper.server.support.config;
//
//import org.limbo.doorkeeper.server.support.session.RedisSessionDAO;
//import org.redisson.api.RedissonClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * @author liuqingtong
// * @date 2020/11/24 16:14
// */
//@Configuration
////@AutoConfigureAfter(RedisConfig.class)
//public class SessionConfig {
//
//    @Bean
//    public RedisSessionDAO sessionDAO(RedissonClient redissonClient) {
//        return new RedisSessionDAO(redissonClient, 7, TimeUnit.DAYS);
//    }
//
//}
