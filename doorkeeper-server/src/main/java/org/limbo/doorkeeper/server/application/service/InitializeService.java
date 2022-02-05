package org.limbo.doorkeeper.server.application.service;

import org.limbo.doorkeeper.api.constants.BatchMethod;
import org.limbo.doorkeeper.api.dto.param.add.RoleAddParam;
import org.limbo.doorkeeper.api.dto.param.batch.UserRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.RoleVO;
import org.limbo.doorkeeper.common.constant.DoorkeeperConstants;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.NamespaceMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.RealmMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.UserMapper;
import org.limbo.doorkeeper.server.infrastructure.po.NamespacePO;
import org.limbo.doorkeeper.server.infrastructure.po.RealmPO;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.utils.encryption.MD5Utils;
import org.limbo.utils.strings.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * @author Devil
 * @since 2021/11/30
 */
@Service
public class InitializeService {

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private NamespaceMapper namespaceMapper;

    @Autowired
    private DoorkeeperService doorkeeperService;

    /**
     * 系统数据初始化
     * @return 是否完成初始化进程 false表示以前已经进行过初始化
     */
    @Transactional
    public boolean initDoorkeeper() {
        RealmPO realm = new RealmPO();
        realm.setName(DoorkeeperConstants.DOORKEEPER_REALM_NAME);
        realm.setSecret(UUIDUtils.randomID());
        try {
            realmMapper.insert(realm);
        } catch (DuplicateKeyException e) {
            return false;
        }

        // 创建管理员账户
        UserPO admin = new UserPO();
        admin.setRealmId(realm.getRealmId());
        admin.setNickname(DoorkeeperConstants.ADMIN);
        admin.setUsername(DoorkeeperConstants.ADMIN);
        admin.setPassword(MD5Utils.md5AndHex(DoorkeeperConstants.ADMIN, StandardCharsets.UTF_8));
        admin.setIsEnabled(true);
        userMapper.insert(admin);
        // 创建doorkeeper超管角色
        RoleAddParam realmAdminRoleParam =  RoleAddParam.builder()
                .namespaceId(DoorkeeperConstants.REALM_ROLE_ID)
                .name(DoorkeeperConstants.ADMIN)
                .build();

        RoleVO realmAdminRole = roleService.add(realm.getRealmId(), realmAdminRoleParam);
        // 绑定管理员和角色
        UserRoleBatchUpdateParam userRoleBatchUpdateParam = new UserRoleBatchUpdateParam();
        userRoleBatchUpdateParam.setType(BatchMethod.SAVE);
        userRoleBatchUpdateParam.setRoleIds(Collections.singletonList(realmAdminRole.getRoleId()));
        userRoleService.batchUpdate(admin.getUserId(), userRoleBatchUpdateParam);
        // 创建api client
        NamespacePO apiClient = new NamespacePO();
        apiClient.setRealmId(realm.getRealmId());
        apiClient.setName(DoorkeeperConstants.API_CLIENT);
        apiClient.setDescription("manager doorkeeper permission");
        apiClient.setIsEnabled(true);
        namespaceMapper.insert(apiClient);
        // 资源数据
        doorkeeperService.createRealmResource(admin.getUserId(), realm.getRealmId(), realm.getName());
        doorkeeperService.createClientResource(admin.getUserId(), realm.getRealmId(), apiClient.getNamespaceId(), apiClient.getName());
        return true;
    }
}
