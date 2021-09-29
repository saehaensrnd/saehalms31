package com.edutem.lms.controller;

import java.io.IOException;
import java.text.ParseException;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.MapUtils;

import com.edutem.lms.component.Common;
import com.edutem.lms.component.S3Wrapper;
import com.edutem.lms.mapper.AdminMapper;
import com.edutem.lms.paging.ApplicationCriteria;
import com.edutem.lms.paging.ApplicationPageMaker;
import com.edutem.lms.paging.AssignCriteria;
import com.edutem.lms.paging.AssignPageMaker;
import com.edutem.lms.paging.ClassCriteria;
import com.edutem.lms.paging.ClassDetailCriteria;
import com.edutem.lms.paging.ClassDetailPageMaker;
import com.edutem.lms.paging.ClassPageMaker;
import com.edutem.lms.paging.Criteria;
import com.edutem.lms.paging.NoticeCriteria;
import com.edutem.lms.paging.NoticePageMaker;
import com.edutem.lms.paging.PageMaker;
import com.edutem.lms.paging.ScheduleCriteria;
import com.edutem.lms.paging.SchedulePageMaker;

@Controller
public class SaehaLmsController {

	@Autowired
	private AdminMapper adminMapper;
	@Autowired
	private S3Wrapper s3Wrapper;
	

	@RequestMapping("/")
	public ModelAndView index() {
		
		
		ModelAndView mav = new ModelAndView();
//		//edutem개발
//		//mav.setViewName("redirect:home");
		mav.setViewName("redirect:home");
//		
return mav;
	}
	
	/* @RequestMapping("/") // 
	 public ModelAndView index() { 
		 
		 ModelAndView mav = new ModelAndView(); 
		 mav.setViewName("page/common/home");  
		 
	 return mav;
	  }*/
	 	
	// 학생로그인 페이지 loginProcessing 사용
	@RequestMapping("/login")
	public ModelAndView login(@RequestParam Map<String, Object> paramMap, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/common/login");		
		
		// url에 site_code를 가지고 접속했다면 site_code 세션에 저장하여 관리
		if(paramMap.containsKey("site_code")) {
			mav.addObject("site_code", paramMap.get("site_code"));
			session.setAttribute("site_code", paramMap.get("site_code"));
		} else {
			session.setAttribute("site_code", "");
		}
		
		return mav;
	}
	
	// 관리자로그인 페이지 adminLoginProcessing 사용
	@RequestMapping("/adminLogin")
	public ModelAndView adminLogin(@RequestParam Map<String, Object> paramMap, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/common/adminLogin");
		
		return mav;
	}

	// 아이디 중복 체크
	@RequestMapping(value = "/idCheck", method = { RequestMethod.POST })
	@ResponseBody
	public String idCheck(@RequestParam("userId") String userId) throws Exception {
		// 중복 아이디 존재한다면
		if (adminMapper.idCheck(userId) == 1)
			return "exist";
		// 중복 아니면 "ok" 리턴
		return "ok";
	}

