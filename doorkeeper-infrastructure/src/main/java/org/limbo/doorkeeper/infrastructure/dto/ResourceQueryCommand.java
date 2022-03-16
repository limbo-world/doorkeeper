package org.limbo.doorkeeper.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceQueryCommand {

    private Long realmId;

    private Long namespaceId;

    private List<Long> resourceIds;
    /**
     * 名称 精确查询
     */
    private List<String> names;

    private Boolean isEnabled;
}
