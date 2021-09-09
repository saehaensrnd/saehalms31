package com.edutem.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SaehaLmsApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SaehaLmsApplication.class);
	}	

	// spring boot 를 실행하는 main method
	public static void main(String[] args) {
		SpringApplication.run(SaehaLmsApplication.class, args);
	}

}
