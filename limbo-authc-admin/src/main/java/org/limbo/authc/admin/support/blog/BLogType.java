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

/**
 * @author Brozen
 * @date 2020/3/23 9:34 AM
 * @email brozen@qq.com
 */
public enum BLogType {

    CREATE("新增"),
    RETRIEVE("查询"),
    UPDATE("修改"),
    DELETE("删除"),

    LOGIN("登录"),
    LOGOUT("注销"),
    ;

    private String text;

    BLogType(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
