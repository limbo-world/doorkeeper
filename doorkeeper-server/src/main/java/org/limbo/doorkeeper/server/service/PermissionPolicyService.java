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
import org.limbo.doorkeeper.server.dao.PermissionMapper;
import org.limbo.doorkeeper.server.dao.PermissionPolicyMapper;
import org.limbo.doorkeeper.server.dao.policy.PolicyMapper;
import org.limbo.doorkeeper.server.entity.Permission;
import org.limbo.doorkeeper.server.entity.PermissionPolicy;
import org.limbo.doorkeeper.server.entity.policy.Policy;
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

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PolicyMapper policyMapper;

    public List<PermissionPolicyVO> getByPermissionId(Long permissionId) {
        List<PermissionPolicy> permissionPolicies = permissionPolicyMapper.selectList(Wrappers.<PermissionPolicy>lambdaQuery()
                .eq(PermissionPolicy::getPermissionId, permissionId)
        );
        return permissionPolicies == null ? new ArrayList<>() : EnhancedBeanUtils.createAndCopyList(permissionPolicies, PermissionPolicyVO.class);
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
        if (CollectionUtils.isEmpty(addParams)) {
            return;
        }
        List<PermissionPolicy> permissionPolicies = verifyPolicyList(permissionId, params);
        Verifies.verify(CollectionUtils.isNotEmpty(permissionPolicies), "策略列表为空");
        MyBatisPlusUtils.batchSave(permissionPolicies, PermissionPolicy.class);
    }

    @Transactional
    public void batchSave(Long permissionId, List<PermissionPolicyAddParam> params) {

        List<PermissionPolicy> permissionPolicies = verifyPolicyList(permissionId, params);

        Verifies.verify(CollectionUtils.isNotEmpty(permissionPolicies), "策略列表为空");

        MyBatisPlusUtils.batchSave(permissionPolicies, PermissionPolicy.class);
    }

    private List<PermissionPolicy> verifyPolicyList(Long permissionId, List<PermissionPolicyAddParam> params) {
        Permission permission = permissionMapper.selectById(permissionId);
        Verifies.notNull(permission, "权限不存在");

        List<PermissionPolicy> list = new ArrayList<>();

        // 排除不是同个client的
        List<Long> policyIds = params.stream().map(PermissionPolicyAddParam::getPolicyId).collect(Collectors.toList());
        List<Policy> policys = policyMapper.selectList(Wrappers.<Policy>lambdaQuery()
                .select(Policy::getPolicyId)
                .eq(Policy::getRealmId, permission.getRealmId())
                .eq(Policy::getClientId, permission.getClientId())
                .in(Policy::getPolicyId, policyIds)
        );

        for (Policy policy : policys) {
            PermissionPolicy po = new PermissionPolicy();
            po.setPermissionId(permissionId);
            po.setPolicyId(policy.getPolicyId());
            list.add(po);
        }

        return list;
    }
}
