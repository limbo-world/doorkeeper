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
import org.limbo.authc.api.interfaces.beans.po.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/2/28 10:06 AM
 * @email brozen@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleVO extends RolePO {

    private static final long serialVersionUID = -2910888810564311931L;

    /**
     * 角色关联的菜单code
     */
    private List<String> menuCodes;

    /**
     * 角色关联的菜单
     */
    private List<MenuPO> menus;

    /**
     * 角色的权限策略
     */
    private List<RolePermissionPolicyPO> permPolicies;

    /**
     * 角色授权的账户
     */
    private List<AccountPO> accounts;

    /**
     * 角色新增参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AddParam extends Param {
        private static final long serialVersionUID = -2362043767668494939L;

        @NotBlank(message = "角色名不可为空！")
        private String roleName;

        private String roleDesc;
        private List<String> menuCodeList;
        private List<PermissionPolicyVO> permPolicies;
    }

    /**
     * 角色更新参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateParam extends Param {
        private static final long serialVersionUID = -4388175432777294579L;

        @NotNull(message = "角色不存在!")
        private Long roleId;

        @NotBlank(message = "角色名不可为空！")
        private String roleName;

        private String roleDesc;
        private List<String> menuCodeList;
        private List<PermissionPolicyVO> permPolicies;
    }

    /**
     * 删除参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class DeleteParam extends Param {
        private static final long serialVersionUID = 199013535979245750L;

        @NotNull(message = "角色不存在!")
        private Long roleId;

        public DeleteParam(Long projectId, @NotNull(message = "角色不存在!") Long roleId) {
            super(projectId);
            this.roleId = roleId;
        }
    }

    /**
     * 获取参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class GetParam extends DeleteParam {
        private static final long serialVersionUID = 6218665913487187427L;

        public GetParam(Long projectId, @NotNull(message = "角色不存在!") Long roleId) {
            super(projectId, roleId);
        }
    }
}
