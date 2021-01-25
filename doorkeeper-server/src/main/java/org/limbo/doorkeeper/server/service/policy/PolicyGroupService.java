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
import org.limbo.doorkeeper.server.dal.entity.Group;
import org.limbo.doorkeeper.server.dal.entity.policy.PolicyGroup;
import org.limbo.doorkeeper.server.dal.mapper.GroupMapper;
import org.limbo.doorkeeper.server.dal.mapper.policy.PolicyGroupMapper;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/6 7:35 下午
 */
@Service
public class PolicyGroupService {

    @Autowired
    private PolicyGroupMapper policyGroupMapper;

    @Autowired
    private GroupMapper groupMapper;

    public List<PolicyGroupVO> getByPolicy(Long policyId) {
        List<PolicyGroup> policyGroups = policyGroupMapper.selectList(Wrappers.<PolicyGroup>lambdaQuery()
                .eq(PolicyGroup::getPolicyId, policyId)
        );
        List<PolicyGroupVO> policyGroupVOS = EnhancedBeanUtils.createAndCopyList(policyGroups, PolicyGroupVO.class);
        List<Group> groups = groupMapper.selectBatchIds(policyGroups.stream().map(PolicyGroup::getGroupId).collect(Collectors.toList()));
        for (PolicyGroupVO policyGroupVO : policyGroupVOS) {
            for (Group group : groups) {
                if (policyGroupVO.getGroupId().equals(group.getGroupId())) {
                    policyGroupVO.setName(group.getName());
                    policyGroupVO.setParentId(group.getParentId());
                }
            }
        }
        return policyGroupVOS;
    }

    @Transactional
    public void update(Long policyId, List<PolicyGroupAddParam> params) {
        // 删除
        policyGroupMapper.delete(Wrappers.<PolicyGroup>lambdaQuery()
                .eq(PolicyGroup::getPolicyId, policyId)
        );
        // 新增
        if (CollectionUtils.isNotEmpty(params)) {
            batchSave(policyId, params);
        }
    }

    @Transactional
    public void batchSave(Long policyId, List<PolicyGroupAddParam> params) {
        Verifies.verify(CollectionUtils.isNotEmpty(params), "角色列表为空");
        // 去重
        Map<Long, PolicyGroupAddParam> map = new HashMap<>();
        for (PolicyGroupAddParam param : params) {
            map.put(param.getGroupId(), param);
        }
        List<PolicyGroup> policyGroups = new ArrayList<>();
        for (PolicyGroupAddParam value : map.values()) {
            PolicyGroup policyGroup = new PolicyGroup();
            policyGroup.setPolicyId(policyId);
            policyGroup.setGroupId(value.getGroupId());
            policyGroups.add(policyGroup);
        }
        MyBatisPlusUtils.batchSave(policyGroups, PolicyGroup.class);
    }
}
