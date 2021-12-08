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

package org.limbo.doorkeeper.server.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.dto.param.add.PolicyAddParam;
import org.limbo.doorkeeper.api.dto.param.batch.PolicyBatchUpdateParam;
import org.limbo.doorkeeper.api.dto.param.query.PolicyQueryParam;
import org.limbo.doorkeeper.api.dto.param.update.PolicyUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.PageVO;
import org.limbo.doorkeeper.api.dto.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.infrastructure.dao.PermissionPolicyDao;
import org.limbo.doorkeeper.server.infrastructure.dao.PolicyGroupDao;
import org.limbo.doorkeeper.server.infrastructure.dao.PolicyRoleDao;
import org.limbo.doorkeeper.server.infrastructure.dao.PolicyUserDao;
import org.limbo.doorkeeper.server.infrastructure.exception.ParamException;
import org.limbo.doorkeeper.server.infrastructure.mapper.NamespaceMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.PermissionPolicyMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.policy.PolicyGroupMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.policy.PolicyMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.policy.PolicyRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.policy.PolicyUserMapper;
import org.limbo.doorkeeper.server.infrastructure.po.*;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @since 2021/1/6 5:16 下午
 */
@Service
public class PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PolicyRoleDao policyRoleDao;

    @Autowired
    private PolicyUserDao policyUserDao;

    @Autowired
    private PolicyGroupDao policyGroupDao;

    @Autowired
    private NamespaceMapper namespaceMapper;

    @Autowired
    private PermissionPolicyMapper permissionPolicyMapper;

    @Autowired
    private PolicyGroupMapper policyGroupMapper;

    @Autowired
    private PolicyUserMapper policyUserMapper;

    @Autowired
    private PolicyRoleMapper policyRoleMapper;

    @Autowired
    private PermissionPolicyDao permissionPolicyDao;

    @Transactional
    public PolicyVO add(Long realmId, Long clientId, PolicyAddParam param) {
        Verifies.notNull(param.getType(), "策略类型不存在");

        NamespacePO client = namespaceMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");

        PolicyPO policy = EnhancedBeanUtils.createAndCopy(param, PolicyPO.class);
        policy.setRealmId(client.getRealmId());
        policy.setClientId(client.getNamespaceId());
        try {
            policyMapper.insert(policy);
        } catch (DuplicateKeyException e) {
            throw new ParamException("策略已存在");
        }
        switch (param.getType()) {
            case ROLE:
                policyRoleDao.batchSave(policy.getPolicyId(), param.getRoles());
                break;
            case USER:
                policyUserDao.batchSave(policy.getPolicyId(), param.getUsers());
                break;
            case GROUP:
                policyGroupDao.batchSave(policy.getPolicyId(), param.getGroups());
                break;
        }
        if (CollectionUtils.isNotEmpty(param.getPermissionIds())) {
            List<PermissionPolicyPO> params = new ArrayList<>();
            for (Long permissionId : param.getPermissionIds()) {
                PermissionPolicyPO addParam = new PermissionPolicyPO();
                addParam.setPermissionId(permissionId);
                addParam.setPolicyId(policy.getPolicyId());
                params.add(addParam);
            }
            permissionPolicyDao.batchSave(params);
        }
        return EnhancedBeanUtils.createAndCopy(policy, PolicyVO.class);
    }

    @Transactional
    public void batchUpdate(Long realmId, Long clientId, PolicyBatchUpdateParam param) {
        switch (param.getType()) {
            case UPDATE:
                if (CollectionUtils.isEmpty(param.getPolicyIds())) {
                    return;
                }
                policyMapper.update(null, Wrappers.<PolicyPO>lambdaUpdate()
                        .set(param.getIsEnabled() != null, PolicyPO::getIsEnabled, param.getIsEnabled())
                        .in(PolicyPO::getPolicyId, param.getPolicyIds())
                        .eq(PolicyPO::getRealmId, realmId)
                        .eq(PolicyPO::getClientId, clientId)
                );
                break;
            case DELETE:
                if (CollectionUtils.isEmpty(param.getPolicyIds())) {
                    return;
                }
                List<PolicyPO> policies = policyMapper.selectList(Wrappers.<PolicyPO>lambdaQuery()
                        .select(PolicyPO::getPolicyId)
                        .eq(PolicyPO::getRealmId, realmId)
                        .eq(PolicyPO::getClientId, clientId)
                        .in(PolicyPO::getPolicyId, param.getPolicyIds())
                );
                if (org.apache.commons.collections4.CollectionUtils.isEmpty(policies)) {
                    return;
                }
                List<Long> policyIds = policies.stream().map(PolicyPO::getPolicyId).collect(Collectors.toList());
                policyMapper.deleteBatchIds(policyIds);
                permissionPolicyMapper.delete(Wrappers.<PermissionPolicyPO>lambdaQuery()
                        .in(PermissionPolicyPO::getPolicyId, policyIds)
                );
                policyGroupMapper.delete(Wrappers.<PolicyGroupPO>lambdaQuery()
                        .in(PolicyGroupPO::getPolicyId, policyIds)
                );
                policyUserMapper.delete(Wrappers.<PolicyUserPO>lambdaQuery()
                        .in(PolicyUserPO::getPolicyId, policyIds)
                );
                policyRoleMapper.delete(Wrappers.<PolicyRolePO>lambdaQuery()
                        .in(PolicyRolePO::getPolicyId, policyIds)
                );
                break;
            default:
                break;
        }
    }

    public PageVO<PolicyVO> page(Long realmId, Long clientId, PolicyQueryParam param) {
        IPage<PolicyPO> mpage = MyBatisPlusUtils.pageOf(param);
        mpage = policyMapper.selectPage(mpage, Wrappers.<PolicyPO>lambdaQuery()
                .eq(PolicyPO::getRealmId, realmId)
                .eq(PolicyPO::getClientId, clientId)
                .eq(StringUtils.isNotBlank(param.getName()), PolicyPO::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getDimName()), PolicyPO::getName, param.getDimName())
                .eq(param.getIsEnabled() != null, PolicyPO::getIsEnabled, param.getIsEnabled())
                .eq(param.getType() != null, PolicyPO::getType, param.getType())
                .orderByDesc(PolicyPO::getPolicyId)
        );


        PageVO<PolicyVO> result = PageVO.convertByPage(param);
        result.setTotal(mpage.getTotal());
        result.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), PolicyVO.class));
        return result;
    }

    public PolicyVO get(Long realmId, Long clientId, Long policyId) {
        PolicyPO policy = policyMapper.getById(realmId, clientId, policyId);
        Verifies.notNull(policy, "策略不存在");
        PolicyVO result = EnhancedBeanUtils.createAndCopy(policy, PolicyVO.class);
        switch (policy.getType()) {
            case ROLE:
                result.setRoles(policyRoleDao.getByPolicy(policyId));
                break;
            case USER:
                result.setUsers(policyUserDao.getByPolicy(policyId));
                break;
            case GROUP:
                result.setGroups(policyGroupDao.getByPolicy(policyId));
                break;
        }

        return result;
    }

    @Transactional
    public void update(Long realmId, Long clientId, Long policyId, PolicyUpdateParam param) {
        PolicyPO policy = policyMapper.getById(realmId, clientId, policyId);
        Verifies.notNull(policy, "策略不存在");

        try {
            policyMapper.update(null, Wrappers.<PolicyPO>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()), PolicyPO::getName, param.getName())
                    .set(param.getDescription() != null, PolicyPO::getDescription, param.getDescription())
                    .set(param.getLogic() != null, PolicyPO::getLogic, param.getLogic())
                    .set(param.getIntention() != null, PolicyPO::getIntention, param.getIntention())
                    .set(param.getIsEnabled() != null, PolicyPO::getIsEnabled, param.getIsEnabled())
                    .eq(PolicyPO::getPolicyId, policyId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("策略已存在");
        }

        switch (policy.getType()) {
            case ROLE:
                policyRoleDao.update(policyId, param.getRoles());
                break;
            case USER:
                policyUserDao.update(policyId, param.getUsers());
                break;
            case GROUP:
                policyGroupDao.update(policyId, param.getGroups());
                break;
        }
    }

}
