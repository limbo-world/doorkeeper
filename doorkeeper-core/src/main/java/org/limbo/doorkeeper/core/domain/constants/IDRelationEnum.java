package org.limbo.doorkeeper.core.domain.constants;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
public enum IDRelationEnum {

    RESOURCE_PERMISSION("resource_permission", "资源ID关联的所有权限ID"),
    PERMISSION_POLICY("permission_policy", "权限ID关联的所有策略ID"),
    ;

    /**
     * 联系
     */
    private final String relation;
    /**
     * 描述
     */
    private final String description;

    IDRelationEnum(String relation, String description) {
        this.relation = relation;
        this.description = description;
    }

    public String getRelation() {
        return relation;
    }

    public String getDescription() {
        return description;
    }
}
