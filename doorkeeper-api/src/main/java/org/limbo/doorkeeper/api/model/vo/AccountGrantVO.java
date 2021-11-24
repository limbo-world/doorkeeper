package org.limbo.doorkeeper.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author liuqingtong
 * @date 2020/11/25 20:27
 */
@Data
public class AccountGrantVO {

    @Schema(description ="用户ID")
    private Long accountId;

    @Schema(description ="用户授予的角色")
    private List<RoleVO> roles;

    @Schema(description ="用户可以访问的权限")
    private List<PermissionVO> allowedPermissions;

    @Schema(description ="用户禁止访问的权限")
    private List<PermissionVO> refusedPermissions;

}
