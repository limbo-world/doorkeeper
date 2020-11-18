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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.po.AccountPO;
import org.limbo.authc.api.interfaces.beans.po.BasePO;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.api.interfaces.utils.EnhancedBeanUtils;
import org.limbo.authc.api.interfaces.utils.MD5Utils;
import org.limbo.authc.api.interfaces.utils.verify.ParamException;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.core.dao.AccountMapper;
import org.limbo.authc.core.dao.AccountPermissionPolicyMapper;
import org.limbo.authc.core.dao.GrantMapper;
import org.limbo.authc.core.service.AccountService;
import org.limbo.authc.core.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/9 10:58 AM
 * @email brozen@qq.com
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountPO> implements AccountService {

    @Autowired
    private GrantMapper grantMapper;

    @Autowired
    private AccountPermissionPolicyMapper accountPermissionPolicyMapper;

    @Override
    @Transactional
    public void updateAccount(AccountVO.UpdateParam param) {
        AccountPO account = baseMapper.getAccount(param.getProjectId(), param.getAccountId());
        Verifies.notNull(account, "用户不存在！");

        EnhancedBeanUtils.copyPropertiesIgnoreNull(param, account);

        // 账户密码不能修改 超级管理员只有一个也不能修改
        account.setUsername(null);
        account.setPassword(null);
        account.setIsSuperAdmin(null);

        baseMapper.updateBaseInfo(account);
    }

    @Override
    @Transactional
    public void updatePassword(AccountVO.UpdatePasswordParam param) {
        Verifies.verify(StringUtils.equals(param.getNewPassword(), param.getConfirmPassword()), "两次输入密码不一致！");
        AccountPO account = baseMapper.getAccountAllById(param.getProjectId(), param.getAccountId());
        Verifies.notNull(account, "用户不存在！");
        Verifies.verify(MD5Utils.verify(param.getOriginalPassword(), account.getPassword()), "密码错误！");

        // 对新密码进行加密
        // 对密码做加密处理
        String md5pwd = MD5Utils.md5WithSalt(param.getNewPassword());
        param.setNewPassword(md5pwd);

        baseMapper.updatePassword(param);
    }

    @Override
    public void deleteAccount(AccountVO.DeleteParam param) {
        baseMapper.updateDelete(param.getProjectId(), param.getAccountId(), true);
    }

    @Override
    public void unDeleteAccount(AccountVO.DeleteParam param) {
        baseMapper.updateDelete(param.getProjectId(), param.getAccountId(), false);
    }

    @Override
    public List<AccountVO> listAccounts(Long projectId) {
        List<AccountPO> accounts = baseMapper.getAccounts(projectId);
        return EnhancedBeanUtils.createAndCopyList(accounts, AccountVO.class);
    }

    @Override
    public Page<AccountVO> queryAccount(AccountVO.QueryParam param) {
        // 分页
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AccountPO> mpage
                = MyBatisPlusUtils.pageOf(param);

        // 查询条件
        // project_id = #{projectId} and nick like #{nick} and username = #{username}
        // and ( nick like #{keyword} or username like #{keyword} )
        LambdaQueryWrapper<AccountPO> condition = columnNoPass()
                .eq(BasePO::getProjectId, param.getProjectId())
                .like(StringUtils.isNoneBlank(param.getNick()), AccountPO::getNick, param.getNick())
                .eq(StringUtils.isNoneBlank(param.getUsername()), AccountPO::getUsername, param.getUsername())
                .eq(AccountPO::getIsDeleted, false)
                .eq(AccountPO::getIsActivated, true);
        if (StringUtils.isNotBlank(param.getKeyword())) {
            condition.and(w -> w.like(AccountPO::getNick, param.getKeyword())
                    .or(ww -> ww.like(AccountPO::getUsername, param.getKeyword()))
            );
        }

        // 查询，并封装结果
        mpage = baseMapper.selectPage(mpage, condition);
        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), AccountVO.class));
        return param;
    }

    @Override
    public AccountVO get(AccountVO.GetParam param) {
        Long accountId = param.getAccountId();
        String username = param.getUsername();
        Verifies.verify(StringUtils.isNotBlank(username) || accountId != null, "账户不存在");

        Long projectId = param.getProjectId();
        AccountPO account;
        if (StringUtils.isNotBlank(username) && accountId != null) {
            account = baseMapper.getAccountByIdAndUsername(projectId, accountId, username);
        } else if (accountId != null) {
            account = baseMapper.getAccount(projectId, accountId);
        } else if (StringUtils.isNotBlank(username)) {
            account = baseMapper.getAccountByUsername(projectId, username);
        } else {
            throw new ParamException("账户不存在！");
        }

        // 填充授权信息
        AccountVO vo = EnhancedBeanUtils.createAndCopy(account, AccountVO.class);
        vo.setRoles(grantMapper.getGrantedRoles(projectId, Collections.singletonList(accountId)));
        vo.setPermPolicies(accountPermissionPolicyMapper.getPolicies(projectId, Collections.singletonList(accountId), null));

        return vo;
    }

    // 没有密码
    private LambdaQueryWrapper<AccountPO> columnNoPass() {
        return Wrappers.<AccountPO>lambdaQuery().select(
                AccountPO::getProjectId,
                AccountPO::getAccountId,
                AccountPO::getUsername,
                AccountPO::getNick,
                AccountPO::getLastLogin,
                AccountPO::getLastLoginIp,
                AccountPO::getIsSuperAdmin
        );
    }
}
