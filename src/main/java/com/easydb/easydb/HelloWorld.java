package com.easydb.easydb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hello")
public class HelloWorld {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World2!";
    }
}
