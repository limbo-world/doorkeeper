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
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.PolicyType;
import org.limbo.doorkeeper.api.model.param.policy.PolicyAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyParamAddParam;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.constants.HttpMethod;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.RealmMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.Realm;
import org.limbo.doorkeeper.server.service.policy.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 * @date 2021/1/10 10:39 上午
 */
@Service
public class DoorkeeperService {

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private PolicyService policyService;

    @Transactional
    public void createRealm(Long userId, Long realmId, String realmName) {
        Realm dkRealm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
                .eq(Realm::getName, DoorkeeperConstants.REALM_NAME)
        );

        Client client = new Client();
        client.setRealmId(dkRealm.getRealmId());
        client.setName(realmName);
        client.setIsEnabled(true);
        clientMapper.insert(client);

        // uri资源

        // http策略
        List<PolicyAddParam> httpPolicy = createHttpPolicy();
        for (PolicyAddParam policyAddParam : httpPolicy) {
            policyService.add(dkRealm.getRealmId(), client.getClientId(), policyAddParam);
        }

    }

    @Transactional
    public void creatClient(Long userId, Long realmId, Long clientId, String clientName) {
        Realm dkRealm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
                .eq(Realm::getName, DoorkeeperConstants.REALM_NAME)
        );

        // todo client相关操作
        // 1. 委托方角色 2. 资源 3. 策略 4 权限

        // http策略
        List<PolicyAddParam> httpPolicy = createHttpPolicy();
        for (PolicyAddParam policyAddParam : httpPolicy) {
            policyService.add(dkRealm.getRealmId(), clientId, policyAddParam);
        }
    }

    /**
     * 基于http的策略
     */
    private List<PolicyAddParam> createHttpPolicy() {
        List<PolicyAddParam> policyAddParams = new ArrayList<>();
        for (HttpMethod httpMethod : HttpMethod.values()) {
            PolicyAddParam policy = new PolicyAddParam();
            policy.setName(httpMethod.name() + "请求");
            policy.setType(PolicyType.PARAM);
            policy.setIntention(Intention.ALLOW);
            policy.setIsEnabled(Boolean.TRUE);

            List<PolicyParamAddParam>  params = new ArrayList<>();
            PolicyParamAddParam param = new PolicyParamAddParam();
            param.setK(HttpMethod.class.getSimpleName());
            param.setV(httpMethod.name());
            params.add(param);

            policy.setParams(params);

            policyAddParams.add(policy);
        }

        return policyAddParams;
    }

//    private List<ResourceAddParam> createRealmResource() {
//
//    }



}
