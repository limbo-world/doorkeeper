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

package org.limbo.doorkeeper.server.support;

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/26 10:32 上午
 */
public class GroupTool {

    /**
     * 组织树状数据结构
     */
    public static List<GroupVO> organizeGroupTree(List<GroupVO> tree, List<GroupVO> groups) {
        if (tree == null) {
            tree = new ArrayList<>();
        }
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
                GroupVO findGroup = findGroup(group.getParentId(), tree);
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
    public static GroupVO findGroup(Long id, List<GroupVO> tree) {
        if (CollectionUtils.isEmpty(tree)) {
            return null;
        }
        for (GroupVO node : tree) {
            if (id.equals(node.getGroupId())) {
                return node;
            } else {
                GroupVO find = findGroup(id, node.getChildren());
                if (find != null) {
                    return find;
                }
            }
        }
        return null;
    }


}
