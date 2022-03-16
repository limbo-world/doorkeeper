package org.limbo.doorkeeper.core.factory;

import org.limbo.doorkeeper.core.domian.aggregate.Session;
import org.limbo.utils.strings.UUIDUtils;
import org.limbo.utils.verifies.Verifies;

/**
 * @author yuansheng
 * @since 2022/2/6
 */
public class SessionFactory {

    public static Session create(String context, String secret, int expiry) {
        Verifies.verify(expiry <= 0, "过期时间必须为正数");

        return Session.builder()
                .sessionId(UUIDUtils.randomID())
                .context(context)
                .secret(secret)
                .expiry(expiry)
                .build();
    }
}
