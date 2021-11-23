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

package org.limbo.doorkeeper.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Devil
 * @since 2021/4/19 8:27 下午
 */
@Data
public class TagVO {

    @Schema(description ="ID")
    private Long tagId;

    @Schema(description ="域ID")
    private Long realmId;

    @Schema(description ="clientID")
    private Long clientId;

    @Schema(description ="key")
    private String k;

    @Schema(description ="value")
    private String v;

    @Schema(description ="key=value")
    private String kv;
}
