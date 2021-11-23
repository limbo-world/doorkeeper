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

package org.limbo.doorkeeper.server.domain;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.api.model.vo.GroupVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @since 2021/6/1 4:29 下午
 */
@Data
public class GroupTreeDO {

    private List<GroupVO> tree;

    private List<GroupVO> list;

    private GroupTreeDO(List<GroupVO> groups) {
        this.list = groups;
        this.tree = organizeGroupTree(new ArrayList<>(), groups);
    }

    /**
     * 工厂方式创建
     * @return
     */
    public static GroupTreeDO create(List<GroupVO> groups) {
        return new GroupTreeDO(groups);
    }

    /**
     * 组织树状数据结构
     */
    private List<GroupVO> organizeGroupTree(List<GroupVO> tree, List<GroupVO> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return tree;
        }
        List<GroupVO> remain = new ArrayList<>(); // 剩余的
        for (GroupVO group : groups) {
            if (group.getChildren() == null) {
                group.setChildren(new ArrayList<>());
            }
            if (DoorkeeperConstants.DEFAULT_ID.equals(group.getParentId())) {
                tree.add(group);
            } else {
                GroupVO findGroup = findFromTree(group.getParentId(), tree);
                if (findGroup != null) {
                    findGroup.getChildren().add(group);
                } else {
                    remain.add(group);
                }
            }
        }
        return organizeGroupTree(tree, remain);
    }

    /**
     * 找到对应树中是否存在此用户组
     */
    public GroupVO findFromTree(Long groupId) {
        if (CollectionUtils.isEmpty(tree) || groupId == null) {
            return null;
        }
        return findFromTree(groupId, tree);
    }

    /**
     * 找到对应树中是否存在此用户组
     */
    private GroupVO findFromTree(Long groupId, List<GroupVO> tree) {
        if (CollectionUtils.isEmpty(tree)) {
            return null;
        }
        for (GroupVO node : tree) {
            if (groupId.equals(node.getGroupId())) {
                return node;
            } else {
                GroupVO find = findFromTree(groupId, node.getChildren());
                if (find != null) {
                    return find;
                }
            }
        }
        return null;
    }

}
