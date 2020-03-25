/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.admin.support.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.limbo.authc.admin.support.authc.AuthcFailHandler;
import org.limbo.authc.admin.support.authc.AuthcInfoSupplier;
import org.limbo.authc.admin.support.authc.AuthcInterceptor;
import org.limbo.authc.admin.support.autoconfig.AuthcProperties;
import org.limbo.authc.admin.support.blog.BLogAspect;
import org.limbo.authc.admin.support.session.AdminSession;
import org.limbo.authc.admin.support.session.RedisSessionDAO;
import org.limbo.authc.admin.support.session.SessionInterceptor;
import org.limbo.authc.api.interfaces.support.spring.SpringBeanContext;
import org.limbo.authc.session.SessionAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;

/**
 * @author devil
 * @date 2020/3/9
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    @Autowired
    private AuthcProperties authcProperties;

    @Bean
    public SpringBeanContext SpringBeanContext(ApplicationContext applicationContext, Environment environment) {
        SpringBeanContext springBeanContext = new SpringBeanContext();
        springBeanContext.setApplicationContext(applicationContext);
        springBeanContext.setEnvironment(environment);
        return springBeanContext;
    }

    @Bean
    public AuthcInfoSupplier authcInfoSupplier() {
        return (request, response) -> {
            String sessionId = request.getHeader(authcProperties.getSession().getHeaderName());
            AdminSession adminSession = redisSessionDAO.readSessionMayNull(sessionId);
            SessionAccount sessionAccount = adminSession.getAccount();

            String uri = request.getRequestURI();
            if (uri.startsWith("/project")) {
                // 项目API使用admin项目自身的projectId和projectCode
                return new AdminAuthcInfo(
                        authcProperties.getProjectId(),
                        authcProperties.getProjectCode(),
                        sessionAccount.getAccountId()
                );
            } else {
                // 其他API使用会话中选中的projectId
                return new AdminAuthcInfo(sessionAccount.getCurrentProjectId(), null, sessionAccount.getAccountId());
            }
        };
    }

    @Bean
    public AuthcFailHandler authcFailHandler() {
        return (request, response, authcResponse) -> AuthcFailHandler.AuthcFailOperation.REFUSE;
    }

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 会话拦截器
        registry.addInterceptor(sessionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/ping/**")
                .excludePathPatterns("/login/**");

        // 权限拦截器
        registry.addInterceptor(authcInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/ping/**")
                .excludePathPatterns("/session/**")
                .excludePathPatterns("/login/**");
    }

    @Bean
    public SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }

    @Bean
    public AuthcInterceptor authcInterceptor() {
        return new AuthcInterceptor();
    }

    /**
     * 用于记录业务日志
     */
    @Bean
    public BLogAspect bLogAspect() {
        return new BLogAspect();
    }

    @Bean
    @Lazy(false)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SpringBeanContext springBeanContext () {
        return new SpringBeanContext();
    }

    /**
     * MyBatisPlus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
