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

package org.limbo.doorkeeper.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.limbo.doorkeeper.server.constants.BusinessType;
import org.limbo.doorkeeper.server.constants.OperateType;

import java.util.Date;

/**
 * @Author Devil
 * @Date 2020/11/30 7:46 下午
 */
@Data
@TableName("l_project_log")
public class ProjectLog {

    @TableId(type = IdType.AUTO)
    private Long projectLogId;
    /**
     * 项目
     */
    private Long projectId;
    /**
     * 操作人
     */
    private Long accountId;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 操作类型
     */
    private String operateType;
    /**
     * 内容
     */
    private String content;

    private Date gmtCreated;

    private Date gmtModified;
}
