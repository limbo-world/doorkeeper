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
import org.limbo.doorkeeper.api.model.vo.PermissionPolicyVO;
import org.limbo.doorkeeper.server.dal.entity.PermissionPolicy;
import org.limbo.doorkeeper.server.dal.mapper.PermissionPolicyMapper;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/8 10:41 上午
 */
@Service
public class PermissionPolicyService {

    @Autowired
    private PermissionPolicyMapper permissionPolicyMapper;

    public List<PermissionPolicyVO> getByPermissionId(Long permissionId) {
        List<PermissionPolicy> permissionPolicies = permissionPolicyMapper.selectList(Wrappers.<PermissionPolicy>lambdaQuery()
                .eq(PermissionPolicy::getPermissionId, permissionId)
        );
        return permissionPolicies == null ? new ArrayList<>() : EnhancedBeanUtils.createAndCopyList(permissionPolicies, PermissionPolicyVO.class);
    }

    @Transactional
    public void update(Long permissionId, List<Long> policyIds) {
        // 删除
        permissionPolicyMapper.delete(Wrappers.<PermissionPolicy>lambdaQuery()
                .eq(PermissionPolicy::getPermissionId, permissionId)
        );
        if (CollectionUtils.isEmpty(policyIds)) {
            return;
        }
        // 新增
        List<PermissionPolicy> permissionPolicies = new ArrayList<>();
        for (Long policyId : policyIds) {
            PermissionPolicy permissionPolicy = new PermissionPolicy();
            permissionPolicy.setPermissionId(permissionId);
            permissionPolicy.setPolicyId(policyId);
            permissionPolicies.add(permissionPolicy);
        }
        batchSave(permissionPolicies);
    }

    @Transactional
    public void batchSave(List<PermissionPolicy> permissionPolicies) {
        if (CollectionUtils.isEmpty(permissionPolicies)) {
            return;
        }
        permissionPolicyMapper.batchInsertIgnore(permissionPolicies);
    }
}
