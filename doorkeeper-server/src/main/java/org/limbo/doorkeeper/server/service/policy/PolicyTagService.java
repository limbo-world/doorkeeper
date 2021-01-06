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
import org.limbo.doorkeeper.api.model.param.policy.PolicyTagAddParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyTagVO;
import org.limbo.doorkeeper.server.dao.policy.PolicyTagMapper;
import org.limbo.doorkeeper.server.entity.policy.PolicyTag;
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
public class PolicyTagService {

    @Autowired
    private PolicyTagMapper policyTagMapper;

    public List<PolicyTagVO> getByPolicy(Long policyId) {
        List<PolicyTag> policyTags = policyTagMapper.selectList(Wrappers.<PolicyTag>lambdaQuery()
                .eq(PolicyTag::getPolicyId, policyId)
        );
        return EnhancedBeanUtils.createAndCopyList(policyTags, PolicyTagVO.class);
    }

    @Transactional
    public void update(Long policyId, List<PolicyTagAddParam> params) {
        // 删除
        List<Long> ids = params.stream()
                .map(PolicyTagAddParam::getPolicyTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        policyTagMapper.delete(Wrappers.<PolicyTag>lambdaQuery()
                .notIn(CollectionUtils.isNotEmpty(ids), PolicyTag::getPolicyTagId, ids)
        );
        // 新增
        List<PolicyTagAddParam> addParams = params.stream()
                .filter(obj -> obj.getPolicyTagId() == null)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(addParams)) {
            List<PolicyTag> list = new ArrayList<>();
            for (PolicyTagAddParam param : addParams) {
                PolicyTag po = EnhancedBeanUtils.createAndCopy(param, PolicyTag.class);
                po.setPolicyId(policyId);
                list.add(po);
            }
            MyBatisPlusUtils.batchSave(list, PolicyTag.class);
        }
    }

    @Transactional
    public void batchSave(Long policyId, List<PolicyTagAddParam> params) {
        Verifies.verify(CollectionUtils.isNotEmpty(params), "标签列表为空");
        List<PolicyTag> policyTags = new ArrayList<>();
        for (PolicyTagAddParam tag : params) {
            PolicyTag policyTag = new PolicyTag();
            policyTag.setPolicyId(policyId);
            policyTag.setK(tag.getK());
            policyTag.setV(tag.getV());
            policyTags.add(policyTag);
        }
        MyBatisPlusUtils.batchSave(policyTags, PolicyTag.class);
    }
}
