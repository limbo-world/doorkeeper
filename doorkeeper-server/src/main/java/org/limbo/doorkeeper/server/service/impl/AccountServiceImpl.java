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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.*;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.server.constants.BusinessType;
import org.limbo.doorkeeper.server.constants.OperateType;
import org.limbo.doorkeeper.server.dao.AccountMapper;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.entity.Account;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.service.AccountRoleService;
import org.limbo.doorkeeper.server.service.AccountService;
import org.limbo.doorkeeper.server.support.plog.PLog;
import org.limbo.doorkeeper.server.support.plog.PLogConstants;
import org.limbo.doorkeeper.server.support.plog.PLogTag;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 10:58 AM
 * @email brozen@qq.com
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AccountRoleService accountRoleService;

    @Override
    @Transactional
    @PLog(operateType = OperateType.CREATE, businessType = BusinessType.ACCOUNT)
    public AccountVO addAccount(@PLogTag(PLogConstants.CONTENT) Long projectId,
                                @PLogTag(PLogConstants.CONTENT) AccountAddParam param) {
        // todo
        Account po = EnhancedBeanUtils.createAndCopy(param, Account.class);
        try {
            accountMapper.insert(po);
        } catch (DuplicateKeyException e) {
            throw new ParamException("账户已存在");
        }

        // 找到项目中需要默认添加的角色
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getProjectId, projectId)
                .eq(Role::getIsDefault, true)
        );

        if (CollectionUtils.isNotEmpty(roles)) {
            List<AccountRoleAddParam> accountRoles = new ArrayList<>();
            for (Role role : roles) {
                AccountRoleAddParam accountRole = new AccountRoleAddParam();
                accountRole.setAccountId(po.getAccountId());
                accountRole.setRoleId(role.getRoleId());
                accountRoles.add(accountRole);
            }
            accountRoleService.batchSave(projectId, accountRoles);
        }

        return EnhancedBeanUtils.createAndCopy(po, AccountVO.class);
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.UPDATE, businessType = BusinessType.ACCOUNT)
    public Integer batchUpdate(@PLogTag(PLogConstants.CONTENT)  Long projectId,
                               @PLogTag(PLogConstants.CONTENT) AccountBatchUpdateParam param) {
        // todo
        return accountMapper.update(null, Wrappers.<Account>lambdaUpdate()
                .set(param.getIsAdmin() != null, Account::getIsAdmin, param.getIsAdmin())
                .in(Account::getAccountId, param.getAccountIds())
        );
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.UPDATE, businessType = BusinessType.ACCOUNT)
    public Integer update(@PLogTag(PLogConstants.CONTENT) Long projectId,
                          @PLogTag(PLogConstants.CONTENT) AccountUpdateParam param) {
        // todo
        return accountMapper.update(null, Wrappers.<Account>lambdaUpdate()
                .set(param.getIsAdmin() != null, Account::getIsAdmin, param.getIsAdmin())
                .set(StringUtils.isNotBlank(param.getAccountDescribe()), Account::getAccountDescribe, param.getAccountDescribe())
                .eq(Account::getAccountId, param.getAccountId())
        );
    }

    @Override
    public Page<AccountVO> queryPage(Long projectId, AccountQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Account> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<Account> condition = Wrappers.<Account>lambdaQuery()
                .like(StringUtils.isNotBlank(param.getUsername()), Account::getUsername, param.getUsername())
                .in(CollectionUtils.isNotEmpty(param.getAccountIds()), Account::getAccountId, param.getAccountIds());
        mpage = accountMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), AccountVO.class));
        return param;
    }

    @Override
    public List<AccountVO> list(Long projectId) {
        // todo
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
