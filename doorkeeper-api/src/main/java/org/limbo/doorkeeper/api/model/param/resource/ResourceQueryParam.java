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

package org.limbo.doorkeeper.api.model.param.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.vo.ResourceVO;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/5 4:48 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceQueryParam extends Page<ResourceVO> {

    private Long realmId;

    private Long clientId;

    @Schema(description ="资源ID")
    private List<Long> resourceIds;

    @Schema(description = "名称列表，精确查询")
    private List<String> names;

    @Schema(description = "名称，模糊查询")
    private String dimName;

    @Schema(description ="是否启用")
    private Boolean isEnabled;

    @Schema(description = "uri列表，精确查询")
    private List<String> uris;

    @Schema(description = "uri，模糊查询")
    private String dimUri;

    @Schema(description = "k=v形式，精确查询")
    private List<String> kvs;

    @Schema(description = "标签名，精确查询")
    private String k;

    @Schema(description = "标签名，模糊查询")
    private String dimK;

    @Schema(description = "标签值，精确查询")
    private String v;

    @Schema(description = "标签值，模糊查询")
    private String dimV;

}
