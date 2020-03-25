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

import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 * @date 2020/3/23 11:05 AM
 * @email brozen@qq.com
 */
public class LogNameExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> nameCache = new ConcurrentHashMap<>(64);

    public String name(String express, Object root, Method logMethod, EvaluationContext context) {
        return (String) getExpression(this.nameCache, new AnnotatedElementKey(logMethod, root.getClass()), express).getValue(context);
    }

}
