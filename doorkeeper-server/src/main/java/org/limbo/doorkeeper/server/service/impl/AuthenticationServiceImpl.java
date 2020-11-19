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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.limbo.doorkeeper.api.model.param.AccountLoginParam;
import org.limbo.doorkeeper.api.model.param.AccountRegisterParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.entity.Project;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.limbo.doorkeeper.server.service.ProjectService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MD5Utils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2020/11/19 3:23 PM
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ProjectService projectService;

    @Override
    public AccountVO register(AccountRegisterParam param, Boolean isActivated) {
        Account po = EnhancedBeanUtils.createAndCopy(param, Account.class);

        // 对密码做加密处理
        String md5pwd = MD5Utils.md5WithSalt(po.getPassword());
        po.setPassword(md5pwd);

        // 判断用户名是否已存在
        Verifies.verify(accountMapper.countByUsername(param.getProjectId(), param.getUsername()) == 0, "用户名已存在");

        po.setIsActivated(isActivated);
        accountMapper.replace(po);

        // todo 为账户添加默认角色

        return EnhancedBeanUtils.createAndCopy(po, AccountVO.class);
    }

    @Override
    public AccountVO login(AccountLoginParam param) {
        Verifies.verify(param.getProjectId() != null
                || StringUtils.isNotBlank(param.getProjectCode()), "未知的项目: project不存在!");

        if (param.getProjectId() == null) {
            Project project = projectService.get(param.getProjectCode());
            Verifies.notNull(project, "未知的项目: project不存在!");
            param.setProjectId(project.getProjectId());
        }

        // 账户登陆自己项目
        Account account = accountMapper.getAccountAllByUsername(param.getProjectId(), param.getUsername());
        Verifies.notNull(account, "用户名或密码错误！");
        Verifies.verify(MD5Utils.verify(param.getPassword(), account.getPassword()), "用户名或密码错误！");
        account.setPassword(null);
        return EnhancedBeanUtils.createAndCopy(account, AccountVO.class);
    }
}
