/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.api.dto.param.update;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.limbo.doorkeeper.api.dto.param.add.ResourceTagAddParam;
import org.limbo.doorkeeper.api.dto.param.add.ResourceUriAddParam;

import java.util.List;

/**
 * @author Devil
 * @since 2021/1/5 4:48 下午
 */
@Data
public class ResourceUpdateParam {

    @Schema(description ="名称")
    private String name;

    @Schema(description ="描述")
    private String description;

    @Schema(description ="是否启用")
    private Boolean isEnabled;

    @Schema(description ="资源uri")
    private List<ResourceUriAddParam> uris;

    @Schema(description ="资源标签")
    private List<ResourceTagAddParam> tags;

    @Schema(description ="父资源ID")
    private List<Long> parentIds;

    @Schema(description ="父资源名称")
    private List<String> parentNames;

}
