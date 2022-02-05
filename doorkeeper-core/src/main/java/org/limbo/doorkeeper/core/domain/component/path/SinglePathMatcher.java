package org.limbo.doorkeeper.core.domain.component.path;

/**
 * @author yuansheng
 * @since 2022/2/5
 */
public class SinglePathMatcher {

    private static PathMatcher pathMatcher;

    public static PathMatcher getMather() {
        if (pathMatcher == null) {
            pathMatcher = new AntPathMatcher();
        }
        return pathMatcher;
    }

}
