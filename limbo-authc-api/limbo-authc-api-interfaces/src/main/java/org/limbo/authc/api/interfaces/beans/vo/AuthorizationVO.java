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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/10 8:58 AM
 * @email brozen@qq.com
 */
public class AuthorizationVO {

    /**
     * 授权接口
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class GrantParam extends Param {
        private static final long serialVersionUID = 9207958954198401661L;

        @NotEmpty(message = "角色不存在!")
        private List<Long> roleIds;

        @NotEmpty(message = "用户不存在")
        private List<Long> accountIds;
    }

    /**
     * 将角色授权给账户的参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class RoleGrantParam extends Param {
        private static final long serialVersionUID = 4254466031132361179L;

        @NotNull(message = "角色不存在!")
        private Long roleId;

        @NotEmpty(message = "用户不存在")
        private List<Long> accountIds;
    }

    /**
     * 对账户授权的参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AccountGrantParam extends Param {
        private static final long serialVersionUID = -7419868705868787204L;

        @NotNull(message = "用户不存在")
        private Long accountId;

        private List<Long> roleIds;
        private List<PermissionPolicyVO> permPolicies;
    }

    /**
     * 检测是否有权限
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PermissionCheckParam extends Param {
        private static final long serialVersionUID = 7707499082996236680L;

        @NotNull(message = "账户不存在")
        private Long accountId;
        @NotBlank(message = "未知的请求类型")
        private String httpMethod;
        @NotBlank(message = "请求路径不存在")
        private String path;
    }

    /**
     * 获取账户拥有权限的菜单
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class GetMenusParam extends Param {
        private static final long serialVersionUID = 4919878947379415223L;

        @NotNull(message = "账户不存在")
        private Long accountId;

        public GetMenusParam(Long projectId, @NotNull(message = "账户不存在") Long accountId) {
            super(projectId);
            this.accountId = accountId;
        }
    }
}
