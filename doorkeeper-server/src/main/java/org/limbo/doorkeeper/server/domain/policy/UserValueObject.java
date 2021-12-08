package org.limbo.doorkeeper.server.domain.policy;

import lombok.Data;

/**
 * @author Devil
 * @since 2021/11/30
 */
@Data
public class UserValueObject {

    private Long userId;

    private Boolean isEnabled;
}
