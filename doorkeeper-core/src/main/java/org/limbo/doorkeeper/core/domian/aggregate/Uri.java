package org.limbo.doorkeeper.core.domian.aggregate;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.UriMethod;
import org.limbo.doorkeeper.core.component.path.SinglePathMatcher;

/**
 * @author yuansheng
 * @since 2022/2/2
 */
@Getter
@Builder
@ToString
public class Uri {

    private Long id;

    private UriMethod method;

    private String pattern;

    @Tolerate
    private Uri() {
    }

    public boolean match(UriMethod method, String path) {
        return (UriMethod.ALL == method || this.method == method)
                && SinglePathMatcher.match(StringUtils.trim(pattern), StringUtils.trim(path));
    }
}
