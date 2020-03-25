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
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.po.PermissionPO;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/2/27 5:39 PM
 * @email brozen@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionVO extends PermissionPO {

    private static final long serialVersionUID = 1387286935128195975L;

    public List<String> getApiList() {
        String apis = getApis();
        return apis == null || apis.isEmpty()
                ? Collections.emptyList()
                : Arrays.asList(apis.split(","));
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AddParam extends Param {
        private static final long serialVersionUID = -6519826272206554362L;

        // 如果为空会自动生成一个
        private String permCode;

        @NotBlank(message = "权限名称不可为空")
        private String permName;

        private String permDesc;

        private List<String> apiList;

        private Boolean isOnline;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateParam extends Param {
        private static final long serialVersionUID = -6486642202632036427L;

        @NotBlank(message = "权限不存在")
        private String permCode;

        @NotBlank(message = "权限名称不可为空")
        private String permName;

        private String permDesc;

        private List<String> apiList;

        private Boolean isOnline;
    }


    /**
     * 权限查询参数，keyword将like匹配permCode、permName、permDesc
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class QueryParam extends Page<PermissionVO> {
        private static final long serialVersionUID = 7794591188214414657L;

        /**
         * 查询API，like匹配 apis
         */
        private String api;
    }

    /**
     * 删除参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class DeleteParam extends Param {
        private static final long serialVersionUID = -2950064371236887708L;

        @NotBlank(message = "权限不存在")
        private String permCode;

        public DeleteParam(Long projectId, @NotBlank(message = "权限不存在") String permCode) {
            super(projectId);
            this.permCode = permCode;
        }
    }

    /**
     * 获取参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class GetParam extends DeleteParam {
        private static final long serialVersionUID = -257574007763919005L;

        public GetParam(Long projectId, @NotBlank(message = "权限不存在") String permCode) {
            super(projectId, permCode);
        }
    }
}
