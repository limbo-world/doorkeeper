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

package org.limbo.authc.admin.service.impl;

import org.limbo.authc.admin.beans.po.AdminAccountPO;
import org.limbo.authc.admin.dao.AdminAccountMapper;
import org.limbo.authc.admin.utils.MyBatisPlusUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.authc.admin.beans.po.AdminAccountProjectPO;
import org.limbo.authc.admin.beans.po.AdminProjectPO;
import org.limbo.authc.admin.dao.AdminAccountProjectMapper;
import org.limbo.authc.admin.dao.AdminProjectMapper;
import org.limbo.authc.admin.service.AdminAccountProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author devil
 * @date 2020/3/13
 */
@Service
public class AdminAccountProjectServiceImpl extends ServiceImpl<AdminAccountProjectMapper, AdminAccountProjectPO> implements AdminAccountProjectService {

    @Autowired
    private AdminAccountMapper adminAccountMapper;
    @Autowired
    private AdminProjectMapper adminProjectMapper;

    @Override
    public List<AdminProjectPO> getByAccount(Long accountId) {
        // 如果是admin的超级管理员 则有所有项目权限
        AdminAccountPO account = adminAccountMapper.selectById(accountId);
        if (account.getIsSuperAdmin()) {
            return adminProjectMapper.selectList(Wrappers.emptyWrapper());
        }

        return baseMapper.getByAccount(accountId);
    }

    @Override
    public void updateAccountProjects(Long accountId, List<Long> projectIds) {
        // 先删后增
        baseMapper.delete(Wrappers.<AdminAccountProjectPO>lambdaQuery().eq(AdminAccountProjectPO::getAccountId, accountId));

        if (CollectionUtils.isEmpty(projectIds)) return;

        // 如果是更新超级管理员的，直接返回
        AdminAccountPO account = adminAccountMapper.selectById(accountId);
        if (account.getIsSuperAdmin()) return;

        List<AdminAccountProjectPO> pos = new ArrayList<>();
        for (Long projectId : projectIds) {
            AdminAccountProjectPO po = new AdminAccountProjectPO();
            po.setAccountId(accountId);
            po.setProjectId(projectId);
            pos.add(po);
        }

        MyBatisPlusUtils.batchSave(pos, AdminAccountProjectPO.class);
    }
}
