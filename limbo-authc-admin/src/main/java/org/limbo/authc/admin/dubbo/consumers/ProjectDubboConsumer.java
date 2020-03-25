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
import org.limbo.authc.api.interfaces.apis.ProjectApi;
import org.springframework.stereotype.Component;

/**
 * @author devil
 * @date 2020/3/10
 */
@Component
public class ProjectDubboConsumer {

    @Delegate
    @Reference(version = "1.0.0", filter = {"consumerAuthcFilter"})
    private ProjectApi projectApi;

}
