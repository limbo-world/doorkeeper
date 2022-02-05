package org.limbo.doorkeeper.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.UriMethod;
import org.limbo.doorkeeper.core.domain.component.path.PathMatcher;
import org.limbo.doorkeeper.core.domain.component.path.SinglePathMatcher;

/**
 * @author yuansheng
 * @since 2022/2/2
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Uri {

    private Long id;

    private UriMethod method;

    private String pattern;

    private PathMatcher pathMatcher;

    public boolean match(UriMethod method, String path) {
        return (UriMethod.ALL == method || this.method == method)
                && SinglePathMatcher.getMather().match(StringUtils.trim(pattern), StringUtils.trim(path));
    }
}
