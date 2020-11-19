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

package org.limbo.doorkeeper.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuqingtong
 * @date 2020/11/19 19:35
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @Operation(summary = "这是一个get请求")
    public void get() {}

    @PostMapping
    @Operation(summary = "这是一个post请求")
    public void post() {}

    @PutMapping
    @Operation(summary = "这是一个put请求")
    public void put() {}

    @DeleteMapping
    @Operation(summary = "这是一个delete请求")
    public void delete() {}

}
