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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.limbo.authc.api.interfaces.beans.Param;

import javax.validation.constraints.NotBlank;

/**
 * 验证码数据
 *
 * @author Brozen
 * @date 2020/3/4 9:56 AM
 * @email brozen@qq.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {

    /**
     * 验证码token
     */
    private String token;

    /**
     * 验证码图片的base64数据
     */
    private String imageData;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class VerifyParam extends Param {
        private static final long serialVersionUID = 7936489847439913313L;

        @NotBlank(message = "验证码错误")
        private String token;
        @NotBlank(message = "请输入验证码")
        private String captcha;
    }
}
