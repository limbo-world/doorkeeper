/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
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

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.AccountQueryParam;
import org.limbo.doorkeeper.api.model.param.AccountUpdateParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.server.constants.BusinessType;
import org.limbo.doorkeeper.server.constants.OperateType;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.service.AccountService;
import org.limbo.doorkeeper.server.service.ProjectAccountService;
import org.limbo.doorkeeper.server.support.plog.PLog;
import org.limbo.doorkeeper.server.support.plog.PLogConstants;
import org.limbo.doorkeeper.server.support.plog.PLogTag;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 10:58 AM
 * @email brozen@qq.com
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ProjectAccountService projectAccountService;

    @Override
    @Transactional
    @PLog(operateType = OperateType.CREATE, businessType = BusinessType.ACCOUNT)
    public AccountVO addAccount(Long currentProjectId, Long currentAccountId,
                                        @PLogTag(PLogConstants.CONTENT) AccountAddParam param, boolean isAdmin) {
        // 判断账号是否已经存在
        Account po = EnhancedBeanUtils.createAndCopy(param, Account.class);
        po.setPassword(MD5Utils.md5WithSalt(po.getPassword()));
        try {
            accountMapper.insert(po);
        } catch (DuplicateKeyException e) {
            po = accountMapper.selectOne(Wrappers.<Account>lambdaQuery()
                    .eq(Account::getUsername, param.getUsername())
            );
        }

        // 绑定到对应的项目
        projectAccountService.joinProject(currentProjectId, po.getAccountId(), isAdmin);

        return EnhancedBeanUtils.createAndCopy(po, AccountVO.class);
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.UPDATE, businessType = BusinessType.ACCOUNT)
    public Integer update(Long currentProjectId, Long currentAccountId,
                                  @PLogTag(PLogConstants.CONTENT) AccountUpdateParam param) {
        return accountMapper.update(null, Wrappers.<Account>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getAccountDescribe()), Account::getAccountDescribe, param.getAccountDescribe())
                .set(StringUtils.isNotBlank(param.getNickname()), Account::getNickname, param.getNickname())
                .eq(Account::getAccountId, param.getAccountId())
        );
    }

    @Override
    public Page<AccountVO> queryPage(Long projectId, AccountQueryParam param) {
        param.setProjectId(projectId);
        long count = accountMapper.pageVOCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(accountMapper.pageVOS(param));
        }
        return param;
    }

    @Override
    public List<AccountVO> list(Long projectId) {
        List<Account> accounts = accountMapper.selectList(Wrappers.emptyWrapper());
        return EnhancedBeanUtils.createAndCopyList(accounts, AccountVO.class);
    }

    @Override
    public AccountVO get(Long accountId) {
        Account account = accountMapper.selectOne(Wrappers.<Account>lambdaQuery()
                .eq(Account::getAccountId, accountId)
        );
        return account == null ? null : EnhancedBeanUtils.createAndCopy(account, AccountVO.class);
    }

}
