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

package org.limbo.authc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.limbo.authc.api.interfaces.beans.po.AccountPO;
import org.limbo.authc.api.interfaces.beans.po.ProjectPO;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.api.interfaces.utils.EnhancedBeanUtils;
import org.limbo.authc.api.interfaces.utils.MD5Utils;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.core.dao.AccountMapper;
import org.limbo.authc.core.service.AuthenticationService;
import org.limbo.authc.core.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Brozen
 * @date 2020/3/9 9:29 AM
 * @email brozen@qq.com
 */
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private ProjectService projectService;

    @Override
    @Transactional
    public AccountVO register(AccountVO.RegisterParam param, Boolean isActivated) {
        AccountPO po = EnhancedBeanUtils.createAndCopy(param, AccountPO.class);

        // 对密码做加密处理
        String md5pwd = MD5Utils.md5WithSalt(po.getPassword());
        po.setPassword(md5pwd);

        // 判断用户名是否已存在
        Verifies.verify(accountMapper.countByUsername(param.getProjectId(), param.getUsername()) == 0, "用户名已存在");

        po.setIsActivated(isActivated);
        accountMapper.replace(po);

        return EnhancedBeanUtils.createAndCopy(po, AccountVO.class);
    }

    @Override
    public Boolean isUsernameExist(AccountVO.UsernameCheckParam param) {
        return accountMapper.countByUsername(param.getProjectId(), param.getUsername()) > 0;
    }

    @Override
    public AccountVO authenticate(AccountVO.LoginParam param) {
        Verifies.verify(param.getProjectId() != null
                || StringUtils.isNotBlank(param.getProjectCode()), "未知的项目: project不存在!");

        if (param.getProjectId() == null) {
            ProjectPO project = projectService.get(param.getProjectCode());
            Verifies.notNull(project, "未知的项目: project不存在!");
            param.setProjectId(project.getProjectId());
        }

        // 账户登陆自己项目
        AccountPO account = accountMapper.getLoginAccountByUsername(param.getProjectId(), param.getUsername());
        Verifies.notNull(account, "用户名或密码错误！");
        Verifies.verify(MD5Utils.verify(param.getPassword(), account.getPassword()), "用户名或密码错误！");
        account.setPassword(null);
        return EnhancedBeanUtils.createAndCopy(account, AccountVO.class);
    }

    private LambdaQueryWrapper<AccountPO> column() {
        return Wrappers.lambdaQuery();
    }
}
