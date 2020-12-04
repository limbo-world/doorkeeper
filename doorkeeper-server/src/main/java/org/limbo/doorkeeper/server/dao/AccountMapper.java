/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
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

package org.limbo.doorkeeper.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.limbo.doorkeeper.api.model.param.AccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.server.entity.Account;

import java.util.List;

/**
 * @author Brozen
 * @date 2020/2/27 11:39 AM
 * @email brozen@qq.com
 */
public interface AccountMapper extends BaseMapper<Account> {

    long pageVOCount(AccountQueryParam param);

    List<AccountVO> pageVOS(AccountQueryParam param);

}
