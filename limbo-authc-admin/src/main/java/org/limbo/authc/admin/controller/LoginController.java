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

package org.limbo.authc.admin.controller;

import org.limbo.authc.admin.beans.po.AdminBLogPO;
import org.limbo.authc.admin.dubbo.consumers.AuthenticationDubboConsumer;
import org.limbo.authc.admin.service.CaptchaService;
import org.limbo.authc.admin.support.blog.BLog;
import org.limbo.authc.admin.support.blog.BLogType;
import org.limbo.authc.admin.support.session.AdminSession;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.vo.AccountVO;
import org.limbo.authc.api.interfaces.beans.vo.CaptchaVO;
import org.limbo.authc.api.interfaces.utils.verify.Verifies;
import org.limbo.authc.session.SessionAccount;
import org.limbo.authc.session.utils.HiJackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author devil
 * @date 2020/3/11
 */
@RestController
@RequestMapping("/login")
public class LoginController extends BaseController {

    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private AuthenticationDubboConsumer authenticationDubboProvider;

    @PostMapping
    public Response<AdminSession> login(@RequestBody AccountVO.LoginParam param) {
        Verifies.verify(captchaService.verifyCaptcha(param.getCaptchaToken(), param.getCaptcha()), "验证码错误！");
        Response<SessionAccount> authcResponse = authenticationDubboProvider.login(param);
        if (authcResponse.ok()) {
            // 创建会话
            AdminSession session = sessionDAO.createSession(authcResponse.getData());

            // 记录登录日志
            AdminBLogPO log = new AdminBLogPO();
            log.setAccountId(session.getAccount().getAccountId());
            log.setAccountNick(session.getAccount().getAccountNick());
            log.setLogName("登录");
            log.setLogType(BLogType.LOGIN.name());
            log.setIp(clientIp());
            log.setParam(HiJackson.toJson(param));
            log.setSession(HiJackson.toJson(session));
            bLogService.addLog(log);

            return Response.ok(session);
        } else {
            return Response.unauthenticated(authcResponse.getMsg());
        }
    }

    @GetMapping("captcha")
    public Response<CaptchaVO> captcha() {
        return Response.ok(captchaService.generateCaptcha());
    }
}
