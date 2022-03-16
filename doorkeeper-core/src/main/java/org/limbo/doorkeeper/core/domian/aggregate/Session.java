package org.limbo.doorkeeper.core.domian.aggregate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.apache.commons.lang3.time.DateUtils;
import org.limbo.doorkeeper.api.constants.ApiConstants;

import java.util.Date;

/**
 * @author yuansheng
 * @since 2022/2/6
 */
@Getter
@Builder
@ToString
public class Session {

    private String sessionId;

    private String secret;

    /**
     * 会话上下文
     */
    private String context;

    /**
     * 过期时间 秒
     */
    private int expiry;

    @Tolerate
    private Session() {
    }

    public String token() {
        return JWT.create().withIssuer(ApiConstants.ISSUER)
                .withClaim("context", context)
                .withExpiresAt(DateUtils.addSeconds(new Date(), expiry))  //设置过期时间
                .sign(Algorithm.HMAC256(secret));
    }

}
