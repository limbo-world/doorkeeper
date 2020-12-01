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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.RoleQueryParam;
import org.limbo.doorkeeper.api.model.param.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.constants.BusinessType;
import org.limbo.doorkeeper.server.constants.OperateType;
import org.limbo.doorkeeper.server.dao.AccountRoleMapper;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.dao.RolePermissionMapper;
import org.limbo.doorkeeper.server.entity.AccountRole;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.entity.RolePermission;
import org.limbo.doorkeeper.server.service.RoleService;
import org.limbo.doorkeeper.server.support.plog.PLog;
import org.limbo.doorkeeper.server.support.plog.PLogConstants;
import org.limbo.doorkeeper.server.support.plog.PLogParam;
import org.limbo.doorkeeper.server.support.plog.PLogTag;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/19 5:44 PM
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AccountRoleMapper accountRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional
    @PLog(operateType = OperateType.CREATE, businessType = BusinessType.ROLE)
    public RoleVO addRole(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                          @PLogTag(PLogConstants.CONTENT) RoleAddParam param) {
        Role role = EnhancedBeanUtils.createAndCopy(param, Role.class);
        role.setProjectId(projectId);
        roleMapper.insert(role);
        return EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.UPDATE, businessType = BusinessType.ROLE)
    public Integer updateRole(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                              @PLogTag(PLogConstants.CONTENT) RoleUpdateParam param) {
        Role role = roleMapper.selectById(param.getRoleId());
        Verifies.notNull(role, "角色不存在");
        return roleMapper.update(null, Wrappers.<Role>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getRoleName()), Role::getRoleName, param.getRoleName())
                .set(StringUtils.isNotBlank(param.getRoleDescribe()), Role::getRoleDescribe, param.getRoleDescribe())
                .set(param.getIsDefault() != null, Role::getIsDefault, param.getIsDefault())
                .eq(Role::getProjectId, projectId)
                .eq(Role::getRoleId, param.getRoleId())
        );
    }

    @Override
    @Transactional
    @PLog(operateType = OperateType.DELETE, businessType = BusinessType.ROLE)
    public Integer deleteRole(PLogParam pLogParam, @PLogTag(PLogConstants.CONTENT) Long projectId,
                              @PLogTag(PLogConstants.CONTENT) List<Long> roleIds) {

        Integer result = roleMapper.delete(Wrappers.<Role>lambdaQuery()
                .in(Role::getRoleId, roleIds)
                .eq(Role::getProjectId, projectId)
        );

        // 删除账户角色绑定
        accountRoleMapper.delete(Wrappers.<AccountRole>lambdaQuery()
                .in(AccountRole::getRoleId, roleIds)
                .eq(AccountRole::getProjectId, projectId)
        );

        // 删除角色权限绑定
        rolePermissionMapper.delete(Wrappers.<RolePermission>lambdaQuery()
                .in(RolePermission::getRoleId, roleIds)
                .eq(RolePermission::getProjectId, projectId)
        );

        return result;
    }

    @Override
    public Page<RoleVO> queryRole(Long projectId, RoleQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Role> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<Role> condition = Wrappers.<Role>lambdaQuery()
                .like(StringUtils.isNotBlank(param.getRoleName()), Role::getRoleName, param.getRoleName())
                .eq(Role::getProjectId, projectId);
        mpage = roleMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), RoleVO.class));
        return param;
    }

    @Override
    public List<RoleVO> list(Long projectId) {
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getProjectId, projectId)
        );
        return EnhancedBeanUtils.createAndCopyList(roles, RoleVO.class);
    }

}
