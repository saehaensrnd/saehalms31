package com.edutem.lms.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
 
 
@Component
public class LoginInterceptor implements HandlerInterceptor {
 
    //private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
      
    // 클라이언트의 요청을 컨트롤러에 전달하기 전에 호출된다. 여기서 false를 리턴하면 다음 내용(Controller)을 실행하지 않는다.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	// 1. handler 종류 확인
		// Controller에 있는 메서드이므로 HandlerMethod 타입인지 체크
		if( handler instanceof HandlerMethod == false ) {
			// return true이면  Controller에 있는 메서드가 아니므로, 그대로 컨트롤러로 진행
			return true;
		}
    	
		// 2.형 변환
		HandlerMethod handlerMethod = (HandlerMethod)handler;
		
		// 3. @Auth 받아오기
		Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
    	
		// 4. method에 @Auth가 없는 경우, 즉 인증이 필요 없는 요청 
		if( auth == null ) {
			//System.out.println("Auth 어노테이션 없는 url 접근");
			
			//return true;
			//logger.info("LoginInterceptor - {}", "호출완료");
			
			HttpSession session = request.getSession();
	        String memberId = (String)session.getAttribute("login_name");
	        
	        if(memberId != null) {
	            return true;
	        } else {
	              try { 
	                  //response.sendRedirect("/login"); 
	            	  response.sendRedirect("/login");
	              } catch (IOException e) {
	                  //e.printStackTrace(); 
	              }
	            return false;
	            
	        }
		} else {
			//System.out.println("Auth 어노테이션 !!");
			//System.out.println(auth);	//System.out.println(auth.role());
			
			HttpSession session = request.getSession();
	        String memberId = (String)session.getAttribute("login_name");	        
	        String userRole = (String)session.getAttribute("role_name").toString();
	        
	        if(memberId != null) {
	        	
	        	if(!auth.role().toString().equals(userRole)) {		// Auth 어노테이션이 붙은 url 요청이라면, 로그인 세션 userRole과 비교
	        		return false;
	        	}
	        	
	            return true;
	        } else {
	              try { 
	                  //response.sendRedirect("/login");
	                  response.sendRedirect("/login");
	              } catch (IOException e) {
	                  //e.printStackTrace(); 
	              }
	            return false;
	            
	        }
			
		}
    	
    	
    	
    	
    	
    	//logger.info("LoginInterceptor - {}", "호출완료");
        /*
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("login_name");
        
        if(memberId != null) {
            return true;
        } else {
              try { 
                  response.sendRedirect("/login"); 
              } catch (IOException e) {
                  e.printStackTrace(); 
              }
            return false;
            
        }
        */
    }

    // 클라이언트의 요청을 처리한 뒤에 호출된다. 컨트롤러에서 예외가 발생되면 실행되지 않는다.
   @Override
   public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
         ModelAndView modelAndView) throws Exception {
      // TODO Auto-generated method stub
      HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
   }

   // 클라이언트 요청을 마치고 클라이언트에서 뷰를 통해 응답을 전송한뒤 실행이 된다. 뷰를 생성할 때에 예외가 발생할 경우에도 실행이 된다.
   @Override
   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)         throws Exception {
      // TODO Auto-generated method stub
      HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
   }      

}