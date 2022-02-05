package org.limbo.doorkeeper.core.domain.entity;

import lombok.Data;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
@Data
public class User {

    /**
     * 是否启用
     */
    private boolean isEnabled;

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isDisabled() {
        return !isEnabled;
    }

}
