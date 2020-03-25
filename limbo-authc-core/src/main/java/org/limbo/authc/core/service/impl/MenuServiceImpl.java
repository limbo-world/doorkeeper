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

package org.limbo.authc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.po.BasePO;
import org.limbo.authc.api.interfaces.beans.po.MenuPO;
import org.limbo.authc.api.interfaces.beans.po.PermissionPO;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;
import org.limbo.authc.core.dao.MenuMapper;
import org.limbo.authc.core.dao.MenuPermissionMapper;
import org.limbo.authc.core.dao.PermissionMapper;
import org.limbo.authc.core.dao.RoleMenuMapper;
import org.limbo.authc.core.service.MenuService;
import org.limbo.authc.api.interfaces.utils.EnhancedBeanUtils;
import org.limbo.authc.core.utils.MyBatisPlusUtils;
import org.limbo.authc.api.interfaces.utils.UUIDUtils;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brozen
 * @date 2020/3/4 12:22 PM
 * @email brozen@qq.com
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private MenuPermissionMapper menuPermissionMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    @Transactional
    public String addMenu(MenuVO.AddParam param) {
        MenuPO menu = EnhancedBeanUtils.createAndCopy(param, MenuPO.class);
        if (StringUtils.isBlank(menu.getMenuCode())) {
            menu.setMenuCode(UUIDUtils.get());
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }

        Verifies.verify(menuMapper.countMenuCode(param.getProjectId(), menu.getMenuCode()).equals(0),
                "菜单编码已存在！");

        menuMapper.insert(menu);
        param.setMenuCode(menu.getMenuCode());

        if (CollectionUtils.isNotEmpty(param.getPermCodeList())) {
            List<String> permCodes = permissionMapper.selectObjs(
                    Wrappers.<PermissionPO>lambdaQuery()
                            .select(PermissionPO::getPermCode)
                            .eq(BasePO::getProjectId, param.getProjectId())
                            .in(PermissionPO::getPermCode, param.getPermCodeList())
            ).stream().map(Object::toString).collect(Collectors.toList());
            menuPermissionMapper.addMenuPermission(param.getProjectId(), menu.getMenuCode(), permCodes);
        }

        return menu.getMenuCode();
    }

    @Override
    @Transactional
    public Integer updateMenu(MenuVO.UpdateParam param) {
        Long projectId = param.getProjectId();
        MenuPO menu = menuMapper.getMenu(projectId, param.getMenuCode());
        Verifies.notNull(menu, "菜单不存在！");

        // 更新菜单
        EnhancedBeanUtils.copyPropertiesIgnoreNull(param, menu);
        Integer influenced = menuMapper.updateById(menu);

        if (influenced > 0) {
            // 更新菜单关联的权限
            List<String> permCodes = param.getPermCodeList();
            if (CollectionUtils.isNotEmpty(permCodes)) {
                // 部分更新，删除不在新列表里的，然后replace插入
                menuPermissionMapper.deleteMenuPermissionNotIn(projectId, param.getMenuCode(), permCodes);
                menuPermissionMapper.addMenuPermission(projectId, param.getMenuCode(), permCodes);
            } else {
                // 全删
                menuPermissionMapper.deleteMenuPermission(projectId, param.getMenuCode());
            }
        }

        return influenced;
    }

    @Override
    public Integer updateSort(MenuVO.UpdateSortParam param) {
        Long projectId = param.getProjectId();
        List<MenuVO.MenuSort> sorts = param.getSorts();

        List<Wrapper<MenuPO>> update = sorts.stream()
                .map(s -> Wrappers.<MenuPO>lambdaUpdate()
                        .set(MenuPO::getSort, s.getSort())
                        .eq(MenuPO::getProjectId, projectId)
                        .eq(MenuPO::getMenuCode, s.getMenuCode()))
                .collect(Collectors.toList());

        return MyBatisPlusUtils.batchUpdate(update, MenuPO.class);
    }

    @Override
    @Transactional
    public Integer deleteMenu(Long projectId, String menuCode) {
        MenuPO menu = menuMapper.getMenu(projectId, menuCode);
        Verifies.notNull(menu, "菜单不存在！");
        Verifies.equals(menu.getProjectId(), projectId, "无权执行此操作！");

        Integer influenced = menuMapper.deleteById(menuCode);
        if (influenced > 0) {
            menuPermissionMapper.deleteMenuPermission(projectId, menuCode);
            roleMenuMapper.deleteMenuRelatedRole(projectId, menuCode);
        }
        return influenced;
    }

    @Override
    public List<MenuVO> listMenu(Long projectId) {
        List<MenuPO> allMenus = menuMapper.getMenus(projectId);
        return EnhancedBeanUtils.createAndCopyList(allMenus, MenuVO.class);
    }

    @Override
    public Page<MenuVO> queryMenu(Page<MenuVO> param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MenuPO> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<MenuPO> condition = Wrappers.<MenuPO>lambdaQuery()
                .eq(BasePO::getProjectId, param.getProjectId())
                .like(StringUtils.isNotBlank(param.getKeyword()), MenuPO::getMenuCode, param.getKeyword())
                .like(StringUtils.isNotBlank(param.getKeyword()), MenuPO::getMenuName, param.getKeyword());
        mpage = menuMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), MenuVO.class));
        return param;
    }

    @Override
    public MenuVO getMenu(Long projectId, String menuCode) {
        MenuPO menu = menuMapper.getMenu(projectId, menuCode);
        Verifies.notNull(menu, "菜单不存在!");

        MenuVO vo = EnhancedBeanUtils.createAndCopy(menu, MenuVO.class);
        vo.setPermissions(menuPermissionMapper.getMenuPermissions(projectId, Collections.singletonList(menuCode)));

        return vo;
    }


}
