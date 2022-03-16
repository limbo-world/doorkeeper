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

package org.limbo.doorkeeper.application.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.constants.BatchMethod;
import org.limbo.doorkeeper.api.dto.param.add.UserAddParam;
import org.limbo.doorkeeper.api.dto.param.batch.UserRoleBatchUpdateParam;
import org.limbo.doorkeeper.api.dto.param.query.UserQueryParam;
import org.limbo.doorkeeper.api.dto.param.update.PasswordUpdateParam;
import org.limbo.doorkeeper.api.dto.param.update.UserUpdateParam;
import org.limbo.doorkeeper.api.dto.vo.PageVO;
import org.limbo.doorkeeper.api.dto.vo.UserVO;
import org.limbo.doorkeeper.infrastructure.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.GroupUserMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.NamespaceMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.RoleMapper;
import org.limbo.doorkeeper.infrastructure.dao.mybatis.mapper.UserMapper;
import org.limbo.doorkeeper.infrastructure.po.*;
import org.limbo.doorkeeper.server.useless.GroupDao;
import org.limbo.utils.encryption.MD5Utils;
import org.limbo.utils.mybatisplus.MyBatisPlusUtils;
import org.limbo.utils.reflection.EnhancedBeanUtils;
import org.limbo.utils.verifies.Verifies;
import org.limbo.utils.verifies.VerifyException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @since 2021/1/9 7:57 下午
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private GroupDao groupDao;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private DoorkeeperService doorkeeperService;

    @Resource
    private NamespaceMapper namespaceMapper;

    @Resource
    private GroupUserMapper groupUserMapper;

    @Transactional
    public UserVO add(Long realmId, UserAddParam param) {
        UserPO user = EnhancedBeanUtils.createAndCopy(param, UserPO.class);
        user.setRealmId(realmId);
        user.setPassword(MD5Utils.md5AndHex(param.getPassword(), StandardCharsets.UTF_8.name()));
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new VerifyException("用户已存在");
        }
        user.setPassword(null);

        // 用户组 参数添加 默认添加
        List<GroupPO> defaultGroup = groupDao.getDefaultGroup(realmId);
        if (CollectionUtils.isNotEmpty(defaultGroup)) {
            List<Long> groupIds = defaultGroup.stream().map(GroupPO::getGroupId).collect(Collectors.toList());
            batchSaveUserGroups(user.getUserId(), groupIds);
        }

        // 用户角色 参数添加 默认添加
        List<RolePO> defaultRole = roleMapper.selectList(Wrappers.<RolePO>lambdaQuery()
                .eq(RolePO::getRealmId, realmId)
                .eq(RolePO::getIsDefault, true)
        );
        if (CollectionUtils.isNotEmpty(defaultRole)) {
            List<Long> roleIds = defaultRole.stream().map(RolePO::getRoleId).collect(Collectors.toList());
            UserRoleBatchUpdateParam batchUpdateParam = new UserRoleBatchUpdateParam();
            batchUpdateParam.setType(BatchMethod.SAVE);
            batchUpdateParam.setRoleIds(roleIds);
            userRoleService.batchUpdate(user.getUserId(), batchUpdateParam);
        }

        // 如果是 doorkeeper 域下的用户 创建策略和权限
        if (doorkeeperService.getDoorkeeperRealmId().equals(realmId)) {
            NamespacePO apiClient = namespaceMapper.getByName(doorkeeperService.getDoorkeeperRealmId(), DoorkeeperConstants.API_CLIENT);
            doorkeeperService.bindUser(user.getUserId(), user.getUsername(), null, apiClient.getRealmId(), apiClient.getNamespaceId());
        }

        return EnhancedBeanUtils.createAndCopy(user, UserVO.class);
    }

    private void batchSaveUserGroups(Long userId, List<Long> groupIds) {
        List<GroupUserPO> groupUsers = new ArrayList<>();
        for (Long groupId : groupIds) {
            GroupUserPO groupUser = new GroupUserPO();
            groupUser.setGroupId(groupId);
            groupUser.setUserId(userId);
            groupUsers.add(groupUser);
        }
        groupUserMapper.batchInsertIgnore(groupUsers);
    }

    public PageVO<UserVO> page(Long realmId, UserQueryParam param) {
        IPage<UserPO> mpage = MyBatisPlusUtils.pageOf(param);
        mpage = userMapper.selectPage(mpage, Wrappers.<UserPO>lambdaQuery()
                .eq(UserPO::getRealmId, realmId)
                .eq(StringUtils.isNotBlank(param.getUsername()), UserPO::getUsername, param.getUsername())
                .eq(StringUtils.isNotBlank(param.getNickname()), UserPO::getNickname, param.getNickname())
                .and(StringUtils.isNotBlank(param.getDimName()), wrapper -> wrapper
                        .like(StringUtils.isNotBlank(param.getDimName()), UserPO::getUsername, param.getDimName())
                        .or()
                        .like(StringUtils.isNotBlank(param.getDimName()), UserPO::getNickname, param.getDimName())
                ).orderByDesc(UserPO::getUserId)
        );

        PageVO<UserVO> result = PageVO.convertByPage(param);
        result.setTotal(mpage.getTotal());
        result.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), UserVO.class));
        return result;
    }

    public UserVO get(Long realmId, Long userId, String username) {
        if (userId == null && StringUtils.isBlank(username)) {
            throw new VerifyException("未传递查询参数");
        }
        UserPO user;
        if (userId != null) {
            user = userMapper.selectOne(Wrappers.<UserPO>lambdaQuery()
                    .eq(UserPO::getUserId, userId)
                    .eq(UserPO::getRealmId, realmId)
            );
        } else {
            user = userMapper.selectOne(Wrappers.<UserPO>lambdaQuery()
                    .eq(UserPO::getUsername, username)
                    .eq(UserPO::getRealmId, realmId)
            );
        }
        if (user == null) {
            return null;
        }
        user.setPassword(null);
        return EnhancedBeanUtils.createAndCopy(user, UserVO.class);
    }

    @Transactional
    public void update(Long realmId, Long userId, UserUpdateParam param) {
        LambdaUpdateWrapper<UserPO> updateWrapper = Wrappers.<UserPO>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getNickname()), UserPO::getNickname, param.getNickname())
                .set(param.getDescription() != null, UserPO::getDescription, param.getDescription())
                .set(param.getIsEnabled() != null, UserPO::getIsEnabled, param.getIsEnabled())
                .set(param.getEmail() != null, UserPO::getEmail, param.getEmail())
                .set(param.getPhone() != null, UserPO::getPhone, param.getPhone())
                .set(param.getExtend() != null, UserPO::getExtend, param.getExtend())
                .eq(UserPO::getUserId, userId)
                .eq(UserPO::getRealmId, realmId);

        // 判断是否需要更新密码
        if (StringUtils.isNotBlank(param.getPassword())) {
            updateWrapper.set(UserPO::getPassword, MD5Utils.md5AndHex(param.getPassword(), StandardCharsets.UTF_8.name()));
        }
        userMapper.update(null, updateWrapper);
    }

    @Transactional
    public void changePassword(Long realmId, Long userId, PasswordUpdateParam param) {
        if (StringUtils.isBlank(param.getNewPassword())) {
            return;
        }
        UserPO user = userMapper.getById(realmId, userId);
        Verifies.notNull(user, "用户不存在");
        Verifies.verify(MD5Utils.verify(param.getOldPassword(), user.getPassword()), "密码错误");
        userMapper.update(null, Wrappers.<UserPO>lambdaUpdate()
                .set(UserPO::getPassword, MD5Utils.md5AndHex(param.getNewPassword(), StandardCharsets.UTF_8.name()))
                .eq(UserPO::getUserId, userId)
                .eq(UserPO::getRealmId, realmId)
        );
    }

}
