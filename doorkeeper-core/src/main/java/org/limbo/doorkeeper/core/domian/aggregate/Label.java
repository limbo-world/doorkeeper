package org.limbo.doorkeeper.core.domian.aggregate;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yuansheng
 * @since 2022/2/7
 */
@Getter
@Builder
@ToString
public class Label {

    protected Long id;

    protected String key;

    protected String value;

    @Tolerate
    private Label() {
    }

    public boolean match(String key, String value) {
        return StringUtils.equals(this.key, key) && StringUtils.equals(this.value, value);
    }
}
