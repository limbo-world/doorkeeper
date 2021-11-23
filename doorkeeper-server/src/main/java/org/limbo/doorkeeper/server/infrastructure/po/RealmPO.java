package org.limbo.doorkeeper.server.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 域 一个域中的配置会有交集 不同域有隔离性
 *
 * @author Devil
 * @since 2020/12/29 3:49 下午
 */
@Data
@TableName("realm")
public class RealmPO {

    @TableId(type = IdType.AUTO)
    private Long realmId;

    private String name;

    private String secret;

    private Date createTime;

    private Date updateTime;

}
