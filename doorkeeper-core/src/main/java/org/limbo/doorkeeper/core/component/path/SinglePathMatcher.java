package org.limbo.doorkeeper.core.component.path;

/**
 * @author yuansheng
 * @since 2022/2/5
 */
public class SinglePathMatcher {

    private static PathMatcher pathMatcher;

    public static boolean match(String pattern, String path) {
        if (pathMatcher == null) {
            pathMatcher = new AntPathMatcher();
        }
        return pathMatcher.match(pattern, path);
    }

}
