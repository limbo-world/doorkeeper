/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.admin.service.impl;

import com.github.botaruibo.xvcode.generator.Generator;
import com.github.botaruibo.xvcode.generator.GifVCGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.admin.model.param.LoginParam;
import org.limbo.doorkeeper.admin.model.vo.CaptchaVO;
import org.limbo.doorkeeper.admin.service.LoginService;
import org.limbo.doorkeeper.admin.utils.UUIDUtils;
import org.limbo.doorkeeper.admin.utils.Verifies;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * @author Devil
 * @date 2020/11/23 8:04 PM
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private static final String CAPTCHA_REDIS_KEY = "LIMBO_AUTHC_CAPTCHA::TOKEN";

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void login(LoginParam param) {
        Verifies.verify(verifyCaptcha(param.getCaptchaToken(), param.getCaptcha()), "验证码错误！");
    }

    @Override
    public CaptchaVO generateCaptcha() {
        Integer width = 200;
        Integer height = 40;
        Integer count = 5;  // 验证码字符数量
        Generator generator = new GifVCGenerator(width, height, count);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayOutputStream os = (ByteArrayOutputStream) generator.write2out(baos);
             ByteArrayInputStream swapStream = new ByteArrayInputStream(os.toByteArray())) {

            // 生成
            byte[] data = new byte[swapStream.available()];
            IOUtils.read(swapStream, data);
            CaptchaVO captcha = new CaptchaVO(UUIDUtils.get(),
                    "data:image/gif;base64," + Base64.getEncoder().encodeToString(data));

            // 缓存，2分钟内token有效
            RBucket<String> bucket = redissonClient.getBucket(CAPTCHA_REDIS_KEY + "::" + captcha.getToken());
            bucket.set(generator.text(), 2, TimeUnit.MINUTES);

            return captcha;
        } catch (IOException e) {
            log.error("获取验证码失败", e);
            throw new IllegalStateException("生成验证码失败！", e);
        }
    }

    private boolean verifyCaptcha(String token, String captcha) {
        RBucket<String> bucket = redissonClient.getBucket(CAPTCHA_REDIS_KEY + "::" + token);
        if (bucket != null) {
            String cachedCaptcha = bucket.get();
            bucket.delete();
            return StringUtils.equalsIgnoreCase(cachedCaptcha, captcha);
        }

        return false;
    }
}
