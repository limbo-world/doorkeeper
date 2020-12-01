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

package org.limbo.doorkeeper.server.support.plog;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author Devil
 * @Date 2020/12/1 5:20 下午
 */
@Data
@AllArgsConstructor
public class PLogParam {
    /**
     * 当前项目
     */
    private Long projectId;
    /**
     * 当前账户
     */
    private Long accountId;
}
