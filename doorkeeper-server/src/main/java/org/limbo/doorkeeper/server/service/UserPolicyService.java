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

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.server.dal.entity.policy.PolicyUser;
import org.limbo.doorkeeper.server.dal.mapper.policy.PolicyUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/6 7:44 下午
 */
@Service
public class UserPolicyService {

    @Autowired
    private PolicyUserMapper policyUserMapper;

    @Transactional
    public void batchSave(Long userId, List<Long> policyIds) {
        if (CollectionUtils.isEmpty(policyIds)) {
            return;
        }
        List<PolicyUser> policyUsers = new ArrayList<>();
        for (Long policyId : policyIds) {
            PolicyUser policyUser = new PolicyUser();
            policyUser.setPolicyId(policyId);
            policyUser.setUserId(userId);
            policyUsers.add(policyUser);
        }
        policyUserMapper.batchInsertIgnore(policyUsers);
    }

}
