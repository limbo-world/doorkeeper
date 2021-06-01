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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.param.batch.GroupUserBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.GroupUserVO;
import org.limbo.doorkeeper.server.infrastructure.po.GroupUserPO;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupUserMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/12 3:31 下午
 */
@Service
public class GroupUserService {

    @Autowired
    private GroupUserMapper groupUserMapper;

    public List<GroupUserVO> list(Long realmId, Long groupId) {
        List<GroupUserPO> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUserPO>lambdaQuery()
                .eq(GroupUserPO::getGroupId, groupId)
        );
        return EnhancedBeanUtils.createAndCopyList(groupUsers, GroupUserVO.class);
    }

    @Transactional
    public void batchUpdate(Long groupId, GroupUserBatchUpdateParam param) {
        if (CollectionUtils.isEmpty(param.getUserIds())) {
            return;
        }

        switch (param.getType()) {
            case SAVE: // 新增
                List<GroupUserPO> groupUsers = new ArrayList<>();
                for (Long userId : param.getUserIds()) {
                    GroupUserPO groupUser = new GroupUserPO();
                    groupUser.setGroupId(groupId);
                    groupUser.setUserId(userId);
                    groupUser.setExtend(StringUtils.isBlank(param.getExtend()) ? "" : param.getExtend());
                    groupUsers.add(groupUser);
                }
                groupUserMapper.batchInsertIgnore(groupUsers);
                break;
            case UPDATE:
                for (Long userId : param.getUserIds()) {
                    groupUserMapper.update(null, Wrappers.<GroupUserPO>lambdaUpdate()
                            .set(GroupUserPO::getExtend, param.getExtend())
                            .eq(GroupUserPO::getGroupId, groupId)
                            .eq(GroupUserPO::getUserId, userId)
                    );
                }
                break;
            case DELETE: // 删除
                groupUserMapper.delete(Wrappers.<GroupUserPO>lambdaQuery()
                        .eq(GroupUserPO::getGroupId, groupId)
                        .in(GroupUserPO::getUserId, param.getUserIds())
                );
                break;
            default:
                break;
        }
    }

    public List<GroupUserVO> getByUser(Long userId) {
        Verifies.notNull(userId, "用户ID不能为空");
        List<GroupUserPO> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUserPO>lambdaQuery()
                .eq(GroupUserPO::getUserId, userId)
        );
        return EnhancedBeanUtils.createAndCopyList(groupUsers, GroupUserVO.class);
    }

    public GroupUserVO getByUserAndGroup(Long userId, Long groupId) {
        Verifies.notNull(userId, "用户ID不能为空");
        Verifies.notNull(groupId, "用户组ID不能为空");
        GroupUserPO groupUser = groupUserMapper.selectOne(Wrappers.<GroupUserPO>lambdaQuery()
                .eq(GroupUserPO::getUserId, userId)
                .eq(GroupUserPO::getGroupId, groupId)
        );
        return EnhancedBeanUtils.createAndCopy(groupUser, GroupUserVO.class);
    }

}
