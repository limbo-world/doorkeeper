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

package org.limbo.authc.session.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Jackson序列化工具
 *
 * @author Brozen
 * @date 2020/3/9 3:04 PM
 * @email brozen@qq.com
 */
public class HiJackson {

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();

        //在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //在序列化时日期格式默认为 yyyy-MM-dd HH:mm:ss
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.getDeserializationConfig().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        //在序列化时忽略值为 null 的属性
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }


    public static <T> String toJson(T t) {
        Objects.requireNonNull(t);
        ObjectMapper mapper = createMapper();
        try {
            return mapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Jackson序列化失败！type=" + t.getClass().getName(), e);
        }
    }


    public static <T> T fromJson(String json, Class<T> type) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(type);
        ObjectMapper mapper = createMapper();
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Jackson反序列化失败！type=" + type.getName(), e);
        }
    }

}
