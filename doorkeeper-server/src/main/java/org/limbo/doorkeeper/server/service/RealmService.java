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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.param.check.ResourceCheckParam;
import org.limbo.doorkeeper.api.model.param.realm.RealmUpdateParam;
import org.limbo.doorkeeper.api.model.param.resource.RealmAddParam;
import org.limbo.doorkeeper.api.model.vo.RealmVO;
import org.limbo.doorkeeper.api.model.vo.check.ResourceCheckResult;
import org.limbo.doorkeeper.server.dal.entity.Client;
import org.limbo.doorkeeper.server.dal.entity.Realm;
import org.limbo.doorkeeper.server.dal.entity.User;
import org.limbo.doorkeeper.server.dal.mapper.ClientMapper;
import org.limbo.doorkeeper.server.dal.mapper.RealmMapper;
import org.limbo.doorkeeper.server.dal.mapper.UserMapper;
import org.limbo.doorkeeper.server.support.ParamException;
import org.limbo.doorkeeper.server.support.auth.ResourceChecker;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.JWTUtil;
import org.limbo.doorkeeper.server.utils.UUIDUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private UserMapper userMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ResourceChecker resourceChecker;

    @Transactional
    public RealmVO add(Long userId, RealmAddParam param) {
        Realm realm = EnhancedBeanUtils.createAndCopy(param, Realm.class);
        if (StringUtils.isBlank(param.getSecret())) {
            realm.setSecret(UUIDUtils.get());
        }
        try {
            realmMapper.insert(realm);
        } catch (DuplicateKeyException e) {
            throw new ParamException("域已存在");
        }

        // 初始化realm数据
        doorkeeperService.createRealmClient(userId, realm.getRealmId(), realm.getName(), true);

        return EnhancedBeanUtils.createAndCopy(realm, RealmVO.class);
    }

    /**
     * user拥有哪些realm
     */
    public List<RealmVO> userRealms(Long userId) {
        LambdaQueryWrapper<Realm> realmSelect = Wrappers.<Realm>lambdaQuery().select(Realm::getRealmId, Realm::getName);
        // 判断是不是doorkeeper的REALM admin
        if (doorkeeperService.isSuperAdmin(userId)) {
            List<Realm> realms = realmMapper.selectList(realmSelect);
            return EnhancedBeanUtils.createAndCopyList(realms, RealmVO.class);
        }

        Realm doorkeeperRealm = realmMapper.getDoorkeeperRealm();

        // 普通用户，查看绑定的realm 资源
        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .select(Client::getClientId, Client::getName)
                .eq(Client::getRealmId, doorkeeperRealm.getRealmId())
        );
        List<String> realmNames = new ArrayList<>();
        for (Client client : clients) {
            ResourceCheckParam checkParam = new ResourceCheckParam();
            checkParam.setClientId(client.getClientId());
            checkParam.setTags(Collections.singletonList("type=realmOwn"));
            ResourceCheckResult check = resourceChecker.check(userId, true, checkParam);
            if (CollectionUtils.isNotEmpty(check.getResources())) {
                realmNames.add(client.getName());
            }
        }

        if (CollectionUtils.isEmpty(realmNames)) {
            return new ArrayList<>();
        }

        List<Realm> realms = realmMapper.selectList(realmSelect
                .in(Realm::getName, realmNames)
        );

        return EnhancedBeanUtils.createAndCopyList(realms, RealmVO.class);
    }

    public RealmVO get(Long realmId) {
        Realm realm = realmMapper.selectById(realmId);
        return EnhancedBeanUtils.createAndCopy(realm, RealmVO.class);
    }

    public void update(Long realmId, RealmUpdateParam param) {
        realmMapper.update(null, Wrappers.<Realm>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getSecret()), Realm::getSecret, param.getSecret())
                .eq(Realm::getRealmId, realmId)
        );
    }


    public Realm getRealmByToken(String token) {
        Long userId = JWTUtil.getUserId(token);
        User user;
        if (userId != null) {
            user = userMapper.selectById(userId);
        } else {
            String username = JWTUtil.getUsername(token);
            Long realmId = JWTUtil.getRealmId(token);
            user = userMapper.getByUsername(realmId, username);
        }
        Verifies.notNull(user, "用户不存在");
        Verifies.verify(user.getIsEnabled(), "用户未启用");

        Realm realm = realmMapper.selectById(user.getRealmId());
        Verifies.notNull(realm, "realm不存在");
        return realm;
    }
}
