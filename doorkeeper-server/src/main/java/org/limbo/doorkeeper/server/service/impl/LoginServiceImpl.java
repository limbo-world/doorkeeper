/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.param.LoginParam;
import org.limbo.doorkeeper.api.model.param.ProjectAccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;
import org.limbo.doorkeeper.api.model.vo.SessionAccount;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.service.LoginService;
import org.limbo.doorkeeper.server.service.ProjectAccountService;
import org.limbo.doorkeeper.server.support.session.RedisSessionDAO;
import org.limbo.doorkeeper.server.utils.MD5Utils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/23 8:04 PM
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private RedisSessionDAO sessionDAO;

    @Autowired
    private ProjectAccountService projectAccountService;

    @Override
    public SessionAccount login(LoginParam param) {

        Account account = accountMapper.selectOne(Wrappers.<Account>lambdaQuery()
                .eq(Account::getUsername, param.getUsername())
        );
        Verifies.notNull(account, "账户不存在");
        Verifies.verify(MD5Utils.verify(param.getPassword(), account.getPassword()), "用户名或密码错误！");

        SessionAccount sessionAccount = new SessionAccount();
        sessionAccount.setAccountId(account.getAccountId());
        sessionAccount.setNickname(account.getNickname());

        // 选中当前项目
        ProjectAccountQueryParam projectAccountQueryParam = new ProjectAccountQueryParam();
        projectAccountQueryParam.setAccountId(account.getAccountId());
        List<ProjectAccountVO> projectAccountVOS = projectAccountService.list(projectAccountQueryParam);
        sessionAccount.setProjects(projectAccountVOS);
        return sessionDAO.createSession(sessionAccount);
    }

}
