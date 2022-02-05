package org.limbo.doorkeeper.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Devil
 * @since 2021/12/1
 */
@Data
@TableName("doorkeeper_property")
public class PropertyPO {

    @TableId(type = IdType.AUTO)
    private Long propertyId;

    private Long realmId;

    private Long namespaceId;
    /**
     * key
     */
    private String key;
    /**
     * value
     */
    private String value;
}
