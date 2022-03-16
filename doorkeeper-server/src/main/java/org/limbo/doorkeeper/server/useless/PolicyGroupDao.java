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

package org.limbo.doorkeeper.server.useless;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.dto.param.add.PolicyGroupAddParam;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyGroupVO;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.policy.PolicyGroupMapper;
import org.limbo.doorkeeper.server.infrastructure.po.PolicyGroupPO;
import org.limbo.utils.reflection.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @since 2021/1/6 7:35 下午
 */
@Repository
public class PolicyGroupDao {

    @Autowired
    private PolicyGroupMapper policyGroupMapper;

    public List<PolicyGroupVO> getByPolicy(Long policyId) {
        List<PolicyGroupPO> policyGroups = policyGroupMapper.selectList(Wrappers.<PolicyGroupPO>lambdaQuery()
                .eq(PolicyGroupPO::getPolicyId, policyId)
        );
        return EnhancedBeanUtils.createAndCopyList(policyGroups, PolicyGroupVO.class);
    }

    @Transactional
    public void update(Long policyId, List<PolicyGroupAddParam> params) {
        // 删除
        policyGroupMapper.delete(Wrappers.<PolicyGroupPO>lambdaQuery()
                .eq(PolicyGroupPO::getPolicyId, policyId)
        );
        // 新增
        batchSave(policyId, params);
    }

    @Transactional
    public void batchSave(Long policyId, List<PolicyGroupAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<PolicyGroupPO> policyGroups = new ArrayList<>();
        for (PolicyGroupAddParam param : params) {
            PolicyGroupPO policyGroup = new PolicyGroupPO();
            policyGroup.setPolicyId(policyId);
            policyGroup.setGroupId(param.getGroupId());
            policyGroup.setIsExtend(param.getIsExtend());
            policyGroups.add(policyGroup);
        }
        policyGroupMapper.batchInsertIgnore(policyGroups);
    }
}
