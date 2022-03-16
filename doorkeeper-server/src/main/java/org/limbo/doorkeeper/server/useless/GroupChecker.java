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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.dto.param.query.GroupCheckParam;
import org.limbo.doorkeeper.api.dto.vo.GroupVO;
import org.limbo.doorkeeper.api.dto.vo.check.GroupCheckResult;
import org.limbo.doorkeeper.infrastructure.exception.AuthorizationException;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.GroupMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.GroupUserMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.UserMapper;
import org.limbo.doorkeeper.server.infrastructure.po.GroupPO;
import org.limbo.doorkeeper.server.infrastructure.po.GroupUserPO;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.utils.reflection.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户组校验器
 *
 * @author Devil
 * @since 2021/4/19 4:21 下午
 */
@Slf4j
@Component
public class GroupChecker {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupUserMapper groupUserMapper;

    /**
     * 获取用户所在用户组
     */
    public GroupCheckResult check(Long userId, GroupCheckParam checkParam) {
        GroupCheckResult result = new GroupCheckResult();
        result.setGroups(new ArrayList<>());

        UserPO user = userMapper.selectById(userId);
        if (user == null) {
            throw new AuthorizationException("无法找到用户，Id=" + userId);
        }
        if (!user.getIsEnabled()) {
            return result;
        }
        user.setPassword(null);

        // 获取用户用户组
        List<GroupUserPO> userGroups = groupUserMapper.selectList(Wrappers.<GroupUserPO>lambdaQuery()
                .eq(GroupUserPO::getUserId, userId)
                .in(CollectionUtils.isNotEmpty(checkParam.getGroupIds()), GroupUserPO::getGroupId, checkParam.getGroupIds())
        );

        if (CollectionUtils.isEmpty(userGroups)) {
            return result;
        }

        // 获取用户组
        List<Long> groupIds = userGroups.stream().map(GroupUserPO::getGroupId).collect(Collectors.toList());
        List<GroupPO> groups = groupMapper.selectList(Wrappers.<GroupPO>lambdaQuery()
                .eq(GroupPO::getRealmId, user.getRealmId())
                .in(GroupPO::getGroupId, groupIds)
                .eq(StringUtils.isNotBlank(checkParam.getName()), GroupPO::getName, checkParam.getName())
                .like(StringUtils.isNotBlank(checkParam.getDimName()), GroupPO::getName, checkParam.getDimName())
                .in(CollectionUtils.isNotEmpty(checkParam.getNames()), GroupPO::getName, checkParam.getNames())

        );
        if (CollectionUtils.isEmpty(groups)) {
            return result;
        }

        // 合并数据
        List<GroupVO> groupVOS = EnhancedBeanUtils.createAndCopyList(groups, GroupVO.class);
        for (GroupVO groupVO : groupVOS) {
            for (GroupUserPO userGroup : userGroups) {
                if (groupVO.getGroupId().equals(userGroup.getGroupId())) {
                    groupVO.setGroupUserId(userGroup.getGroupUserId());
                    groupVO.setUserId(userGroup.getUserId());
                    groupVO.setExtend(userGroup.getExtend());
                }
            }
        }

        result.setGroups(groupVOS);
        return result;
    }

}
