/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.api.model.param.auth;

import java.util.List;
import java.util.Map;

/**
 * 授权校验参数
 *
 * @author brozen
 * @date 2021/1/14
 * @param <T> 通过不同方式约束资源查询类型，T的类型不同。根据资源名称或资源URI约束时，T为String，参考{@link}。根据资源tag约束时，T为Map<String, String>，参考{@link}。
 */
public interface AuthorizationCheckParam<T> {

    /**
     * 进行权限校验时，需要校验的用户ID
     */
    Long getUserId();

    /**
     * 进行权限校验时，资源所属委托方
     */
    Long getClientId();

    /**
     * 获取进行权限校验时的参数
     */
    Map<String, String> getParams();

    /**
     * 获取授权校验Resource的约束，可以是String(资源名称、资源URI)或Map<String,String>(资源tag)
     */
    List<T> getResourceAssigner();

}
