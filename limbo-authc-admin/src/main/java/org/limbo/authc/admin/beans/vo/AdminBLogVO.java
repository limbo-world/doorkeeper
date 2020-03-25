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

package org.limbo.authc.admin.beans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.limbo.authc.admin.beans.po.AdminBLogPO;
import org.limbo.authc.api.interfaces.beans.Page;

/**
 * @author Brozen
 * @date 2020/3/24 10:54 AM
 * @email brozen@qq.com
 */
@EqualsAndHashCode(callSuper = true)
public class AdminBLogVO extends AdminBLogPO {

    private static final long serialVersionUID = 8203805928954227182L;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class QueryParam extends Page<AdminBLogVO> {
        private static final long serialVersionUID = 3678467117594503611L;

        private Long accountId;

        private String type;

    }

}
