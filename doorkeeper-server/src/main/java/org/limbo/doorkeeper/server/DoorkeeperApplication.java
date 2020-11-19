package org.limbo.doorkeeper.server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

/**
 * @author liuqingtong
 * @date 2020/11/19 19:29
 */
@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(classes = Service.class))
public class DoorkeeperApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(DoorkeeperApplication.class)
                .web(WebApplicationType.SERVLET)
                .build()
                .run(args);
    }

}
