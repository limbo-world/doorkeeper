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

package org.limbo.doorkeeper.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.permission.PermissionPolicyAddParam;
import org.limbo.doorkeeper.api.model.vo.PermissionPolicyVO;
import org.limbo.doorkeeper.server.dao.PermissionPolicyMapper;
import org.limbo.doorkeeper.server.entity.PermissionPolicy;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/8 10:41 上午
 */
@Service
public class PermissionPolicyService {

    @Autowired
    private PermissionPolicyMapper permissionPolicyMapper;

    public List<PermissionPolicyVO> getByPermissionId(Long permissionId) {
        return null;
    }

    @Transactional
    public void update(Long permissionId, List<PermissionPolicyAddParam> params) {
        // 删除
        permissionPolicyMapper.delete(Wrappers.<PermissionPolicy>lambdaQuery()
                .eq(PermissionPolicy::getPermissionId, permissionId)
        );
        // 新增
        List<PermissionPolicyAddParam> addParams = params.stream()
                .filter(obj -> obj.getPermissionPolicyId() == null)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(addParams)) {
            List<PermissionPolicy> list = new ArrayList<>();
            for (PermissionPolicyAddParam param : addParams) {
                PermissionPolicy po = EnhancedBeanUtils.createAndCopy(param, PermissionPolicy.class);
                po.setPermissionId(permissionId);
                list.add(po);
            }
            MyBatisPlusUtils.batchSave(list, PermissionPolicy.class);
        }
    }

    @Transactional
    public void batchSave(Long permissionId, List<PermissionPolicyAddParam> params) {
        Verifies.verify(CollectionUtils.isNotEmpty(params), "策略列表为空");
        List<PermissionPolicy> list = new ArrayList<>();
        for (PermissionPolicyAddParam param : params) {
            PermissionPolicy po = new PermissionPolicy();
            po.setPermissionId(permissionId);
            po.setPolicyId(param.getPolicyId());
            list.add(po);
        }
        MyBatisPlusUtils.batchSave(list, PermissionPolicy.class);
    }
}
