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


package org.limbo.doorkeeper.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author liuqingtong
 * @date 2020/11/19 19:29
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("org.limbo.doorkeeper.server.dao")
public class DoorkeeperApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(DoorkeeperApplication.class)
                .web(WebApplicationType.SERVLET)
                .build()
                .run(args);
    }

}
