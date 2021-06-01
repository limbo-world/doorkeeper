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
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.model.param.add.PolicyParamAddParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyParamVO;
import org.limbo.doorkeeper.server.infrastructure.po.PolicyParamPO;
import org.limbo.doorkeeper.server.infrastructure.mapper.policy.PolicyParamMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/6 7:35 下午
 */
@Repository
public class PolicyParamDao {

    @Autowired
    private PolicyParamMapper policyParamMapper;

    public List<PolicyParamVO> getByPolicy(Long policyId) {
        List<PolicyParamPO> policyParams = policyParamMapper.selectList(Wrappers.<PolicyParamPO>lambdaQuery()
                .eq(PolicyParamPO::getPolicyId, policyId)
        );
        return EnhancedBeanUtils.createAndCopyList(policyParams, PolicyParamVO.class);
    }

    @Transactional
    public void update(Long policyId, List<PolicyParamAddParam> params) {
        // 删除
        policyParamMapper.delete(Wrappers.<PolicyParamPO>lambdaQuery()
                .eq(PolicyParamPO::getPolicyId, policyId)
        );
        // 新增
        batchSave(policyId, params);
    }

    @Transactional
    public void batchSave(Long policyId, List<PolicyParamAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<PolicyParamPO> policyParams = new ArrayList<>();
        for (PolicyParamAddParam tag : params) {
            PolicyParamPO policyParam = new PolicyParamPO();
            policyParam.setPolicyId(policyId);
            policyParam.setK(tag.getK());
            policyParam.setV(tag.getV());
            policyParam.setKv(tag.getK() + DoorkeeperConstants.KV_DELIMITER + tag.getV());
            policyParams.add(policyParam);
        }
        MyBatisPlusUtils.batchSave(policyParams, PolicyParamPO.class);
    }
}
