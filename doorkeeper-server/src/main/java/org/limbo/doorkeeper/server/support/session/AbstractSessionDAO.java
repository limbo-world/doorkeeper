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

package org.limbo.doorkeeper.server.support.session;

import org.apache.commons.lang3.time.DateUtils;
import org.limbo.doorkeeper.api.model.vo.SessionUser;
import org.limbo.doorkeeper.server.support.session.exception.AuthenticationException;
import org.limbo.doorkeeper.server.utils.UUIDUtils;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Devil
 * @date 2020/11/23 8:17 PM
 */
public abstract class AbstractSessionDAO<T extends SessionUser> {

    protected long sessionExpiry = 5;

    protected TimeUnit sessionExpiryUnit = TimeUnit.DAYS;

    public AbstractSessionDAO(long expiry, TimeUnit timeUnit) {
        if (expiry <= 0) {
            throw new IllegalArgumentException("过期时间必须为正数！");
        }
        Objects.requireNonNull(timeUnit);
        this.sessionExpiry = expiry;
        this.sessionExpiryUnit = timeUnit;
    }

    /**
     * 创建会话，
     */
    public T createSession(SessionUser sessionUser) {
        try {
            // 会话用户
            T session = create(UUIDUtils.get(), sessionUser);

            // 保存会话
            save(session);

            return session;
        } catch (Exception e) {
            throw new AuthenticationException("会话创建失败！", e);
        }
    }

    /**
     * 读取会话，如果会话不存在将返回null
     */
    public T readSessionMayNull(String sessionId) {
        T session;
        try {
            session = read(sessionId);
        } catch (Exception e) {
            throw new AuthenticationException("读取会话失败！", e);
        }
        return session;
    }

    /**
     * 读取会话，如果会话不存在将抛出异常
     */
    public T readSession(String sessionId) {
        T session = readSessionMayNull(sessionId);
        if (session == null) {
            throw new AuthenticationException("会话不存在");
        }
        return session;
    }

    /**
     * 销毁会话
     */
    public T destroySession(String sessionId) {
        try {
            return destroy(sessionId);
        } catch (Exception e) {
            throw new AuthenticationException("销毁会话失败！", e);
        }
    }

    Date getExpiredSessionLastAccessTime() {
        return DateUtils.addSeconds(new Date(), (int) sessionExpiryUnit.toSeconds(sessionExpiry));
    }

    /**
     * 重置会话过期时间
     */
    public abstract void touchSession(String sessionId);

    /**
     * 更新会话内容
     */
    public void refreshSession(T session) {
        save(session);
    }

    /**
     * 更新会话内容
     */
    public abstract void save(T session);


    /**
     * 需自行实现，可以在子类实现中使用扩展的AbstractSession的创建
     */
    protected abstract T create(String sessionId, SessionUser sessionUser);

    /**
     * 需自行实现，可以实现自定义Session反序列化，可实现在Session中存储自定义信息
     */
    protected abstract T read(String sessionId);

    /**
     * 在调用父类destroy之前、之后可以实现资源释放等操作
     */
    protected abstract T destroy(String sessionId);

    /**
     * 自定义会话缓存key的前缀，可以区分多可项目的会话
     */
    protected abstract String getSessionPrefix();
}
