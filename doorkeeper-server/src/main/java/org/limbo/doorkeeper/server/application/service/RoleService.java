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

package org.limbo.doorkeeper.server.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.dto.param.add.RoleAddParam;
import org.limbo.doorkeeper.api.dto.param.query.RoleQueryParam;
import org.limbo.doorkeeper.api.dto.param.update.RolePatchParam;
import org.limbo.doorkeeper.api.dto.vo.PageVO;
import org.limbo.doorkeeper.api.dto.vo.RoleVO;
import org.limbo.doorkeeper.server.infrastructure.exception.ParamException;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.RoleMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.UserRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.policy.PolicyRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.po.GroupRolePO;
import org.limbo.doorkeeper.server.infrastructure.po.PolicyRolePO;
import org.limbo.doorkeeper.server.infrastructure.po.RolePO;
import org.limbo.doorkeeper.server.infrastructure.po.UserRolePO;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @since 2021/1/4 5:29 下午
 */
@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private GroupRoleMapper groupRoleMapper;

    @Autowired
    private PolicyRoleMapper policyRoleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Transactional
    public Long add(Long realmId, RoleAddParam param) {
        RolePO role = EnhancedBeanUtils.createAndCopy(param, RolePO.class);
        role.setRealmId(realmId);
        try {
            roleMapper.insert(role);
        } catch (DuplicateKeyException e) {
            throw new ParamException("角色已存在");
        }
        return role.getRoleId();
    }

    public PageVO<RoleVO> page(Long realmId, RoleQueryParam param) {
        IPage<RolePO> mpage = MyBatisPlusUtils.pageOf(param);
        mpage = roleMapper.selectPage(mpage, Wrappers.<RolePO>lambdaQuery()
                .eq(RolePO::getRealmId, realmId)
                .eq(RolePO::getNamespaceId, param.getNamespaceId())
                .eq(param.getIsEnabled() != null, RolePO::getIsEnabled, param.getIsEnabled())
                .eq(param.getIsDefault() != null, RolePO::getIsDefault, param.getIsDefault())
                .eq(StringUtils.isNotBlank(param.getName()), RolePO::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getDimName()), RolePO::getName, param.getDimName())
        );

        PageVO<RoleVO> result = PageVO.convertByPage(param);
        result.setTotal(mpage.getTotal());
        result.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), RoleVO.class));
        return result;
    }

    public RoleVO get(Long realmId, Long roleId) {
        RolePO role = roleMapper.getById(realmId, roleId);
        Verifies.notNull(role, "角色不存在");
        return EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
    }

    @Transactional
    public void update(Long realmId, Long roleId, RolePatchParam param) {
        RolePO role = roleMapper.getById(realmId, roleId);
        Verifies.notNull(role, "角色不存在");

        try {
            roleMapper.update(null, Wrappers.<RolePO>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()), RolePO::getName, param.getName())
                    .set(param.getDescription() != null, RolePO::getDescription, param.getDescription())
                    .set(param.getIsDefault() != null, RolePO::getIsDefault, param.getIsDefault())
                    .set(param.getIsEnabled() != null, RolePO::getIsEnabled, param.getIsEnabled())
                    .eq(RolePO::getRoleId, roleId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("角色已存在");
        }
    }

    @Transactional
    public void batchDelete(Long realmId, List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        List<RolePO> roles = roleMapper.selectList(Wrappers.<RolePO>lambdaQuery()
                .select(RolePO::getRoleId)
                .eq(RolePO::getRealmId, realmId)
                .in(RolePO::getRoleId, roleIds)
        );
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        // 从能找到的角色中删除，防止参数中传入非法ID
        roleIds = roles.stream().map(RolePO::getRoleId).collect(Collectors.toList());
        roleMapper.deleteBatchIds(roleIds);
        groupRoleMapper.delete(Wrappers.<GroupRolePO>lambdaQuery()
                .in(GroupRolePO::getRoleId, roleIds)
        );
        policyRoleMapper.delete(Wrappers.<PolicyRolePO>lambdaQuery()
                .in(PolicyRolePO::getRoleId, roleIds)
        );
        userRoleMapper.delete(Wrappers.<UserRolePO>lambdaQuery()
                .in(UserRolePO::getRoleId, roleIds)
        );
    }

}
