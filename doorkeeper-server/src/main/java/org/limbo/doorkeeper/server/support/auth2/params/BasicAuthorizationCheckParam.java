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

package org.limbo.doorkeeper.server.support.auth2.params;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author brozen
 * @date 2021/1/18
 */
@Data
@Accessors(chain = true)
public class BasicAuthorizationCheckParam<T> implements AuthorizationCheckParam<T> {

    /**
     * 进行权限校验时，发起校验的用户ID
     */
    private Long userId;

    /**
     * 进行权限校验时，发起校验的用户所属委托方
     */
    private Long clientId;

    /**
     * 进行权限校验时的参数
     */
    private Map<String, String> params;

    /**
     * 进行权限校验时Resource的约束，可以是String(资源名称、资源URI)或Map (资源tag)
     */
    private List<T> resourceAssigner;

}
