/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.api.model;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brozen
 * @date 2020/3/6 8:35 AM
 * @email brozen@qq.com
 */
@Data
public class Page<T> {
    /**
     * 页码，从1开始
     */
    @Positive(message = "页码不可为负数")
    @Min(value = 1, message = "页码从1开始")
    @Parameter(description = "页码")
    private int current = 1;

    /**
     * 每页条数
     */
    @Positive(message = "条数不可为负数")
    @Max(value = 1000, message = "每页最多1000条数据")
    @Parameter(description = "每页条数，默认20，上限1000")
    private int size = 20;

    /**
     * 最大条数
     */
    public static final int MAX_SIZE = 1000;

    /**
     * 排序字段
     */
    @Parameter(description = "排序字段")
    private List<String> orderBy;

    /**
     * 排序字段的排序方式
     */
    @Parameter(description = "排序字段的排序方式")
    private List<String> sort;

    /**
     * 总条数，总条数若为非负数，则不进行总数查询
     */
    @Schema(description = "总条数，总条数若为非负数，则不进行总数查询")
    private long total = -1;

    /**
     * 当前页数据
     */
    @Schema(description = "当前页数据")
    private List<T> data;

    /**
     * 是否查询所有数据
     */
    @Parameter(description = "是否查询所有数据")
    private Boolean needAll;

    /**
     * 是否还有下一页
     */
    @Schema(description = "是否还有下一页")
    public Boolean getHasNext() {
        return total > current * size;
    }

    /**
     * 获取分页查询的偏移条数
     */
    public int getOffset() {
        return size * (current - 1);
    }

    /**
     * 得到下一页分页对象
     */
    public Page<T> next() {
        Page<T> next = new Page<>();
        next.current = this.current + 1;
        next.size = this.size;
        next.total = this.total;
        return next;
    }

    public List<T> getData() {
        return data == null ? new ArrayList<>() : data;
    }
}
