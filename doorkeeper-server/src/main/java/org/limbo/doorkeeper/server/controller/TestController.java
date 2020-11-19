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
