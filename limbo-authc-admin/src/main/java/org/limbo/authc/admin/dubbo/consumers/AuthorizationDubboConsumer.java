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

package org.limbo.authc.admin.dubbo.consumers;

import lombok.experimental.Delegate;
import org.apache.dubbo.config.annotation.Reference;
import org.limbo.authc.api.interfaces.apis.AuthorizationApi;
import org.springframework.stereotype.Service;

/**
 * @author Brozen
 * @date 2020/3/12 11:18 AM
 * @email brozen@qq.com
 */
@Service
public class AuthorizationDubboConsumer {

    @Delegate
    @Reference(version = "${dubbo.service.version}", filter = {"consumerAuthcFilter"})
    private AuthorizationApi authorizationApi;

}
