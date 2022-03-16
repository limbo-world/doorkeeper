package org.limbo.doorkeeper.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuansheng
 * @since 2022/2/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResourceDTO {

    private Long permissionResourceId;

    private Long permissionId;

    private Long resourceId;

}
