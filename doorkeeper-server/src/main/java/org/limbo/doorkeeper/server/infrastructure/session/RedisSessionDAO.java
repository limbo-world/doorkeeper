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

package org.limbo.doorkeeper.server.infrastructure.session;

import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.dto.vo.SessionUser;
import org.limbo.utils.jackson.JacksonUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author Devil
 * @since 2020/11/24 10:04 AM
 */
public class RedisSessionDAO extends AbstractSessionDAO<SessionUser> {

    private final RedissonClient redissonClient;

    private final String prefix;

    public RedisSessionDAO(RedissonClient redissonClient) {
        this(redissonClient, 2, TimeUnit.HOURS);
    }

    public RedisSessionDAO(RedissonClient redissonClient, long expiry, TimeUnit timeUnit) {
        super(expiry, timeUnit);
        this.redissonClient = redissonClient;
        this.prefix = "Doorkeeper-Session-";
    }

    @Override
    public void touchSession(String sessionId) {
        redissonClient.getBucket(getSessionPrefix() + sessionId).expire(sessionExpiry, sessionExpiryUnit);
    }

    @Override
    public void save(SessionUser session) {
        String sessionId = session.getSessionId();
        redissonClient.getBucket(getSessionPrefix() + sessionId)
                .set(JacksonUtils.toJSONString(session), sessionExpiry, sessionExpiryUnit);
    }

    @Override
    protected SessionUser create(String sessionId, SessionUser sessionAccount) {
        sessionAccount.setSessionId(sessionId);
        return sessionAccount;
    }

    @Override
    protected SessionUser read(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            return null;
        }

        String sessionJson = (String) redissonClient.getBucket(getSessionPrefix() + sessionId).get();
        if (StringUtils.isBlank(sessionJson)) {
            return null;
        }

        return JacksonUtils.parseObject(sessionJson, SessionUser.class);
    }

    @Override
    protected SessionUser destroy(String sessionId) {
        RBucket<String> bucket = redissonClient.getBucket(getSessionPrefix() + sessionId);
        String sessionJson = bucket.get();
        if (StringUtils.isBlank(sessionJson)) {
            return null;
        }
        bucket.delete();
        return JacksonUtils.parseObject(sessionJson, SessionUser.class);
    }

    @Override
    protected String getSessionPrefix() {
        return prefix;
    }
}
