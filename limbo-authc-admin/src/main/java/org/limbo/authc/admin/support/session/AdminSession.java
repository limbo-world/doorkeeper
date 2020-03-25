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

package org.limbo.authc.admin.support.session;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.limbo.authc.session.AbstractSession;
import org.limbo.authc.session.SecurityDigest;
import org.limbo.authc.session.SessionAccount;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminSession extends AbstractSession {

    private SessionAccount account;

    public AdminSession() {
    }

    public AdminSession(String sessionId, SecurityDigest securityDigest, SessionAccount sessionAccount) {
        super(sessionId, securityDigest);
        this.account = sessionAccount;
    }
}