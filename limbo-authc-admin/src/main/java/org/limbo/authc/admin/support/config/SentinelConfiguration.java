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

package org.limbo.authc.admin.support.config;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.fasterxml.jackson.core.type.TypeReference;
import org.limbo.authc.session.utils.HiJackson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

/**
 * @author Brozen
 * @date 2020/3/31 12:27 PM
 * @email brozen@qq.com
 */
@Configuration
public class SentinelConfiguration implements InitializingBean {

    @Value("${nacos.config.server-addr}")
    private String nacosAddress;

    @Value("${nacos.config.namespace}")
    private String namespace;

    @Value("${nacos.config.group}")
    private String groupId;

    // 限流规则dataId
    private String flowRuleDataId = "sentinel-flow";

    // 降级规则dataId
    private String degradeRuleDataId = "sentinel-flow";

    @Override
    public void afterPropertiesSet() {
        // 从Nacos读取限流配置
        Properties nacosProps = new Properties();
        nacosProps.put("serverAddr", nacosAddress);
        nacosProps.put("namespace", namespace);
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(
                nacosProps, groupId, flowRuleDataId, source -> HiJackson.fromJson(source, new TypeReference<List<FlowRule>>() {}));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        // 熔断降级配置也可以从Nacos读取，使用DegradeRuleManager.register2Property();
        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new NacosDataSource<List<DegradeRule>>(
                nacosProps, groupId, degradeRuleDataId, source -> HiJackson.fromJson(source, new TypeReference<List<DegradeRule>>() {}));
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
    }

}
