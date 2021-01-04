package org.limbo.doorkeeper.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 域 一个域中的配置会有交集 不同域有隔离性
 *
 * @author Devil
 * @date 2020/12/29 3:49 下午
 */
@Data
@TableName("realm")
public class Realm {
    @TableId(type = IdType.AUTO)
    private Long realmId;

    private String name;

    private Boolean isAdmin;

    private Date createTime;

    private Date updateTime;
}
