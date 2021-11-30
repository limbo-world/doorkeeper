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
import org.limbo.doorkeeper.api.dto.param.add.PolicyUserAddParam;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyUserVO;
import org.limbo.doorkeeper.infrastructure.po.UserPO;
import org.limbo.doorkeeper.infrastructure.po.PolicyUserPO;
import org.limbo.doorkeeper.infrastructure.mapper.UserMapper;
import org.limbo.doorkeeper.infrastructure.mapper.policy.PolicyUserMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @since 2021/1/6 7:44 下午
 */
@Repository
public class PolicyUserDao {

    @Autowired
    private PolicyUserMapper policyUserMapper;

    @Autowired
    private UserMapper userMapper;

    public List<PolicyUserVO> getByPolicy(Long policyId) {
        List<PolicyUserPO> policyUsers = policyUserMapper.selectList(Wrappers.<PolicyUserPO>lambdaQuery()
                .eq(PolicyUserPO::getPolicyId, policyId)
        );
        if (CollectionUtils.isEmpty(policyUsers)) {
            return new ArrayList<>();
        }
        List<PolicyUserVO> policyUserVOS = EnhancedBeanUtils.createAndCopyList(policyUsers, PolicyUserVO.class);
        List<UserPO> users = userMapper.selectBatchIds(policyUsers.stream().map(PolicyUserPO::getUserId).collect(Collectors.toList()));
        for (PolicyUserVO policyUserVO : policyUserVOS) {
            for (UserPO user : users) {
                if (policyUserVO.getUserId().equals(user.getUserId())) {
                    policyUserVO.setUsername(user.getUsername());
                }
            }
        }
        return policyUserVOS;
    }

    @Transactional
    public void update(Long policyId, List<PolicyUserAddParam> params) {
        // 删除
        policyUserMapper.delete(Wrappers.<PolicyUserPO>lambdaQuery()
                .eq(PolicyUserPO::getPolicyId, policyId)
        );
        // 新增
        batchSave(policyId, params);
    }

    @Transactional
    public void batchSave(Long policyId, List<PolicyUserAddParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<PolicyUserPO> policyUsers = new ArrayList<>();
        for (PolicyUserAddParam user : params) {
            PolicyUserPO policyUser = new PolicyUserPO();
            policyUser.setPolicyId(policyId);
            policyUser.setUserId(user.getUserId());
            policyUsers.add(policyUser);
        }
        MyBatisPlusUtils.batchSave(policyUsers, PolicyUserPO.class);
    }

}
