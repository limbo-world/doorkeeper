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

package org.limbo.doorkeeper.api.constants;

/**
 * @author Devil
 * @date 2021/1/10 10:40 上午
 */
public interface DoorkeeperConstants {
    /**
     * token
     */
    String TOKEN_HEADER = "Authorization";

    String DOORKEEPER_REALM_NAME = "doorkeeper";

    String ISSUER = "doorkeeper";

    String REALM = "realm";

    String REALM_ID = "realmId";

    String CLIENT = "client";

    String CLIENT_ID = "clientId";

    Long DEFAULT_ID = 0L;
    /**
     * 查询域角色时候使用
     */
    Long REALM_CLIENT_ID = 0L;

    String ADMIN = "admin";

    String TYPE = "type";

    String USER_ID = "userId";
    String USERNAME = "username";
    String NICKNAME = "nickname";

    String KV_DELIMITER = "=";

    String TREE = "tree";
}