	// 학생 로그인 (학생로그인 url에서 로그인 시도 - 스프링 인터셉터 사용)
	@RequestMapping("/loginProcessing")
	public ModelAndView loginProcessing(@RequestParam Map<String, Object> paramMap, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		
		String password = paramMap.get("password").toString();
		
		// 학생만 조회
		Map<String, Object> user = new HashMap<String, Object>();
		//user = adminMapper.selectUserById(paramMap);
		user = adminMapper.selectUserByStudent(paramMap);
		
		// 암호화된 비밀번호 - 입력받은 비밀번호 pwd가 동일한 형식의 hashCode인지..	 id / password
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if(!encoder.matches(password, user.get("pwd").toString())) {
			// 비밀번호가 동일하지 않다면			
			user = new HashMap<String, Object>();	// 아이디 비밀번호 불일치 -> 초기화 
		}
		
		
		try {
			if (!MapUtils.isEmpty(user)) {	// 암호가 동일하여 정상적으로 로그인 됐다면
				// 로그인 성공
				session.setAttribute("login_no", user.get("user_no"));
				session.setAttribute("login_name", user.get("name"));
				session.setAttribute("login_eng_name", user.get("eng_name"));
				session.setAttribute("role_name", user.get("role_name"));
				session.setAttribute("role_no", user.get("role"));

				// 세션 시간 설정 (초단위) 1000시간
				session.setMaxInactiveInterval(3600000);
				// System.out.println(session.getMaxInactiveInterval());

				mav.setViewName("redirect:home");
				//mav.setViewName("redirect:/login");

			} else {
				// 로그인 실패
				res.setContentType("text/html; charset=UTF-8");
				PrintWriter out = res.getWriter();

				out.println("<script>alert('아이디 또는 비밀번호가 일치하지 않습니다.'); history.go(-1);</script>");
				out.flush();
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		System.out.println("mav :" + mav);
		return mav;
	}
	
	// 관리자 로그인 (스프링 인터셉터 사용)
	@RequestMapping("/adminLoginProcessing")
	public ModelAndView adminLoginProcessing(@RequestParam Map<String, Object> paramMap, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		
		String password = paramMap.get("password").toString();
				
		// 관리자 로그인
		Map<String, Object> user = new HashMap<String, Object>();
		user = adminMapper.selectUserByAdmin(paramMap);
		
		// 암호화된 비밀번호 - 입력받은 비밀번호 pwd가 동일한 형식의 hashCode인지..	 id / password
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if(!encoder.matches(password, user.get("pwd").toString())) {
			// 비밀번호가 동일하지 않다면			
			user = new HashMap<String, Object>();	// 아이디 비밀번호 불일치 -> 초기화 
		}
		
		try {
			if (!MapUtils.isEmpty(user)) {
				// 로그인 성공
				session.setAttribute("login_no", user.get("user_no"));
				session.setAttribute("login_name", user.get("name"));
				session.setAttribute("login_eng_name", user.get("eng_name"));
				session.setAttribute("role_name", user.get("role_name"));
				session.setAttribute("role_no", user.get("role"));

				// 세션 시간 설정 (초단위) 1000시간
				session.setMaxInactiveInterval(3600000);

				//mav.setViewName("redirect:home");
				mav.setViewName("redirect:/adminLogin");

			} else {
				// 로그인 실패
				res.setContentType("text/html; charset=UTF-8");
				PrintWriter out = res.getWriter();

				out.println("<script>alert('아이디 또는 비밀번호가 일치하지 않습니다.'); history.go(-1);</script>");
				out.flush();
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return mav;
	}

	// 로그아웃 버튼 클릭 (스프링 인터셉터 사용)
	@RequestMapping("/logout")
	public ModelAndView logout(HttpSession session) {
		// session.removeAttribute("userId");

		ModelAndView mav = new ModelAndView();

		// role_name
		String role_name = session.getAttribute("role_name").toString();		// 세션에 저장해놓은 role(권한명)을 가져온다.	
		
		if(role_name.equals("ROLE_STUDENT")) {	// 학생 권한으로 로그인 상태였다면 학생 로그인 페이지로 이동됨 
			String site_code = session.getAttribute("site_code").toString();
			
			if(!"".equals(site_code)) {			
				mav.setViewName("redirect:/login?site_code="+site_code);
			} else {			
				mav.setViewName("redirect:/login");
			}
		} else {	// 관리자,강사 권한으로 로그인 상태였다면 관리자 로그인 페이지로 이동됨
			mav.setViewName("redirect:/adminLogin");
		}
		
		session.invalidate();

		return mav;
	}

	@RequestMapping("home")
	public ModelAndView home(HttpSession session) {
		ModelAndView mav = new ModelAndView();

		if (session.getAttribute("login_no") == null) {
			
			
			//mav.setViewName("redirect:/login");
			mav.setViewName("redirect:login");
			
			
		} else {
			String userRole = session.getAttribute("role_name").toString();

			// ROLE_ADMIN : 슈퍼관리자
			// ROLE_TEACHER : 강사관리자
			// ROLE_STUDENT : 학생			

			if (("ROLE_ADMIN").equals(userRole)) {	// 슈퍼관리자 최초 로그인 시 이동페이지: 회원정보 
				mav.setViewName("redirect:admin/user");
			} else if (("ROLE_TEACHER").equals(userRole)) {
				mav.setViewName("redirect:teacher/todaySchedule"); // 강사관리자 최초 로그인 시 이동페이지: TodaySchedule

			} else if (("ROLE_STUDENT").equals(userRole)) {	 // 학생 최초 로그인 시 이동페이지: 학습현황
				mav.setViewName("redirect:student/myClass"); 

			}
		}

		return mav;
	}
	


}
