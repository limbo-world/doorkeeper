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
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.AccountAdminRoleAddParam;
import org.limbo.doorkeeper.api.model.param.AccountAdminRoleQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountAdminRoleVO;
import org.limbo.doorkeeper.server.constants.BusinessType;
import org.limbo.doorkeeper.server.constants.OperateType;
import org.limbo.doorkeeper.server.dao.AccountAdminRoleMapper;
import org.limbo.doorkeeper.server.entity.AccountAdminRole;
import org.limbo.doorkeeper.server.service.AccountAdminRoleService;
import org.limbo.doorkeeper.server.support.plog.PLog;
import org.limbo.doorkeeper.server.support.plog.PLogConstants;
import org.limbo.doorkeeper.server.support.plog.PLogTag;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author Devil
 * @Date 2020/12/7 10:35 上午
 */
@Service
public class AccountAdminRoleServiceImpl implements AccountAdminRoleService {

    @Autowired
    private AccountAdminRoleMapper accountAdminRoleMapper;

    @Override
    public List<AccountAdminRoleVO> list(Long projectId, AccountAdminRoleQueryParam param) {
        List<AccountAdminRole> accountAdminRoles = accountAdminRoleMapper.selectList(Wrappers.<AccountAdminRole>lambdaQuery()
                .eq(AccountAdminRole::getProjectId, projectId)
                .eq(param.getAccountId() != null, AccountAdminRole::getAccountId, param.getAccountId())
        );
        return EnhancedBeanUtils.createAndCopyList(accountAdminRoles, AccountAdminRoleVO.class);
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.CREATE, businessType = BusinessType.ADMIN_ROLE)
    public void batchSave(@PLogTag(PLogConstants.CONTENT) Long projectId, @PLogTag(PLogConstants.CONTENT) List<AccountAdminRoleAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<AccountAdminRole> accountAdminRoles = EnhancedBeanUtils.createAndCopyList(params, AccountAdminRole.class);
        for (AccountAdminRole accountAdminRole : accountAdminRoles) {
            accountAdminRole.setProjectId(projectId);
        }
        accountAdminRoleMapper.batchInsertIgnore(accountAdminRoles);
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.DELETE, businessType = BusinessType.ADMIN_ROLE)
    public int batchDelete(@PLogTag(PLogConstants.CONTENT) Long projectId, @PLogTag(PLogConstants.CONTENT) List<Long> accountAdminRoleIds) {
        return accountAdminRoleMapper.delete(Wrappers.<AccountAdminRole>lambdaQuery()
                .eq(AccountAdminRole::getProjectId, projectId)
                .in(AccountAdminRole::getAccountAdminRoleId, accountAdminRoleIds)
        );
    }
}
