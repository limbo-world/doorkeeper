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
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.po.BasePO;
import org.limbo.authc.api.interfaces.beans.po.MenuPO;
import org.limbo.authc.api.interfaces.beans.po.RolePO;
import org.limbo.authc.api.interfaces.beans.po.RolePermissionPolicyPO;
import org.limbo.authc.api.interfaces.beans.vo.PermissionPolicyVO;
import org.limbo.authc.api.interfaces.beans.vo.RoleVO;
import org.limbo.authc.api.interfaces.utils.EnhancedBeanUtils;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.core.dao.*;
import org.limbo.authc.core.service.RoleService;
import org.limbo.authc.core.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brozen
 * @date 2020/3/4 12:26 PM
 * @email brozen@qq.com
 */
@Service
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RolePermissionPolicyMapper rolePermissionPolicyMapper;

    @Autowired
    private GrantMapper grantMapper;

    @Override
    @Transactional
    public Long addRole(RoleVO.AddParam param) {
        RolePO role = EnhancedBeanUtils.createAndCopy(param, RolePO.class);
        roleMapper.insert(role);
        Long roleId = role.getRoleId();

        List<String> menuCodeList = param.getMenuCodeList();
        if (CollectionUtils.isNotEmpty(menuCodeList)) {
            menuCodeList = menuMapper.selectObjs(
                    Wrappers.<MenuPO>lambdaQuery()
                            .eq(BasePO::getProjectId, param.getProjectId())
                            .in(MenuPO::getMenuCode, menuCodeList)
            ).stream().map(Object::toString).collect(Collectors.toList());
            roleMenuMapper.addRoleMenus(param.getProjectId(), roleId, menuCodeList);
        }

        List<PermissionPolicyVO> permPolicies = param.getPermPolicies();
        if (CollectionUtils.isNotEmpty(permPolicies)) {
            List<RolePermissionPolicyPO> policies = permPolicies.stream().map(p -> {
                RolePermissionPolicyPO rp = new RolePermissionPolicyPO();
                rp.setProjectId(param.getProjectId());
                rp.setRoleId(roleId);
                rp.setPermCode(p.getPermCode());
                rp.setPolicy(p.getPolicy().getValue());
                return rp;
            }).collect(Collectors.toList());
            rolePermissionPolicyMapper.addPolicies(policies);
        }

        return roleId;
    }

    @Override
    @Transactional
    public Integer updateRole(RoleVO.UpdateParam param) {
        Long projectId = param.getProjectId();
        RolePO role = roleMapper.getRole(projectId, param.getRoleId());
        Verifies.notNull(role, "角色不存在！");

        EnhancedBeanUtils.copyPropertiesIgnoreNull(param, role);
        Integer influenced = roleMapper.updateById(role);

        if (influenced > 0) {
            // 更新关联菜单
            List<String> menuCodes = param.getMenuCodeList();
            if (CollectionUtils.isNotEmpty(menuCodes)) {
                roleMenuMapper.deleteRoleMenusNotIn(projectId, param.getRoleId(), menuCodes);
                roleMenuMapper.addRoleMenus(projectId, param.getRoleId(), menuCodes);
            } else {
                roleMenuMapper.deleteRoleMenus(projectId, param.getRoleId());
            }

            // 更新权限策略
            List<PermissionPolicyVO> permPolicies = param.getPermPolicies();
            if (CollectionUtils.isNotEmpty(permPolicies)) {
                rolePermissionPolicyMapper.deletePolicies(projectId, param.getRoleId());
                rolePermissionPolicyMapper.addPolicies(permPolicies.stream()
                        .map(p -> {
                            RolePermissionPolicyPO rp = new RolePermissionPolicyPO();
                            rp.setProjectId(projectId);
                            rp.setRoleId(param.getRoleId());
                            rp.setPermCode(p.getPermCode());
                            rp.setPolicy(p.getPolicy().getValue());
                            return rp;
                        }).collect(Collectors.toList()));
            } else {
                rolePermissionPolicyMapper.deletePolicies(projectId, param.getRoleId());
            }
        }

        return influenced;
    }

    @Override
    @Transactional
    public Integer deleteRole(Long projectId, Long roleId) {
        Integer influenced = roleMapper.deleteRole(projectId, roleId);
        if (influenced > 0) {
            roleMenuMapper.deleteRoleMenus(projectId, roleId);
            rolePermissionPolicyMapper.deletePolicies(projectId, roleId);
            grantMapper.revoke(projectId, roleId);
        }
        return influenced;
    }

    @Override
    public List<RoleVO> listRole(Long projectId) {
        List<RolePO> allRoles = roleMapper.getRoles(projectId);
        List<RoleVO> vos = EnhancedBeanUtils.createAndCopyList(allRoles, RoleVO.class);

        if (CollectionUtils.isNotEmpty(vos)) {
            fillDataToRoleVOs(projectId, vos);
        }

        return vos;
    }

    @Override
    public Page<RoleVO> queryRole(Page<RoleVO> param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RolePO> mpage = MyBatisPlusUtils.pageOf(param);
        mpage = roleMapper.selectPage(mpage, Wrappers.<RolePO>lambdaQuery()
                        .eq(BasePO::getProjectId, param.getProjectId())
                        .orderByDesc(BasePO::getGmtCreated));

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), RoleVO.class));
        return param;
    }

    private void fillDataToRoleVOs(Long projectId, List<RoleVO> vos) {
        //  设置已授权的菜单code
        /*List<Long> roleIds = vos.stream()
                .map(Role::getRoleId)
                .collect(Collectors.toList());
        List<Tuple<Long, String>> menuCodeTuples = roleMenuMapper.getGroupedRoleMenuCodes(projectId, roleIds);
        Map<Long, List<Tuple<Long, String>>> groupedMenuCodes = menuCodeTuples.stream()
                .collect(Collectors.groupingBy(Tuple::getA, Collectors.toList()));
        vos.forEach(model -> {
            List<Tuple<Long, String>> tuples = groupedMenuCodes.get(model.getRoleId());
            if (CollectionUtils.isNotEmpty(tuples)) {
                model.setMenuCodes(tuples.stream().map(Tuple::getB).collect(Collectors.toList()));
            }
        });*/
    }

    @Override
    public RoleVO getRole(Long projectId, Long roleId) {
        RolePO role = roleMapper.getRole(projectId, roleId);
        Verifies.notNull(role, "角色不存在");

        RoleVO vo = EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
        vo.setMenus(roleMenuMapper.getRoleMenus(projectId, Collections.singletonList(roleId)));
        vo.setPermPolicies(rolePermissionPolicyMapper
                .getPolicies(projectId, Collections.singletonList(roleId), null));
        vo.setAccounts(grantMapper.getGrantedUserAccounts(projectId, roleId));
        return vo;
    }
    
}
