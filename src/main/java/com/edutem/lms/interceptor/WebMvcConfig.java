package com.edutem.lms.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
   

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    	// addPathPatterns : /** -> 모든 패던에 대해 검사 
    	// excludePathPatterns : interceptor 검사에서 예외시킬 url 패턴
        LoginInterceptor loginIntercepter = new LoginInterceptor();
        registry.addInterceptor(loginIntercepter)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**","/img/**","/js/**")
                .excludePathPatterns("/login", "/api/**", "/student/join/**", "/adminLogin")                
                .excludePathPatterns("/logout")
                .excludePathPatterns("/loginProcessing")
        		.excludePathPatterns("/adminLoginProcessing");
    }
}
