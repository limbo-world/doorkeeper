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

package org.limbo.authc.admin.service;

import org.limbo.authc.admin.beans.po.AdminAccountProjectPO;
import org.limbo.authc.admin.beans.po.AdminProjectPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author devil
 * @date 2020/3/13
 */
public interface AdminAccountProjectService extends IService<AdminAccountProjectPO> {

    List<AdminProjectPO> getByAccount(Long accountId);

    void updateAccountProjects(Long accountId, List<Long> projectIds);
}
