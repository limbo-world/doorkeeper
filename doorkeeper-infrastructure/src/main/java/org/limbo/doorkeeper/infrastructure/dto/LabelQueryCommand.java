package org.limbo.doorkeeper.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yuansheng
 * @since 2022/2/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelQueryCommand {
    /**
     * 标签名，精确查询
     */
    private String key;

    /**
     * 标签名，模糊查询
     */
    private String dimKey;

    /**
     * 标签值，精确查询
     */
    private String value;

    /**
     * 标签值，模糊查询
     */
    private String dimValue;

    /**
     * k=v形式，精确查询 同时满足才返回
     */
    private List<String> andKeyValues;

    /**
     * k=v形式，精确查询 满足其中一个就返回
     */
    private List<String> orKeyValues;
}
