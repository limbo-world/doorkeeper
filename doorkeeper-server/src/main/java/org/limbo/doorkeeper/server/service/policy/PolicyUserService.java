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
import org.limbo.doorkeeper.api.model.param.policy.PolicyUserAddParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyUserVO;
import org.limbo.doorkeeper.server.dal.mapper.UserMapper;
import org.limbo.doorkeeper.server.dal.mapper.policy.PolicyUserMapper;
import org.limbo.doorkeeper.server.dal.entity.User;
import org.limbo.doorkeeper.server.dal.entity.policy.PolicyUser;
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
 * @date 2021/1/6 7:44 下午
 */
@Service
public class PolicyUserService {

    @Autowired
    private PolicyUserMapper policyUserMapper;

    @Autowired
    private UserMapper userMapper;

    public List<PolicyUserVO> getByPolicy(Long policyId) {
        List<PolicyUser> policyUsers = policyUserMapper.selectList(Wrappers.<PolicyUser>lambdaQuery()
                .eq(PolicyUser::getPolicyId, policyId)
        );
        List<PolicyUserVO> policyUserVOS = EnhancedBeanUtils.createAndCopyList(policyUsers, PolicyUserVO.class);
        List<User> users = userMapper.selectBatchIds(policyUsers.stream().map(PolicyUser::getUserId).collect(Collectors.toList()));
        for (PolicyUserVO policyUserVO : policyUserVOS) {
            for (User user : users) {
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
        policyUserMapper.delete(Wrappers.<PolicyUser>lambdaQuery()
                .eq(PolicyUser::getPolicyId, policyId)
        );
        // 新增
        if (CollectionUtils.isNotEmpty(params)) {
            batchSave(policyId, params);
        }
    }

    @Transactional
    public void batchSave(Long policyId, List<PolicyUserAddParam> params) {
        Verifies.verify(CollectionUtils.isNotEmpty(params), "用户列表为空");
        List<PolicyUser> policyUsers = new ArrayList<>();
        for (PolicyUserAddParam user : params) {
            PolicyUser policyUser = new PolicyUser();
            policyUser.setPolicyId(policyId);
            policyUser.setUserId(user.getUserId());
            policyUsers.add(policyUser);
        }
        MyBatisPlusUtils.batchSave(policyUsers, PolicyUser.class);
    }

}
