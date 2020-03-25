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

package org.limbo.authc.api.interfaces.apis;

import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Param;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.RoleVO;

import java.util.List;

/**
 * 角色API
 *
 * @author Brozen
 * @date 2020/3/7 4:08 PM
 * @email brozen@qq.com
 */
public interface RoleApi {

    /**
     * 新建角色
     */
    Response<RoleVO> add(RoleVO.AddParam param);

    /**
     * 修改角色
     */
    Response<RoleVO> update(RoleVO.UpdateParam param);

    /**
     * 删除角色
     */
    Response<Boolean> delete(RoleVO.DeleteParam param);

    /**
     * 查询全部角色
     */
    Response<List<RoleVO>> list(Param param);

    /**
     * 分页查询角色，按添加时间倒序排序
     */
    Response<Page<RoleVO>> query(Page<RoleVO> param);

    /**
     * 获取角色，包含角色关联的权限、菜单、授权的用户信息
     */
    Response<RoleVO> get(RoleVO.GetParam param);
}
