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

package org.limbo.authc.admin.support.authc;

/**
 * 授权校验参数，需要提供项目ID\code、登录账户ID。
 * 项目ID\code由权限管理后台创建项目后生成，可在项目的配置文件中保存；
 *      进行授权认证时，优先使用projectId
 * 登录账户ID应该保存在会话中，由项目进行管理，授权服务不管理会话；
 *
 * @author devil
 * @date 2020/3/9
 */
public interface AuthcInfo {

    /**
     * 项目ID
     */
    Long getProjectId();

    /**
     * 项目code
     */
    String getProjectCode();

    /**
     * 登录账户ID
     */
    Long getAccountId();

}
