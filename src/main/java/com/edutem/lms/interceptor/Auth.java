package com.edutem.lms.interceptor;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Auth {	
	// 이와 같이 작성하면 메서드 위에 @Auth(role=Role.ADMIN)과 같이 작성 가능
	public enum Role {ROLE_ADMIN, ROLE_TEACHER, ROLE_STUDENT, ADMIN_TEACHER}	
	
	public Role role() default Role.ROLE_STUDENT;
}
