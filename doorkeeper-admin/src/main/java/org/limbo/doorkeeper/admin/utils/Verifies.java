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

package org.limbo.doorkeeper.admin.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;

import java.util.Collection;
import java.util.Objects;

/**
 * 业务校验工具类，校验不通过会抛出ParamException
 *
 * @author Brozen
 * @date 2019/7/29 9:37 AM
 * @email brozen@qq.com
 */
public final class Verifies {

    public static void verify(boolean expression) {
        verify(expression, "Verify Failed!");
    }

    public static void verify(boolean expression, String message) {
        if (!expression) {
            throw new ParamException(message);
        }
    }

    public static void notNull(Object ref) {
        verify(ref != null, "Verify Failed! Reference is null!");
    }

    public static void isNull(Object ref) {
        verify(ref == null, "Verify Failed! Reference is not null!");
    }

    public static void isNull(Object ref, String message) {
        verify(ref == null, message);
    }

    public static void notNull(Object ref, String message) {
        verify(ref != null, message);
    }

    public static void notBlank(String str) {
        verify(StringUtils.isNotBlank(str), "Verify Failed! String is blank!");
    }

    public static void notBlank(String str, String message) {
        verify(StringUtils.isNotBlank(str), message);
    }

    public static void isBlank(String str) {
        verify(StringUtils.isNotBlank(str), "Verify Failed! String is not blank!");
    }

    public static void isBlank(String str, String message) {
        verify(StringUtils.isBlank(str), message);
    }

    public static void notEmpty(Collection<?> collection) {
        verify(CollectionUtils.isNotEmpty(collection), "Verify Failed! Collection is empty!");
    }

    public static void notEmpty(Collection<?> collection, String message) {
        verify(CollectionUtils.isNotEmpty(collection), message);
    }

    public static void equals(Object o1, Object o2) {
        equals(o1, o2, "Verify Failed! Objects is not equal!");
    }

    public static void equals(Object o1, Object o2, String message) {
        verify(Objects.equals(o1, o2), message);
    }

}