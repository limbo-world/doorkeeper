package org.limbo.doorkeeper.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 域 不同域有隔离性
 *
 * @author Devil
 * @since 2020/12/29 3:49 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("doorkeeper_realm")
public class RealmPO {

    @TableId(type = IdType.AUTO)
    private Long realmId;

    private String name;

    private String secret;

    private Date createTime;

    private Date updateTime;

}
