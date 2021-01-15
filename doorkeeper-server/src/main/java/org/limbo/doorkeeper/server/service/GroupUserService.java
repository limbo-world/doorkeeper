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
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.group.GroupUserBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.group.GroupUserQueryParam;
import org.limbo.doorkeeper.api.model.vo.GroupUserVO;
import org.limbo.doorkeeper.server.dao.GroupUserMapper;
import org.limbo.doorkeeper.server.entity.GroupUser;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
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

    public Page<GroupUserVO> page(Long realmId, Long groupId, GroupUserQueryParam param) {

        param.setRealmId(realmId);
        param.setGroupId(groupId);
        long count = groupUserMapper.listVOCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(groupUserMapper.listVOS(param));
        }
        return param;
    }

    @Transactional
    public void batchUpdate(Long realmId, Long groupId, GroupUserBatchUpdateParam param) {
        switch (param.getType()) {
            case SAVE: // 新增
                List<GroupUser> groupUsers = new ArrayList<>();
                for (Long userId : param.getUserIds()) {
                    GroupUser groupUser = new GroupUser();
                    groupUser.setGroupId(groupId);
                    groupUser.setUserId(userId);
                    groupUsers.add(groupUser);
                }
                MyBatisPlusUtils.batchSave(groupUsers, GroupUser.class);
                break;
            case DELETE: // 删除
                groupUserMapper.delete(Wrappers.<GroupUser>lambdaQuery()
                        .eq(GroupUser::getGroupId, groupId)
                        .in(GroupUser::getUserId, param.getUserIds())
                );
                break;
            default:
                break;
        }
    }

}
