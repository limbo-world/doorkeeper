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
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.po.AccountPO;
import org.limbo.authc.api.interfaces.beans.po.AccountPermissionPolicyPO;
import org.limbo.authc.api.interfaces.beans.po.RolePO;
import org.limbo.authc.api.interfaces.constants.Regexps;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/4 9:38 AM
 * @email brozen@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountVO extends AccountPO {

    private static final long serialVersionUID = -6769644756907425898L;

    /**
     * 账号授权的角色
     */
    private List<RolePO> roles;

    /**
     * 账号配置的授权策略
     */
    private List<AccountPermissionPolicyPO> permPolicies;

    /**
     * 注册用户参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class RegisterParam extends Param {
        private static final long serialVersionUID = -1968168395025294095L;

        @NotBlank(message = "用户名不可为空")
        private String username;
        @NotBlank(message = "密码不可为空")
        private String password;
        @NotBlank(message = "两次输入密码不一致")
        private String confirmPassword;
        @NotBlank(message = "昵称不可为空")
        @Size(max = 32, message = "昵称长度不超过32个字符")
        private String nick;
    }

    /**
     * 检查用户名是否重复
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UsernameCheckParam extends Param {
        private static final long serialVersionUID = -8734341399152173071L;

        @NotBlank(message = "用户名为空！")
        private String username;
    }

    /**
     * 登录参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class LoginParam extends Param {
        private static final long serialVersionUID = 4960299440390496026L;

        private String projectCode;

        @NotBlank(message = "用户名不可为空")
        private String username;
        @NotBlank(message = "密码不可为空")
        private String password;

        private String captcha;

        private String captchaToken;

        private String loginIp;
    }


    /**
     * 更新用户信息
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateParam extends Param {
        private static final long serialVersionUID = -3458953797359458873L;

        @NotNull(message = "用户不存在")
        private Long accountId;

        @NotBlank(message = "昵称不可为空")
        @Size(max = 32, message = "昵称长度不超过32个字符")
        private String nick;
    }

    /**
     * 密码修改参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UpdatePasswordParam extends Param {
        private static final long serialVersionUID = -6814544689722271407L;

        @NotNull(message = "用户不存在")
        private Long accountId;
        @NotBlank(message = "请输入当前密码")
        private String originalPassword;
        @NotBlank(message = "请输入新密码")
        private String newPassword;
        @NotBlank(message = "请确认新密码")
        private String confirmPassword;
    }

    /**
     * 删除账户参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DeleteParam extends Param {
        private static final long serialVersionUID = 2672152740357803076L;

        @NotNull(message = "用户不存在")
        private Long accountId;

        public DeleteParam(Long projectId, @NotNull(message = "用户不存在") Long accountId) {
            super(projectId);
            this.accountId = accountId;
        }
    }

    /**
     * 查询参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class QueryParam extends Page<AccountVO> {
        private static final long serialVersionUID = 3552114932654723602L;

        /**
         * like匹配
         */
        private String username;
        /**
         * like匹配
         */
        private String nick;
    }

    /**
     * 获取单个账户参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class GetParam extends Param {
        private static final long serialVersionUID = -5174127919780975238L;

        /**
         * 等于匹配
         */
        private Long accountId;

        /**
         * 等于匹配
         */
        private String username;

        public GetParam(Long accountId) {
            this.accountId = accountId;
        }

        public GetParam(Long projectId, Long accountId) {
            super(projectId);
            this.accountId = accountId;
        }

        public GetParam(String username) {
            this.username = username;
        }
    }
}
