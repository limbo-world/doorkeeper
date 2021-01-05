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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.RoleQueryParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.dao.RoleCombineMapper;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.entity.RoleCombine;
import org.limbo.doorkeeper.server.service.RoleService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 5:29 下午
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleCombineMapper roleCombineMapper;

    @Override
    @Transactional
    public RoleVO add(RoleAddParam param) {
        // todo 是否能操作这个realm 这个client

        Role role = EnhancedBeanUtils.createAndCopy(param, Role.class);
        role.setIsCombine(false);
        List<RoleCombine> roleCombines = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(param.getCombineRoleIds())) {
            // todo 校验这些role
            role.setIsCombine(true);
        }
        try {
            roleMapper.insert(role);
        } catch (DuplicateKeyException e) {
            throw new ParamException("角色已存在");
        }

        // 如果是组合的 添加下面的信息
        if (CollectionUtils.isNotEmpty(param.getCombineRoleIds())) {
            for (Long combineRoleId : param.getCombineRoleIds()) {
                RoleCombine roleCombine = new RoleCombine();
                roleCombine.setParentId(role.getRoleId());
                roleCombine.setRoleId(combineRoleId);
                roleCombines.add(roleCombine);
            }
            MyBatisPlusUtils.batchSave(roleCombines, RoleCombine.class);
        }

        return EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
    }

    @Override
    public Page<RoleVO> page(RoleQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Role> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<Role> condition = Wrappers.<Role>lambdaQuery()
                .eq(Role::getRealmId, param.getRealmId())
                .like(StringUtils.isNotBlank(param.getName()), Role::getName, param.getName())
                .eq(param.getClientId() != null, Role::getClientId, param.getClientId())
                .orderByDesc(Role::getRoleId);
        mpage = roleMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), RoleVO.class));
        return param;
    }
}
