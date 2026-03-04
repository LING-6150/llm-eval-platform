package com.yuyuan.thumb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pine
 */
@RestController
@RequestMapping("index")
public class IndexController {

    @GetMapping
    public String index() {
        return "hello world";
    }

}
