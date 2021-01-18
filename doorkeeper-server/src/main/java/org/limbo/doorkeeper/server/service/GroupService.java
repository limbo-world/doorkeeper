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
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.param.group.GroupAddParam;
import org.limbo.doorkeeper.api.model.param.group.GroupUpdateParam;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dao.GroupMapper;
import org.limbo.doorkeeper.server.dao.RealmMapper;
import org.limbo.doorkeeper.server.entity.Group;
import org.limbo.doorkeeper.server.entity.Realm;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/9 7:57 下午
 */
@Service
public class GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private RealmMapper realmMapper;

    @Transactional
    public GroupVO add(Long realmId, GroupAddParam param) {
        Group group = EnhancedBeanUtils.createAndCopy(param, Group.class);
        group.setRealmId(realmId);
        try {
            groupMapper.insert(group);
        } catch (DuplicateKeyException e) {
            throw new ParamException("用户组已存在");
        }
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    public List<GroupVO> list(Long realmId) {
        List<Group> groups = groupMapper.selectList(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, realmId)
        );
        return EnhancedBeanUtils.createAndCopyList(groups, GroupVO.class);
    }

    public GroupVO get(Long realmId, Long groupId) {
        Group group = groupMapper.selectOne(Wrappers.<Group>lambdaQuery()
                .eq(Group::getGroupId, groupId)
                .eq(Group::getRealmId, realmId)
        );
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    public GroupVO getRealmGroup() {
        Realm dkRealm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
                .eq(Realm::getName, DoorkeeperConstants.DOORKEEPER_REALM_NAME)
        );

        Group group = groupMapper.selectOne(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, dkRealm.getRealmId())
                .eq(Group::getParentId, DoorkeeperConstants.DEFAULT_ID)
                .eq(Group::getName, DoorkeeperConstants.REALM)
        );
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    public GroupVO getPublicGroup() {
        GroupVO realmGroup = getRealmGroup();
        Group group = groupMapper.selectOne(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, realmGroup.getRealmId())
                .eq(Group::getParentId, realmGroup.getGroupId())
                .eq(Group::getName, DoorkeeperConstants.PUBLIC_REALM_NAME)
        );
        return EnhancedBeanUtils.createAndCopy(group, GroupVO.class);
    }

    @Transactional
    public void update(Long realmId, Long groupId, GroupUpdateParam param) {
        groupMapper.update(null, Wrappers.<Group>lambdaUpdate()
                .set(param.getDescription() != null, Group::getDescription, param.getDescription())
                .set(param.getIsDefault() != null, Group::getIsDefault, param.getIsDefault())
                .set(param.getParentId() != null, Group::getParentId, param.getParentId())
                .eq(Group::getGroupId, groupId)
                .eq(Group::getRealmId, realmId)
        );
    }
}
