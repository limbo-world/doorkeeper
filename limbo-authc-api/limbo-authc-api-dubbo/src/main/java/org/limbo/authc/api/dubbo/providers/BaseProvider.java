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

package org.limbo.authc.api.dubbo.providers;

import org.apache.dubbo.rpc.RpcContext;
import org.limbo.authc.api.interfaces.constants.DubboContants;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;

/**
 * @author Brozen
 * @date 2020/3/10 8:18 AM
 * @email brozen@qq.com
 */
public class BaseProvider {

    /**
     * 获取调用方的项目编号
     */
    protected Long getProjectId() {
        String projectIdStr = RpcContext.getContext().getAttachment(DubboContants.Attachments.PROJECT_ID);
        Verifies.notBlank(projectIdStr, "未知项目！");
        return Long.parseLong(projectIdStr);
    }

}
