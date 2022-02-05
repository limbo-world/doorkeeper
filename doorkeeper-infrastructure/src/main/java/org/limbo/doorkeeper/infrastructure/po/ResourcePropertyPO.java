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
@TableName("doorkeeper_resource_property")
public class ResourcePropertyPO {

    @TableId(type = IdType.AUTO)
    private Long resourcePropertyId;

    private Long resourceId;

    private Long propertyId;

}
