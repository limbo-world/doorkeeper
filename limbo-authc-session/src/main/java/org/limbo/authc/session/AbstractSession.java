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

package org.limbo.authc.session;

import lombok.Data;

/**
 * @author devil
 * @date 2019/9/18
 */
@Data
public abstract class AbstractSession {

    protected String sessionId;

    protected SecurityDigest securityDigest;

    public AbstractSession() {} // 用于json

    public AbstractSession(String sessionId, SecurityDigest securityDigest) {
        this.sessionId = sessionId;
        this.securityDigest = securityDigest;
    }

}
