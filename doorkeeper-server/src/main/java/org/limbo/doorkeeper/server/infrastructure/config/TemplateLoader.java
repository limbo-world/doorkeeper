package org.limbo.doorkeeper.server.infrastructure.config;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Devil
 * @since 2021/9/30
 */
public class TemplateLoader {

    private volatile String template;

    private final String path;

    public TemplateLoader(String path) {
        this.path = path;
    }

    /**
     * 获取模板
     */
    public String getTemplate() throws Exception {
        if (template == null) {
            synchronized (this) {
                if (template == null) {
                    InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
                    template = IOUtils.toString(Objects.requireNonNull(resourceAsStream), StandardCharsets.UTF_8);
                }
            }
        }
        return template;
    }

    public String getPath() {
        return path;
    }
}
