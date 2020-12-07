/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.support.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.server.support.authc.AuthenticationInterceptor;
import org.limbo.doorkeeper.server.support.format.StringToDateConverter;
import org.limbo.doorkeeper.server.support.plog.PLogAspect;
import org.limbo.doorkeeper.server.support.session.RedisSessionDAO;
import org.limbo.doorkeeper.server.support.session.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2020/11/24 11:49 AM
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RedisSessionDAO sessionDAO;

    @Autowired
    private DoorkeeperProperties doorkeeperProperties;

    /**
     * json 返回结果处理
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化
        // Include.Include.ALWAYS 默认
        // Include.NON_DEFAULT 属性为默认值不序列化
        // Include.NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化，则返回的json是没有这个字段的。这样对移动端会更省流量
        // Include.NON_NULL 属性为NULL 不序列化,就是为null的字段不参加序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        objectMapper.setTimeZone( GMT+8 ) 时区偏移设置，如果不指定的话时间和北京时间会差八个小时
        return objectMapper;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter(StringToDateConverter.YMD_HMS_FORMAT));
    }

    /**
     * 用于记录业务日志
     */
    @Bean
    public PLogAspect pLogAspect() {
        return new PLogAspect();
    }

    /**
     * MyBatisPlus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor(doorkeeperProperties, sessionDAO);
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        List<String> adminPath = new ArrayList<>();
        adminPath.add("/project/**");
        adminPath.add("/project-account/**");
        return new AuthenticationInterceptor(doorkeeperProperties, sessionDAO, adminPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor())
                .excludePathPatterns("/login/**")
                .excludePathPatterns("/error");

        registry.addInterceptor(authenticationInterceptor())
                .excludePathPatterns("/login/**")
                .excludePathPatterns("/session/**")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/api-docs/**")
                .excludePathPatterns("/api-docs.html")
                .excludePathPatterns("/error");

    }

}
