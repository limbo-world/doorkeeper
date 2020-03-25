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

package org.limbo.authc.api.dubbo.test;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.limbo.authc.api.interfaces.apis.AccountApi;
import org.limbo.authc.api.interfaces.apis.ProjectApi;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.api.interfaces.beans.vo.CaptchaVO;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Brozen
 * @date 2020/3/5 3:00 PM
 * @email brozen@qq.com
 */
@EnableDubbo
@SpringBootApplication(scanBasePackageClasses = DubboConsumerTest.class)
public class DubboConsumerTest {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DubboConsumerTest.class)
                .web(WebApplicationType.SERVLET)
                .build().run(args);
    }



    @RestController
    @RequestMapping("/ping")
    public static class PingController {

        @Reference(version = "1.0.0", filter = {"consumerAuthcFilter"})
        private AccountApi accountApi;

        @Reference(version = "${dubbo.service.version}", filter = {"consumerAuthcFilter"})
        private ProjectApi projectApi;

        @GetMapping
        public Response<AccountVO> ping() {
            return accountApi.get(new AccountVO.GetParam(1L, 1L));
        }

    }

}
