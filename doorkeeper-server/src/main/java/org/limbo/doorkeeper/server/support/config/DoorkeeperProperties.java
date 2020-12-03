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

package org.limbo.doorkeeper.server.support.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author Brozen
 * @date 2020/3/11 10:12 AM
 * @email brozen@qq.com
 */
@Data
@ConfigurationProperties(prefix = "doorkeeper")
public class DoorkeeperProperties {

    private Session session = new Session();

    private Sign sign = new Sign();

    private Long projectId;

    private String projectSecret;


    @Data
    public static class Session {
        /**
         * 会话header name
         */
        private String headerName = "Doorkeeper-Token";

        /**
         * 回话有效期
         */
        private Duration sessionDuration = Duration.ofHours(1);
    }


    @Data
    public static class Sign {

        /**
         * 签名header name
         */
        private String headerName = "Doorkeeper-Sign";

        /**
         * 参数分隔符
         */
        private String separator = "---doorkeeper---";

        /**
         * 时间戳误差，毫秒
         */
        private Long timestampMistake = 1000L;
    }

}
