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
import org.limbo.authc.api.interfaces.beans.po.ProjectPO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Brozen
 * @date 2020/3/7 4:33 PM
 * @email brozen@qq.com
 */
public class ProjectVO extends ProjectPO {

    private static final long serialVersionUID = 274806927465172440L;

    @Override
    public Boolean getIsDeleted() {
        return super.getIsDeleted();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class QueryParam extends Page<ProjectVO> {
        private static final long serialVersionUID = 2610097273222456034L;
        private String projectName;
    }

    /**
     * 项目创建参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AddParam extends ProjectVO {
        private static final long serialVersionUID = 3686479678783374466L;

        private String projectCode;

        @NotBlank(message = "项目名称不可为空")
        private String projectName;

        private String projectDesc;
    }

    /**
     * 项目更新参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateParam extends AddParam {
        private static final long serialVersionUID = -3014765323071543721L;

        @NotNull(message = "项目不存在")
        private Long projectId;
    }

    /**
     * 删除参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class DeleteParam extends Param {
        private static final long serialVersionUID = -7598660325937017383L;

        @NotNull(message = "项目不存在")
        private Long projectId;

        public DeleteParam(@NotNull(message = "项目不存在") Long projectId) {
            super(projectId);
            this.projectId = projectId;
        }
    }

    /**
     * 获取参数
     */
    @NoArgsConstructor
    public static class GetParam extends DeleteParam {
        private static final long serialVersionUID = -7314010773387154754L;

        public GetParam(@NotNull(message = "项目不存在") Long projectId) {
            super(projectId);
        }
    }

}
