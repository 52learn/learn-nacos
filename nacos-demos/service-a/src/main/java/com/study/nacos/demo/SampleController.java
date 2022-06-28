package com.study.nacos.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
class SampleController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Value("${user.name}")
	String userName;

	@Value("${user.age:25}")
	Integer age;

	@RequestMapping("/user")
	public String simple() {
		String result = "Hello Nacos Config!" + "Hello " + userName + " " + age;
		logger.info("======{}",result);
		return result ;
	}


}