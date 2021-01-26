/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.support.auth.policies;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.param.auth.AuthorizationCheckParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyGroupVO;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dal.entity.GroupUser;
import org.limbo.doorkeeper.server.dal.mapper.GroupUserMapper;

import java.util.List;

/**
 * @author brozen
 * @date 2021/1/18
 */
public class GroupPolicyChecker extends AbstractPolicyChecker {

    @Setter
    private GroupUserMapper groupUserMapper;

    public GroupPolicyChecker(PolicyVO policy) {
        super(policy);
    }


    /**
     * {@inheritDoc}<br/>
     *
     * 检查授权校验参数中的用户是否在对应用户组
     * 目前用户组有继承关系（后面考虑做成可配置的）
     *
     * @param authorizationCheckParam 授权校验参数
     * @return
     */
    @Override
    protected boolean doCheck(AuthorizationCheckParam<?> authorizationCheckParam) {
        List<GroupUser> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUser>lambdaQuery()
                .eq(GroupUser::getUserId, authorizationCheckParam.getUserId())
        );
        if (CollectionUtils.isEmpty(groupUsers)) {
            return false;
        }
        for (PolicyGroupVO group : policy.getGroups()) {
            for (GroupUser groupUser : groupUsers) {
                if (group.getGroupId().equals(groupUser.getGroupId())) {
                    return true;
                }
            }
        }
        return false;
    }

}
