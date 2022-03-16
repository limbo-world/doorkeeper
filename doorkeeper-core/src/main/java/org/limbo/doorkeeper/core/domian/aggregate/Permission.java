package org.limbo.doorkeeper.core.domian.aggregate;

import lombok.Getter;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
@Getter
public class Permission {

    private Long id;

    private Long realmId;

    private Long namespaceId;

    private String name;

    private String description;
    /**
     * 判断逻辑
     */
    private Logic logic;
    /**
     * 执行逻辑
     */
    private Intention intention;
    /**
     * 是否启用
     */
    private boolean isEnabled;

    private List<Long> resourceIds;

    private List<Long> policyIds;

    public List<Long> getResourceIds() {
        return resourceIds == null ? new ArrayList<>() : resourceIds;
    }

    public List<Long> getPolicyIds() {
        return policyIds == null ? new ArrayList<>() : policyIds;
    }
}
