/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/LimboHome).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.authc.api.interfaces.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Brozen
 * @date 2020/3/11 8:52 AM
 * @email brozen@qq.com
 */
@Slf4j
public class ServletUtils {

    public static void writeToResponse(HttpServletResponse resp, String msg) {
        try (PrintWriter pw = resp.getWriter()) {
            pw.write(msg);
            pw.flush();
        } catch (Exception e) {
            log.error("writeToResponse exception: ", e);
        }
    }

}
