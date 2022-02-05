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

package org.limbo.doorkeeper.common.constant;

/**
 * @author Devil
 * @since 2021/1/10 10:40 上午
 */
public interface DoorkeeperConstants {

    String DOORKEEPER_REALM_NAME = "doorkeeper";

    String REALM = "realm";

    String REALM_ID = "realmId";

    String NAMESPACE_ID = "namespaceId";

    String API_CLIENT = "api";
    /**
     * 默认ID
     */
    Long DEFAULT_ID = 0L;
    /**
     * 查询域角色时候使用
     */
    Long REALM_ROLE_ID = 0L;

    String ADMIN = "admin";

    String TYPE = "type";

    String USER_ID = "userId";
    String USERNAME = "username";
    String NICKNAME = "nickname";


    String TREE = "tree";

    /**
     * 父子结构中最大的层级，防止环的情况出现死循环
     */
    Integer HIERARCHY = 10;

    /**
     * 未授权情况下是否拒绝，未授权是指，资源找不到对应的Permission
     */
    boolean REFUSE_WHEN_UNAUTHORIZED = true;
}
