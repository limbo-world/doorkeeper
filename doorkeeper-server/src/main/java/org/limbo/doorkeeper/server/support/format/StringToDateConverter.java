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

package org.limbo.doorkeeper.server.support.format;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Devil
 * @date 2020/11/24 11:50 AM
 */
public class StringToDateConverter implements Converter<String, Date> {

    public static final String YMD_REGEXP = "\\d{4}-\\d{2}-\\d{2}";
    public static final String YMD_FORMAT = "yyyy-MM-dd";

    public static final String HMS_REGEXP = "\\d{2}:\\d{2}:\\d{2}";
    public static final String HMS_FORMAT = "HH-mm-ss";

    public static final String YMD_HMS_REGEXP = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
    public static final String YMD_HMS_FORMAT = "yyyy-MM-dd HH:mm:ss";


    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        try {
            if (source.matches(YMD_HMS_REGEXP)) {
                return new SimpleDateFormat(YMD_HMS_FORMAT).parse(source);
            }
            if (source.matches(YMD_REGEXP)) {
                return new SimpleDateFormat(YMD_FORMAT).parse(source);
            }
            if (source.matches(HMS_REGEXP)) {
                return new SimpleDateFormat(HMS_FORMAT).parse(source);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }

        throw new IllegalArgumentException();
    }
}
