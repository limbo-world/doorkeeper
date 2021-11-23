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

package org.limbo.doorkeeper.api.model.param.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 授权校验参数
 *
 * @author brozen
 * @date 2021/1/14
 */
@Data
@Accessors(chain = true)
public class ResourceCheckParam {

    @NotNull(message = "请选择委托方")
    @Parameter(description = "进行权限校验时，资源所属委托方", required = true)
    private Long clientId;

    @Parameter(description = "资源ID列表")
    private List<Long> resourceIds;

    @Parameter(description = "资源名称列表, 精确查询")
    private List<String> names;

    @Parameter(description = "标签 k=v 同时满足才返回")
    private List<String> andTags;

    @Parameter(description = "标签 k=v 满足一个就返回")
    private List<String> orTags;

    @Parameter(description = "uri列表 method=uri 如 get=/api/test 或者 /api/test")
    private List<String> uris;

    @Parameter(description = "是否返回标签")
    private Boolean needTag;

    @Parameter(description = "是否返回uri")
    private Boolean needUri;

    @Parameter(description = "是否返回父资源ID")
    private Boolean needParentId;

    @Parameter(description = "是否返回子资源ID")
    private Boolean needChildrenId;

}
