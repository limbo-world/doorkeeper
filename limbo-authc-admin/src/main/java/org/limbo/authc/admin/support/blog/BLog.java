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

package org.limbo.authc.admin.support.blog;

import java.lang.annotation.*;

/**
 * @author Brozen
 * @date 2020/3/23 9:25 AM
 * @email brozen@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BLog {

    /**
     * 与name相同，优先使用value
     */
    String value() default "";

    /**
     * 与value相同，优先使用value
     */
    String name() default "";

    /**
     * 使用公式计算的日志名，优先使用value、其次name、最后expression
     */
    String expression() default "";

    BLogType type() default BLogType.RETRIEVE;

    boolean recordParam() default true;

    boolean recordSession() default true;

}
