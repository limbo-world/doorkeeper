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

package org.limbo.doorkeeper.server.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.ProjectAccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;
import org.limbo.doorkeeper.server.dao.ProjectAccountMapper;
import org.limbo.doorkeeper.server.service.ProjectAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Devil
 * @Date 2020/12/3 3:30 下午
 */
@Service
public class ProjectAccountServiceImpl implements ProjectAccountService {

    @Autowired
    private ProjectAccountMapper projectAccountMapper;

    @Override
    public List<ProjectAccountVO> list(ProjectAccountQueryParam param) {
        List<ProjectAccountVO> accountProjects = projectAccountMapper.selectVOS(param);
        return CollectionUtils.isEmpty(accountProjects) ? new ArrayList<>() : accountProjects;
    }

    @Override
    public Page<ProjectAccountVO> page(ProjectAccountQueryParam param) {
        long count = projectAccountMapper.pageVOCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(projectAccountMapper.pageVOS(param));
        }
        return param;
    }

}
