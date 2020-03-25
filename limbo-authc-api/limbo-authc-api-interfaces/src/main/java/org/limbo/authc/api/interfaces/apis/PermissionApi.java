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
import org.limbo.authc.api.interfaces.beans.vo.PermissionVO;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/6 9:03 AM
 * @email brozen@qq.com
 */
public interface PermissionApi {

    /**
     * 添加权限
     */
    Response<PermissionVO> add(PermissionVO.AddParam param);

    /**
     * 更新权限
     */
    Response<PermissionVO> update(PermissionVO.UpdateParam param);

    /**
     * 删除权限，将会删除权限与菜单、权限与角色之间的关联
     */
    Response<Boolean> delete(PermissionVO.DeleteParam param);

    /**
     * 查询全部权限
     */
    Response<List<PermissionVO>> list(Param param);

    /**
     * 分页查询权限
     */
    Response<Page<PermissionVO>> query(PermissionVO.QueryParam param);

    /**
     * 根据code获取权限
     */
    Response<PermissionVO> get(PermissionVO.GetParam param);

}
