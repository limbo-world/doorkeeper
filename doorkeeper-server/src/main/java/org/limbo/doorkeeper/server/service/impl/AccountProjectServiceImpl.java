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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.api.model.vo.AccountProjectVO;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.dao.AccountProjectMapper;
import org.limbo.doorkeeper.server.dao.ProjectMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.entity.AccountProject;
import org.limbo.doorkeeper.server.entity.Project;
import org.limbo.doorkeeper.server.service.AccountProjectService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Devil
 * @Date 2020/12/3 3:30 下午
 */
@Service
public class AccountProjectServiceImpl implements AccountProjectService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountProjectMapper accountProjectMapper;
    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public List<AccountProjectVO> list(Long accountId) {
        List<AccountProject> accountProjects = accountProjectMapper.selectList(Wrappers.<AccountProject>lambdaQuery()
                .eq(AccountProject::getAccountId, accountId)
        );
        return EnhancedBeanUtils.createAndCopyList(accountProjects, AccountProjectVO.class);
    }

    @Override
    public List<AccountProjectVO> sessionProject(Long accountId) {
        Account account = accountMapper.selectById(accountId);
        if (account.getIsAdmin()) {
            List<Project> projects = projectMapper.selectList(Wrappers.emptyWrapper());
            return EnhancedBeanUtils.createAndCopyList(projects, AccountProjectVO.class);
        } else {
            List<AccountProject> accountProjects = accountProjectMapper.selectList(Wrappers.<AccountProject>lambdaQuery()
                    .eq(AccountProject::getAccountId, accountId)
            );
            return EnhancedBeanUtils.createAndCopyList(accountProjects, AccountProjectVO.class);
        }
    }
}
