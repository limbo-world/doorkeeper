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

package org.limbo.authc.api.interfaces.beans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.po.MenuPO;
import org.limbo.authc.api.interfaces.beans.po.PermissionPO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/2/28 8:26 AM
 * @email brozen@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuVO extends MenuPO {

    private static final long serialVersionUID = 2153593439343309469L;

    private List<PermissionPO> permissions;

    /**
     * 新增菜单参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AddParam extends Param {
        private static final long serialVersionUID = -1696793496927087665L;

        // 如果未设置会自动生成一个
        private String menuCode;

        @NotBlank(message = "菜单名称不可为空")
        private String menuName;

        private String menuDesc;
        private String icon;
        private Integer sort;
        private String parentMenuCode;
        private List<String> permCodeList;
    }

    /**
     * 更新菜单参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateParam extends Param {
        private static final long serialVersionUID = -7662011453586911990L;

        @NotBlank(message = "菜单不存在")
        private String menuCode;

        @NotBlank(message = "菜单名称不可为空")
        private String menuName;

        private String menuDesc;
        private Integer sort;
        private String icon;
        private List<String> permCodeList;
    }

    /**
     * 删除参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class DeleteParam extends Param {
        private static final long serialVersionUID = -6603601161875933812L;

        @NotBlank(message = "菜单不存在")
        private String menuCode;

        public DeleteParam(Long projectId, @NotBlank(message = "菜单不存在") String menuCode) {
            super(projectId);
            this.menuCode = menuCode;
        }
    }

    /**
     * 获取参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class GetParam extends DeleteParam {
        private static final long serialVersionUID = 3000951379770315323L;

        public GetParam(Long projectId, @NotBlank(message = "菜单不存在") String menuCode) {
            super(projectId, menuCode);
        }
    }

    /**
     * 更新排序参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateSortParam extends Param {
        private static final long serialVersionUID = -4045652470592185895L;

        @NotEmpty(message = "未指定排序")
        private List<MenuSort> sorts;
    }

    @Data
    public static class MenuSort implements Serializable {
        private static final long serialVersionUID = 5054232745953446520L;

        private String menuCode;
        private Integer sort;
    }
}
