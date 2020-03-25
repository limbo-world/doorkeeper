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

package org.limbo.authc.api.interfaces.beans.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Brozen
 * @date 2020/2/27 5:41 PM
 * @email brozen@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("l_menu")
public class MenuPO extends BasePO {

    private static final long serialVersionUID = 5009205082401921221L;

    @TableId(type = IdType.INPUT)
    private String menuCode;

    private String menuName;

    private String menuDesc;

    private String icon;

    private Integer sort;

    private String parentMenuCode;

    /**
     * 默认菜单不能删除
     */
    private Boolean isDefault;

}