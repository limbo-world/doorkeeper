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
import org.limbo.doorkeeper.api.model.param.policy.PolicyGroupAddParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyGroupVO;
import org.limbo.doorkeeper.server.dal.entity.policy.PolicyGroup;
import org.limbo.doorkeeper.server.dal.mapper.policy.PolicyGroupMapper;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
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
public class PolicyGroupService {

    @Autowired
    private PolicyGroupMapper policyGroupMapper;

    public List<PolicyGroupVO> getByPolicy(Long policyId) {
        List<PolicyGroup> policyGroups = policyGroupMapper.selectList(Wrappers.<PolicyGroup>lambdaQuery()
                .eq(PolicyGroup::getPolicyId, policyId)
        );
        return EnhancedBeanUtils.createAndCopyList(policyGroups, PolicyGroupVO.class);
    }

    @Transactional
    public void update(Long policyId, List<PolicyGroupAddParam> params) {
        // 删除
        policyGroupMapper.delete(Wrappers.<PolicyGroup>lambdaQuery()
                .eq(PolicyGroup::getPolicyId, policyId)
        );
        // 新增
        if (CollectionUtils.isNotEmpty(params)) {
            for (PolicyGroupAddParam param : params) {
                param.setPolicyId(policyId);
            }
            batchSave(params);
        }
    }

    @Transactional
    public void batchSave(List<PolicyGroupAddParam> params) {
        Verifies.verify(CollectionUtils.isNotEmpty(params), "角色列表为空");
        List<PolicyGroup> policyGroups = new ArrayList<>();
        for (PolicyGroupAddParam param : params) {
            PolicyGroup policyGroup = new PolicyGroup();
            policyGroup.setPolicyId(param.getPolicyId());
            policyGroup.setGroupId(param.getGroupId());
            policyGroup.setIsExtend(param.getIsExtend());
            policyGroups.add(policyGroup);
        }
        policyGroupMapper.batchInsertIgnore(policyGroups);
    }
}
