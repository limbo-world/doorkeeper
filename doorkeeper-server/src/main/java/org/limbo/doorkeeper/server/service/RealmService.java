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
import org.limbo.doorkeeper.api.model.param.resource.RealmAddParam;
import org.limbo.doorkeeper.api.model.vo.RealmVO;
import org.limbo.doorkeeper.server.dao.RealmMapper;
import org.limbo.doorkeeper.server.entity.Realm;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public RealmVO add(Long userId, RealmAddParam param) {
        Realm realm = EnhancedBeanUtils.createAndCopy(param, Realm.class);
        try {
            realmMapper.insert(realm);
        } catch (DuplicateKeyException e) {
            throw new ParamException("域已存在");
        }

        // 初始化realm数据
        doorkeeperService.createRealm(userId, realm.getRealmId(), realm.getName());

        return EnhancedBeanUtils.createAndCopy(realm, RealmVO.class);
    }

    /**
     * user拥有哪些realm
     */
    public List<RealmVO> userRealms(Long userId) {
        // todo
//        Set<Long> realmIds = userRealms.stream().map(UserRealm::getRealmId).collect(Collectors.toSet());
//        List<Realm> realms = realmMapper.selectBatchIds(new ArrayList<>())
       List<Realm> realms = realmMapper.selectList(Wrappers.emptyWrapper());
        return EnhancedBeanUtils.createAndCopyList(realms, RealmVO.class);
    }
}
