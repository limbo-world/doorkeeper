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

package org.limbo.doorkeeper.server.service.policy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.policy.PolicyRoleAddParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyRoleVO;
import org.limbo.doorkeeper.server.dal.entity.policy.PolicyRole;
import org.limbo.doorkeeper.server.dal.mapper.policy.PolicyRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/6 7:35 下午
 */
@Service
public class PolicyRoleService {

    @Autowired
    private PolicyRoleMapper policyRoleMapper;

    public List<PolicyRoleVO> getByPolicy(Long policyId) {
        return policyRoleMapper.listVOSByPolicyId(policyId);
    }

    @Transactional
    public void update(Long policyId, List<PolicyRoleAddParam> params) {
        // 删除
        policyRoleMapper.delete(Wrappers.<PolicyRole>lambdaQuery()
                .eq(PolicyRole::getPolicyId, policyId)
        );
        // 新增
        batchSave(policyId, params);
    }

    @Transactional
    public void batchSave(Long policyId, List<PolicyRoleAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<PolicyRole> policyRoles = new ArrayList<>();
        for (PolicyRoleAddParam role : params) {
            PolicyRole policyRole = new PolicyRole();
            policyRole.setPolicyId(policyId);
            policyRole.setRoleId(role.getRoleId());
            policyRoles.add(policyRole);
        }
        policyRoleMapper.batchInsertIgnore(policyRoles);
    }
}
