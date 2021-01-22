/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Devil
 * @date 2020/12/31 3:51 下午
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long userId;
    /**
     * 每个user绑定到一个realm
     */
    private Long realmId;
    /**
     * realm唯一 登录用
     */
    private String username;

    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 描述
     */
    private String description;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 扩展信息
     */
    private String extend;
    /**
     * 是否启用
     */
    private Boolean isEnabled;

    private Date createTime;

    private Date updateTime;
}
