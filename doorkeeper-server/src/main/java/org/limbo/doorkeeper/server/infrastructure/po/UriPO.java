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

package org.limbo.doorkeeper.server.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.limbo.doorkeeper.api.constants.UriMethod;

/**
 * @author Devil
 * @date 2021/4/25 4:42 下午
 */
@Data
@TableName("uri")
public class UriPO {

    @TableId(type = IdType.AUTO)
    private Long uriId;

    private Long realmId;

    private Long clientId;
    /**
     * ant 风格
     */
    private String uri;
    /**
     * 请求方法
     */
    private UriMethod method;

}
