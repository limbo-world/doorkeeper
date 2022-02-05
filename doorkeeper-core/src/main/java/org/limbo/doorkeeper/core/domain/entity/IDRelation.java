package org.limbo.doorkeeper.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.limbo.doorkeeper.core.domain.constants.IDRelationEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IDRelation {

    private Long id;

    private IDRelationEnum relation;

    private List<Long> relatedIds;

    public Long getId() {
        return id;
    }

    public IDRelationEnum getRelation() {
        return relation;
    }

    public List<Long> getRelatedIds() {
        return relatedIds == null ? new ArrayList<>() : relatedIds;
    }
}
