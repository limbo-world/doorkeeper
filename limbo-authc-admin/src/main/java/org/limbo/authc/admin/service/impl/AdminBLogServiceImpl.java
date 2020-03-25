/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.limbo.authc.admin.beans.po.AdminBLogPO;
import org.limbo.authc.admin.beans.vo.AdminBLogVO;
import org.limbo.authc.admin.dao.AdminBLogMapper;
import org.limbo.authc.admin.service.AdminBLogService;
import org.limbo.authc.admin.utils.MyBatisPlusUtils;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.utils.EnhancedBeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Brozen
 * @date 2020/3/23 12:31 PM
 * @email brozen@qq.com
 */
@Service
public class AdminBLogServiceImpl extends ServiceImpl<AdminBLogMapper, AdminBLogPO> implements AdminBLogService {

    @Override
    @Transactional
    public void addLog(AdminBLogPO log) {
        baseMapper.insert(log);
    }

    @Override
    public Page<AdminBLogVO> queryBLog(AdminBLogVO.QueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AdminBLogPO> page = MyBatisPlusUtils.pageOf(param);
        Wrapper<AdminBLogPO> condition = Wrappers.<AdminBLogPO>lambdaQuery()
                .like(StringUtils.isNotBlank(param.getKeyword()), AdminBLogPO::getLogName, param.getKeyword())
                .eq(param.getAccountId() != null, AdminBLogPO::getAccountId, param.getAccountId())
                .eq(param.getType() != null, AdminBLogPO::getLogType, param.getType())
                .eq(param.getProjectId() != null, AdminBLogPO::getProjectId, param.getProjectId())
                .orderByDesc(AdminBLogPO::getGmtCreated);

        page = baseMapper.selectPage(page, condition);
        param.setTotal(page.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(page.getRecords(), AdminBLogVO.class));

        return param;
    }
}
