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

package org.limbo.authc.admin.beans.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.limbo.authc.api.interfaces.beans.po.BasePO;

/**
 * @author Brozen
 * @date 2020/3/23 9:44 AM
 * @email brozen@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("laa_business_log")
public class AdminBLogPO extends BasePO {

    private static final long serialVersionUID = -8549457421249618187L;

    @TableId(type = IdType.AUTO)
    private Long logId;

    private String projectName;

    private Long accountId;

    private String accountNick;

    private String logName;

    private String logType;

    private String ip;

    private String param;

    private String session;

}
