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

/**
 * 用户登录的时候需要指定realm 登录完以后只能操作对应的realm
 *
 * 无需登录的接口
 * 1. 登录 2. realm 创建 更新 3. admin realm user 能管理哪些realm 4. 管理权限
 * admin user 绑定realm 每个绑定的realm拥有相同权限 所以，一般除了super admin其他admin user只给绑定一个realm
 *
 * 登录后都能访问的接口
 * 1. 会话  当前realm下的这些操作的读取权限 2. client 3. permission 4. policy 5. role
 *
 * 其他接口需要有admin realm
 *
 * @author Devil
 * @date 2021/1/3 6:16 下午
 */
package org.limbo.doorkeeper.server.controller;
