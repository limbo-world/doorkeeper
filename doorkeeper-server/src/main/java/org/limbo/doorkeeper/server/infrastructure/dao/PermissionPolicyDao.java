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

package org.limbo.doorkeeper.server.infrastructure.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.server.infrastructure.po.PermissionPolicyPO;
import org.limbo.doorkeeper.server.infrastructure.mapper.PermissionPolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/8 10:41 上午
 */
@Repository
public class PermissionPolicyDao {

    @Autowired
    private PermissionPolicyMapper permissionPolicyMapper;

    @Transactional
    public void update(Long permissionId, List<Long> policyIds) {
        // 删除
        permissionPolicyMapper.delete(Wrappers.<PermissionPolicyPO>lambdaQuery()
                .eq(PermissionPolicyPO::getPermissionId, permissionId)
        );
        if (CollectionUtils.isEmpty(policyIds)) {
            return;
        }
        // 新增
        List<PermissionPolicyPO> permissionPolicies = new ArrayList<>();
        for (Long policyId : policyIds) {
            PermissionPolicyPO permissionPolicy = new PermissionPolicyPO();
            permissionPolicy.setPermissionId(permissionId);
            permissionPolicy.setPolicyId(policyId);
            permissionPolicies.add(permissionPolicy);
        }
        batchSave(permissionPolicies);
    }

    @Transactional
    public void batchSave(List<PermissionPolicyPO> permissionPolicies) {
        if (CollectionUtils.isEmpty(permissionPolicies)) {
            return;
        }
        permissionPolicyMapper.batchInsertIgnore(permissionPolicies);
    }
}
