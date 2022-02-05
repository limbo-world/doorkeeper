package org.limbo.doorkeeper.core.domain.component.checker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuansheng
 * @since 2022/2/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckResult<T> {

    private T result;
}
