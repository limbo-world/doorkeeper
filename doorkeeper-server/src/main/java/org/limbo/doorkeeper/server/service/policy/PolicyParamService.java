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
import org.limbo.doorkeeper.api.model.param.policy.PolicyParamAddParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyParamVO;
import org.limbo.doorkeeper.server.dao.policy.PolicyParamMapper;
import org.limbo.doorkeeper.server.entity.policy.PolicyParam;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/6 7:35 下午
 */
@Service
public class PolicyParamService {

    @Autowired
    private PolicyParamMapper policyParamMapper;

    public List<PolicyParamVO> getByPolicy(Long policyId) {
        List<PolicyParam> policyParams = policyParamMapper.selectList(Wrappers.<PolicyParam>lambdaQuery()
                .eq(PolicyParam::getPolicyId, policyId)
        );
        return EnhancedBeanUtils.createAndCopyList(policyParams, PolicyParamVO.class);
    }

    @Transactional
    public void update(Long policyId, List<PolicyParamAddParam> params) {
        // 删除
        List<Long> ids = params.stream()
                .map(PolicyParamAddParam::getPolicyParamId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        policyParamMapper.delete(Wrappers.<PolicyParam>lambdaQuery()
                .notIn(CollectionUtils.isNotEmpty(ids), PolicyParam::getPolicyParamId, ids)
        );
        // 新增
        List<PolicyParamAddParam> addParams = params.stream()
                .filter(obj -> obj.getPolicyParamId() == null)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(addParams)) {
            List<PolicyParam> list = new ArrayList<>();
            for (PolicyParamAddParam param : addParams) {
                PolicyParam po = EnhancedBeanUtils.createAndCopy(param, PolicyParam.class);
                po.setPolicyId(policyId);
                list.add(po);
            }
            MyBatisPlusUtils.batchSave(list, PolicyParam.class);
        }
    }

    @Transactional
    public void batchSave(Long policyId, List<PolicyParamAddParam> params) {
        Verifies.verify(CollectionUtils.isNotEmpty(params), "标签列表为空");
        List<PolicyParam> policyParams = new ArrayList<>();
        for (PolicyParamAddParam tag : params) {
            PolicyParam policyParam = new PolicyParam();
            policyParam.setPolicyId(policyId);
            policyParam.setK(tag.getK());
            policyParam.setV(tag.getV());
            policyParams.add(policyParam);
        }
        MyBatisPlusUtils.batchSave(policyParams, PolicyParam.class);
    }
}
