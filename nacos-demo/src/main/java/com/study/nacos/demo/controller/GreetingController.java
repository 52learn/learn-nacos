package com.study.nacos.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GreetingController {

    @GetMapping("/say")
    public String sayHello(HttpServletRequest request){

        return " hello , (feigClient request ip :"+request.getRemoteAddr()+")";
    }
}
