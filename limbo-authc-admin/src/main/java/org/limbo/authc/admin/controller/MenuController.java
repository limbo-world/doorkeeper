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

package org.limbo.authc.admin.controller;

import org.limbo.authc.admin.dubbo.consumers.MenuDubboConsumer;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.admin.support.blog.BLogType;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.MenuVO;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/12 8:10 AM
 * @email brozen@qq.com
 */
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {

    @Autowired
    private MenuDubboConsumer menuDubboConsumer;

    @PostMapping
    @BLog(name = "新增菜单", type = BLogType.CREATE)
    public Response<MenuVO> addMenu(@RequestBody MenuVO.AddParam param) {
        param.setProjectId(currentProjectId());
        return menuDubboConsumer.add(param);
    }

    @GetMapping
    public Response<List<MenuVO>> listMenu() {
        return menuDubboConsumer.list(new Param(currentProjectId()));
    }

    @GetMapping("/query")
    public Response<Page<MenuVO>> queryMenu(Page<MenuVO> param) {
        param.setProjectId(currentProjectId());
        return menuDubboConsumer.query(param);
    }

    @GetMapping("/{menuCode}")
    public Response<MenuVO> getMenu(@PathVariable("menuCode") @Valid @NotBlank(message = "菜单不存在") String menuCode) {
        return menuDubboConsumer.get(new MenuVO.GetParam(currentProjectId(), menuCode));
    }

    @PutMapping("/{menuCode}")
    @BLog(expression = "'修改菜单 '.concat(#arg1)", type = BLogType.UPDATE)
    public Response<MenuVO> updateMenu(
            @RequestBody MenuVO.UpdateParam param,
            @PathVariable("menuCode") @Valid @NotBlank(message = "菜单不存在") String menuCode
    ) {
        Verifies.equals(param.getMenuCode(), menuCode, "菜单不存在");
        param.setProjectId(currentProjectId());
        return menuDubboConsumer.update(param);
    }

    @PutMapping("/sort")
    @BLog(name = "修改菜单排序", type = BLogType.UPDATE)
    public Response<Boolean> updateMenuSort(@RequestBody MenuVO.UpdateSortParam param) {
        param.setProjectId(currentProjectId());
        return menuDubboConsumer.updateSort(param);
    }

    @DeleteMapping("/{menuCode}")
    @BLog(expression = "'删除菜单 '.concat(#arg0)", type = BLogType.DELETE)
    public Response<Boolean> deleteMenu(@PathVariable("menuCode") @Valid @NotBlank(message = "菜单不存在") String menuCode) {
        return menuDubboConsumer.delete(new MenuVO.DeleteParam(currentProjectId(), menuCode));
    }

}
