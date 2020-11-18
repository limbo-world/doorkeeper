/*
 *
 *  *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.limbo.doorkeeper.server.utils.tuple;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Brozen
 * @date 2019/7/4 11:09 AM
 * @email brozen@qq.com
 */
@Data
public class Tuple3<A, B, C> implements Serializable {

    private A a;

    private B b;

    private C c;

    public Tuple3() {
    }

    public Tuple3(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public int hashCode() {
        return ((a == null ? 0 : a.hashCode()) * 31 + (b == null ? 0 : b.hashCode())) * 31 + (c == null ? 0 : c.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tuple3)) {
            return false;
        }

        Tuple3 other = (Tuple3) obj;
        return equals(this.a, other.a) && equals(this.b, other.b) && equals(this.c, other.c);

    }

    private boolean equals(Object obj1, Object obj2) {
        return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
    }
}
