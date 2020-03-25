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

package org.limbo.authc.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.SelectKey;
import org.limbo.authc.api.interfaces.beans.po.ProjectPO;

/**
 * @author Brozen
 * @date 2020/3/10 8:42 AM
 * @email brozen@qq.com
 */
public interface ProjectMapper extends BaseMapper<ProjectPO> {

    @Insert("replace into l_project( project_code, project_secret, project_name, project_desc, is_deleted, is_activated )"
            + "values ( #{projectCode}, #{projectSecret}, #{projectName}, #{projectDesc}, 0, 0 )")
    @SelectKey(keyProperty = "projectId", before = false,
            statement = "select LAST_INSERT_ID()", resultType = Long.class)
    Integer replace(ProjectPO project);

}
