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
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.param.resource.RealmAddParam;
import org.limbo.doorkeeper.api.model.vo.RealmVO;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.RealmMapper;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.dao.UserRoleMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.Realm;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.entity.UserRole;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/11 11:17 上午
 */
@Service
public class RealmService {

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private DoorkeeperService doorkeeperService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Transactional
    public RealmVO add(Long userId, RealmAddParam param) {
        Realm realm = EnhancedBeanUtils.createAndCopy(param, Realm.class);
        try {
            realmMapper.insert(realm);
        } catch (DuplicateKeyException e) {
            throw new ParamException("域已存在");
        }

        // 初始化realm数据
        doorkeeperService.createRealmData(userId, realm.getRealmId(), realm.getName());

        return EnhancedBeanUtils.createAndCopy(realm, RealmVO.class);
    }

    /**
     * user拥有哪些realm
     */
    public List<RealmVO> userRealms(Long userId) {
        Realm dkRealm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
                .eq(Realm::getName, DoorkeeperConstants.REALM_NAME)
        );

        // 判断是不是DK的REALM admin
        Role dkAdmin = roleMapper.getByName(dkRealm.getRealmId(), DoorkeeperConstants.REALM_CLIENT_DEFAULT_ID, DoorkeeperConstants.ADMIN);
        if (dkAdmin != null) {
            UserRole userRole = userRoleMapper.selectOne(Wrappers.<UserRole>lambdaQuery()
                    .eq(UserRole::getUserId, userId)
                    .eq(UserRole::getRoleId, dkAdmin.getRoleId())
            );
            if (userRole != null) {
                List<Realm> realms = realmMapper.selectList(Wrappers.emptyWrapper());
                return EnhancedBeanUtils.createAndCopyList(realms, RealmVO.class);
            }
        }

        // 用户拥有哪些域
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getRealmId, dkRealm.getRealmId())
                .ne(Role::getClientId, DoorkeeperConstants.REALM_CLIENT_DEFAULT_ID)
                .eq(Role::getName, DoorkeeperConstants.BINDER)
                .eq(Role::getIsEnabled, true)
        );
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }

        // 用户拥有哪些client 如果由client则也显示对应的域
        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roles.stream().map(Role::getRoleId).collect(Collectors.toList()))
        );
        if (CollectionUtils.isEmpty(userRoles)) {
            return new ArrayList<>();
        }

        // 判断用户拥有的角色 都属于哪些client 根据client名称 获取realm
        List<Long> clientIds = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            for (Role role : roles) {
                if (userRole.getRoleId().equals(role.getRoleId())) {
                    clientIds.add(role.getClientId());
                    break;
                }
            }
        }
        if (CollectionUtils.isEmpty(clientIds)) {
            return new ArrayList<>();
        }

        List<Client> clients = clientMapper.selectBatchIds(clientIds);
        if (CollectionUtils.isEmpty(clients)) {
            return new ArrayList<>();
        }

        List<Realm> realms = realmMapper.selectList(Wrappers.<Realm>lambdaQuery()
                .in(Realm::getName, clients.stream().map(Client::getName).collect(Collectors.toList()))
        );
        return EnhancedBeanUtils.createAndCopyList(realms, RealmVO.class);
    }
}
