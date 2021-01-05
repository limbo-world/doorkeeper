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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.limbo.doorkeeper.api.model.param.RoleCombineQueryParam;
import org.limbo.doorkeeper.api.model.param.RoleCombineUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleCombineVO;
import org.limbo.doorkeeper.server.dao.RoleCombineMapper;
import org.limbo.doorkeeper.server.entity.RoleCombine;
import org.limbo.doorkeeper.server.service.RoleCombineService;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/5 1:59 下午
 */
@Service
public class RoleCombineServiceImpl implements RoleCombineService {

    @Autowired
    private RoleCombineMapper roleCombineMapper;

    @Override
    public List<RoleCombineVO> list(RoleCombineQueryParam param) {
        return roleCombineMapper.listVOSByParent(param);
    }

    @Override
    @Transactional
    public void update(RoleCombineUpdateParam param) {
        switch (param.getType()) {
            case POST: // 新增
                List<RoleCombine> roleCombines = new ArrayList<>();
                for (Long roleId : param.getRoleIds()) {
                    RoleCombine roleCombine = new RoleCombine();
                    roleCombine.setParentId(param.getParentId());
                    roleCombine.setRoleId(roleId);
                    roleCombines.add(roleCombine);
                }
                MyBatisPlusUtils.batchSave(roleCombines, RoleCombine.class);
                break;
            case DELETE: // 删除
                roleCombineMapper.delete(Wrappers.<RoleCombine>lambdaQuery()
                        .eq(RoleCombine::getParentId, param.getParentId())
                        .in(RoleCombine::getRoleId, param.getRoleIds())
                );
                break;
            default:
                break;
        }
    }

}
