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

package org.limbo.authc.api.interfaces.support.spring;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * spring工具类
 *
 * @author Brozen
 * @date 2020/3/9 4:22 PM
 * @email brozen@qq.com
 */
public class SpringBeanContext implements ApplicationContextAware, EnvironmentAware {
    private static ApplicationContext applicationContext;
    private static Environment environment;

    public SpringBeanContext() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanContext.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringBeanContext.environment = environment;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> Optional<T> getBeanByType(Class<T> clazz) {
        Map<String, T> beans = applicationContext.getBeansOfType(clazz);
        if (MapUtils.isNotEmpty(beans)) {
            if (beans.size() > 1) {
                throw new RuntimeException("there are " + beans.size() + " implements for " + clazz);
            } else {
                return Optional.ofNullable(beans.values().iterator().next());
            }
        } else {
            return Optional.empty();
        }
    }

    public static <T> List<T> getBeansByType(Class<T> clazz) {
        Map<String, T> beans = applicationContext.getBeansOfType(clazz);
        if (MapUtils.isNotEmpty(beans)) {
            return new ArrayList<>(beans.values());
        } else {
            return null;
        }
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    public static <T> T getProperty(String key, Class<T> type) {
        return environment.getProperty(key, type);
    }

    public static <T> T getProperty(String key, Class<T> type, T defaultValue) {
        return environment.getProperty(key, type, defaultValue);
    }

}