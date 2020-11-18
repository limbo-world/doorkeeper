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
import org.apache.commons.lang3.StringUtils;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.po.BasePO;
import org.limbo.authc.api.interfaces.beans.po.PermissionPO;
import org.limbo.authc.api.interfaces.beans.vo.PermissionVO;
import org.limbo.authc.api.interfaces.utils.EnhancedBeanUtils;
import org.limbo.authc.api.interfaces.utils.UUIDUtils;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.core.dao.MenuPermissionMapper;
import org.limbo.authc.core.dao.PermissionMapper;
import org.limbo.authc.core.dao.RolePermissionPolicyMapper;
import org.limbo.authc.core.service.PermissionService;
import org.limbo.authc.core.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/4 10:12 AM
 * @email brozen@qq.com
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private MenuPermissionMapper menuPermissionMapper;

    @Autowired
    private RolePermissionPolicyMapper rolePermissionPolicyMapper;

    @Override
    @Transactional
    public PermissionVO addPermission(PermissionVO.AddParam param) {
        PermissionPO permission = EnhancedBeanUtils.createAndCopy(param, PermissionPO.class);
        permission.setApis(StringUtils.join(param.getApiList(), ","));
        permission.setIsOnline(false);
        if (StringUtils.isBlank(permission.getPermCode())) {
            permission.setPermCode(UUIDUtils.get());
        }

        Verifies.verify(permissionMapper.countPermCode(param.getProjectId(), param.getPermCode()).equals(0),
                "权限编码已经存在！");

        permissionMapper.insert(permission);
        param.setPermCode(permission.getPermCode());
        return EnhancedBeanUtils.createAndCopy(permission, PermissionVO.class);
    }

    @Override
    @Transactional
    public Integer updatePermission(PermissionVO.UpdateParam param) {
        PermissionPO permission = permissionMapper.getPermission(param.getProjectId(), param.getPermCode());
        Verifies.notNull(permission, "权限不存在！");

        EnhancedBeanUtils.copyPropertiesIgnoreNull(param, permission);
        permission.setApis(StringUtils.join(param.getApiList(), ","));
        return permissionMapper.updateById(permission);
    }

    @Override
    @Transactional
    public Integer deletePermission(Long projectId, String permCode) {
        Integer influenced = permissionMapper.deletePermission(projectId, permCode);
        if (influenced > 0) {
            menuPermissionMapper.deletePermissionRelatedMenu(projectId, permCode);
            rolePermissionPolicyMapper.deletePoliciesByPermission(projectId, permCode);
        }
        return influenced;
    }

    @Override
    public List<PermissionVO> listPermission(Long projectId) {
        List<PermissionPO> allPermissions = permissionMapper.getPermissions(projectId);
        return EnhancedBeanUtils.createAndCopyList(allPermissions, PermissionVO.class);
    }

    @Override
    public Page<PermissionVO> queryPermission(PermissionVO.QueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PermissionPO> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<PermissionPO> condition = Wrappers.<PermissionPO>lambdaQuery()
                .eq(BasePO::getProjectId, param.getProjectId())
                .like(StringUtils.isNotBlank(param.getApi()), PermissionPO::getApis, param.getApi())
                .like(StringUtils.isNotBlank(param.getKeyword()), PermissionPO::getPermCode, param.getKeyword())
                .like(StringUtils.isNotBlank(param.getKeyword()), PermissionPO::getPermName, param.getKeyword())
                .like(StringUtils.isNotBlank(param.getKeyword()), PermissionPO::getPermDesc, param.getKeyword())
                .orderByDesc(PermissionPO::getPermCode);
        mpage = permissionMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), PermissionVO.class));
        return param;
    }

    @Override
    public PermissionVO getPermission(Long projectId, String permCode) {
        PermissionPO permission = permissionMapper.getPermission(projectId, permCode);
        Verifies.notNull(permission, "权限不存在");
        return EnhancedBeanUtils.createAndCopy(permission, PermissionVO.class);
    }


}
