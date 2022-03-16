package org.limbo.doorkeeper.core.domian.aggregate;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.limbo.utils.encryption.MD5Utils;

import java.nio.charset.StandardCharsets;

/**
 * @author yuansheng
 * @since 2022/2/4
 */
@Getter
@Builder
@ToString
public class User {

    private Long userId;
    /**
     * 每个user绑定到一个realm
     */
    private Long realmId;
    /**
     * realm唯一 登录用
     */
    private String username;
    /**
     * 未加密密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 描述
     */
    private String description;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 是否启用
     */
    private Boolean isEnabled;

    @Tolerate
    public User() {
    }

    /**
     * 对密码加密处理
     * @return
     */
    public String encryptedPassword() {
        return MD5Utils.md5AndHex(password, StandardCharsets.UTF_8.name());
    }
}
