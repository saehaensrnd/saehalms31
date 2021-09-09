package com.edutem.lms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.MapUtils;

import com.edutem.lms.interceptor.Auth;
import com.edutem.lms.interceptor.Auth.Role;
import com.edutem.lms.mapper.AdminMapper;
import com.edutem.lms.mapper.StudentMapper;
import com.edutem.lms.paging.CenterCriteria;
import com.edutem.lms.paging.CenterPageMaker;
import com.edutem.lms.paging.Criteria;
import com.edutem.lms.paging.PageMaker;

// 학생 컨트롤러
@Controller
public class SaehaLmsStudentController {
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private StudentMapper studentMapper;
	
	
	// 마이노트 학습현황
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/myClass")
	public ModelAndView student_myClass(@RequestParam Map<String, Object> paramMap, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/myClass");

		mav.addObject("main_title", "마이노트");
		mav.addObject("sub_title", "학습현황");
		
		// 로그인 user 정보
		String user_no = session.getAttribute("login_no").toString();
		Map<String, Object> user = adminMapper.getUserByLoginNo(user_no);
		
		paramMap.put("user_student_no", user.get("user_student_no"));
		
		// 수강중인 과정 리스트 (콤보박스용) // param: user_student_no	※ 콤보에 레벨테스트도 나와야하는지 미정이라 우선 둘다 불러올수있도록 flag / no 사용
		List<Map<String, Object>> classComboList = studentMapper.selectClassComboByStudent(paramMap);
		
		// 수강 과정이 있는지 없는지..
		if (classComboList.size() > 0) {
			mav.addObject("existFlag", "Y");
			mav.addObject("classComboList", classComboList);
			
			// 처음 보여줄 화면 - 과정 class_no: 가장 최근에 시작된 과정 start_dt 기준			
			String viewNo = classComboList.get(0).get("no").toString();
			
			mav.addObject("classComboList", classComboList);

			// 나의강의실 과정 셀렉트콤보 변경 시 - 클래스 classNo 파라미터
			if (paramMap.containsKey("selectedNo")) {
				paramMap.put("class_no", paramMap.get("selectedNo"));
			} else {
				paramMap.put("class_no", viewNo);
			}
			
		} else {
			mav.addObject("existFlag", "N");			
		}
		
		Map<String, Object> classInfo = studentMapper.getClassInfo(paramMap);
		mav.addObject("classInfo", classInfo);
		
		List<Map<String, Object>> classLog = studentMapper.selectClassLog(paramMap);
		mav.addObject("classLog", classLog);
		mav.addObject("classLogCnt", classLog.size());

		return mav;
	}
	
	// 마이노트 학습현황 - 교재보기
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/myClass/onlineBook")
	public ModelAndView student_onlineBook(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/student_onlineBook");
		
		mav.addObject("textbookInfo", adminMapper.selectTextbookOne(paramMap));					// param : textbook_no
		mav.addObject("textbookTopicList", adminMapper.selectTextbookTopicCombo(paramMap));		// param : textbook_no
		
		return mav;
	}
	
	
	
	
	
	// 마이노트 무료레벨테스트
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/levelTest")
	public ModelAndView student_levelTest(@RequestParam Map<String, Object> paramMap, HttpSession session, Criteria cri) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/levelTest");

		mav.addObject("main_title", "마이노트");
		mav.addObject("sub_title", "무료레벨테스트");
		
		adminMapper.setRownum();		
		
		// 로그인 user 정보
		String user_no = session.getAttribute("login_no").toString();
		Map<String, Object> user = adminMapper.getUserByLoginNo(user_no);
		
		paramMap.put("user_student_no", user.get("user_student_no"));
		paramMap.put("cri", cri);
		
		mav.addObject("user", user);	// 레벨테스트 신청 시 필요
		
		
		// 오늘 날짜 기준 무료레벨테스트 신청 Flag	
		// 1) 사이트별 수강신청의 사이트,언어,기수 별 레벨테스트 신청 시작~종료일을 우선적으로 보며 
		// 2) 사이트별 수강신청에 등록이 안되어있는 기수에 대해선 수업일정 레벨테스트 신청 시작~종료일들을 본다.		
		paramMap.put("site_no", studentMapper.selectStudentOne(user).get("site_no"));
		int ableCnt = studentMapper.countLevelAppAbleTerm(paramMap);
		String leveltestBtnFlag = "0";
		if(ableCnt > 0) leveltestBtnFlag = "1";
		mav.addObject("leveltestBtnFlag", leveltestBtnFlag);	// 오늘날짜가 레벨테스트 신청기간인지 - 신청일에 포함된다면 Flag "1"
		
		
		mav.addObject("levelTest", studentMapper.selectLevelTestAll(paramMap));
		int count = studentMapper.countLevelTestAll(paramMap);
		PageMaker pageMaker = new PageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("levelTestCnt", count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 마이노트 무료레벨테스트 - 결과보기 팝업
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/myClass/levelReport")
	public ModelAndView student_myClass_levelReport(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/levelTest_report");
		
		Map<String, Object> info = studentMapper.getLevelInfo(paramMap);				
		double avg = 0.0;
		
		// 점수 평균
		if(!"".equals(info.get("grammer").toString()) && !"".equals(info.get("vocabulary").toString())) {
			int grammer = Integer.parseInt(info.get("grammer").toString());
			int speaking_fluency = Integer.parseInt(info.get("speaking_fluency").toString());
			int listening_comprehension = Integer.parseInt(info.get("listening_comprehension").toString());
			int pronunciation_intonation = Integer.parseInt(info.get("pronunciation_intonation").toString());
			int vocabulary = Integer.parseInt(info.get("vocabulary").toString());
			
			int sum = grammer + speaking_fluency + listening_comprehension + pronunciation_intonation + vocabulary;			
			avg = (double)sum/5;
			
			info.put("avg", avg);
		} else {
			info.put("avg", "");
		}
		
		mav.addObject("info", info);
		
		return mav;
	}
	
	
	// 마이노트 휴강 및 보강 신청
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/postpone")
	public ModelAndView student_postpone(@RequestParam Map<String, Object> paramMap, HttpSession session, Criteria cri) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/student_postpone");

		mav.addObject("main_title", "마이노트");
		mav.addObject("sub_title", "휴강 및 보강 신청");
		
		adminMapper.setRownum();
		
		// 로그인 user 정보
		String user_no = session.getAttribute("login_no").toString();
		Map<String, Object> user = adminMapper.getUserByLoginNo(user_no);
		
		paramMap.put("user_student_no", user.get("user_student_no"));		
		paramMap.put("cri", cri);
		
		mav.addObject("postpone", studentMapper.selectPostponeByStudent(paramMap));
		int count = studentMapper.countPostponeByStudent(paramMap);
		PageMaker pageMaker = new PageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("postponeCnt", count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 마이노트 휴강및보강신청 등록/수정 페이지
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/postpone/detail")
	public ModelAndView student_postpone_detail(@RequestParam Map<String, Object> paramMap, HttpSession session) {
		ModelAndView mav = new ModelAndView();	
		mav.setViewName("page/student/student_postpone_detail");	
		mav.addObject("main_title", "마이노트");
		mav.addObject("sub_title", "휴강 및 보강 신청");
		
		// 로그인 user 정보
		String user_no = session.getAttribute("login_no").toString();
		Map<String, Object> user = adminMapper.getUserByLoginNo(user_no);		
		mav.addObject("user", user);
		
		paramMap.put("user_student_no", user.get("user_student_no"));
		
		// 등록 시 사용하는 휴강요청수업(수강중인) 셀렉트박스
		List<Map<String, Object>> classComboList = studentMapper.selectClassComboByStudent(paramMap);
		mav.addObject("classComboList", classComboList);
		
		
		// 상세보기 (수정)
		if (paramMap.containsKey("postpone_no")) {	
			// 수정
			Map<String, Object> postpone = studentMapper.getPostpone(paramMap);
			mav.addObject("postpone", postpone);
			mav.addObject("flag", "update");
			mav.addObject("sub_title2", "수정");
			
		} else {	
			// 등록
			mav.addObject("flag", "insert");
			mav.addObject("sub_title2", "등록");
			
		}
		return mav;
	}
	
	
	
	// 회원정보 - 내 정보 수정
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/infoModify")
	public ModelAndView student_infoModify(@RequestParam Map<String, Object> paramMap, HttpSession session) {
		ModelAndView mav = new ModelAndView();	
		mav.setViewName("page/student/student_infoModify");	
		mav.addObject("main_title", "회원정보");
		mav.addObject("sub_title", "내 정보 수정");
		
		// 로그인 user 정보
		String user_no = session.getAttribute("login_no").toString();
		//Map<String, Object> user = adminMapper.getUserByLoginNo(user_no);		
		paramMap.put("user_no", user_no);
		Map<String, Object> student = studentMapper.getStudentInfo(paramMap);
		
		if(student.containsKey("student_address")) {
			String[] address = student.get("student_address").toString().split("/");
			if(address.length > 0) {
				mav.addObject("zip_code", address[0]); 
				mav.addObject("address", address[1]); 
				mav.addObject("address_detail", address[2]);
			}
			else {
				mav.addObject("zip_code", "");
				mav.addObject("address", ""); 
				mav.addObject("address_detail", "");
			}
		}
		else { 
			mav.addObject("zip_code", "");
			mav.addObject("address", ""); 
			mav.addObject("address_detail", "");
		}
		
		mav.addObject("student", student);		
		
		
		return mav;
	}
	
	// 결제
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/enrolment/success")
	public ModelAndView paySuccess(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/enrolment_success");
		
		System.out.println("##############################");
		System.out.println("success");
		System.out.println(paramMap);
		
		if(paramMap.get("price").toString().equals(paramMap.get("amount").toString())) {
			mav.addObject("result", "success");
			
			paramMap.put("payment_student", adminMapper.selectStudentOne(paramMap).get("student_name"));
			paramMap.put("payment_teacher", studentMapper.selectTeacherOne(paramMap).get("teacher_name"));
			paramMap.put("payment_product", adminMapper.selectProductOne(paramMap).get("product_name"));
			paramMap.put("payment_study", adminMapper.selectStudyOne(paramMap).get("study_name"));
			paramMap.put("payment_textbook", adminMapper.selectTextbookOne(paramMap).get("textbook_name"));
			
			if(paramMap.get("bookCheck").toString().equals("true")) {
				paramMap.put("payment_book_status", 0); // 교재 비구매
			}
			else if(paramMap.get("bookCheck").toString().equals("false")) {
				paramMap.put("payment_book_status", 1); // 교재 구매
			}
			
			String payment_address = "";
			if (paramMap.containsKey("zip_code")) {
				payment_address = paramMap.get("zip_code") + "/" + paramMap.get("address") + "/" + paramMap.get("address_detail");
			}
			paramMap.put("payment_address", payment_address);
			
			studentMapper.insertPayment(paramMap);
		}
		else {
			mav.addObject("result", "fail");
			mav.addObject("message", paramMap.get("message"));
		}
		
		mav.addObject("amount", paramMap.get("amount"));
		mav.addObject("orderId", paramMap.get("orderId"));
		mav.addObject("paymentKey", paramMap.get("paymentKey"));
		
		return mav;
	}
	
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/enrolment/fail")
	public ModelAndView payFail(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/enrolment_success");
		
		System.out.println("##############################");
		System.out.println("fail");
		System.out.println(paramMap);
		
		mav.addObject("result", "fail");
		mav.addObject("message", paramMap.get("message"));
		
		mav.addObject("amount", paramMap.get("amount"));
		mav.addObject("orderId", paramMap.get("orderId"));
		mav.addObject("paymentKey", paramMap.get("paymentKey"));
		
		return mav;
	}
	
	
	
	// ############## 공용 ##################
	
	// 마이노트 학습현황 - 녹화파일 다시보기 목록 조회	(현재 사용안함 파일 다시보기 관련 SaehaLmsTeacherRestController : /teacher/todaySchedule/videoFile  사용중)
	@RequestMapping("/student/myClass/fileList")
	public ModelAndView admin_teacher_timetable(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/myClass_fileList");
				
		String study_date = paramMap.get("study_date").toString();		
		
		String urlSplitString = paramMap.get("urlSplitString").toString();
		String recordIdSplitString = paramMap.get("recordIdSplitString").toString();
		
		List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
		Map<String, Object> recordMap = new HashMap<String, Object>();
		
		if(!"".equals(urlSplitString)) {			
			String[] urlSplit = urlSplitString.split("@@");
			String[] recordIdSplit = recordIdSplitString.split("@@");
			
			for(int i=0; i<urlSplit.length; i++) {
				recordMap = new HashMap<String, Object>();
				recordMap.put("url", urlSplit[i]);
				recordMap.put("recordID", recordIdSplit[i]);
				
				recordList.add(recordMap);
			}
		}
		
		mav.addObject("study_date", study_date);
		mav.addObject("class_score_no", paramMap.get("class_score_no"));
		mav.addObject("recordList", recordList);
		
		return mav;
	}
	
	// 마이노트 학습현황 - 출석 클릭 시 report 팝업
	@RequestMapping("/student/myClass/report")
	public ModelAndView student_myClass_report(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/myClass_report");
		
		Map<String, Object> info = studentMapper.getClassLogInfo(paramMap);
		
				
		double avg = 0.0;
		
		// 점수 평균
		if(!"".equals(info.get("grammer").toString()) && !"".equals(info.get("vocabulary").toString())) {
			int grammer = Integer.parseInt(info.get("grammer").toString());
			int speaking_fluency = Integer.parseInt(info.get("speaking_fluency").toString());
			int listening_comprehension = Integer.parseInt(info.get("listening_comprehension").toString());
			int pronunciation_intonation = Integer.parseInt(info.get("pronunciation_intonation").toString());
			int vocabulary = Integer.parseInt(info.get("vocabulary").toString());
			
			int sum = grammer + speaking_fluency + listening_comprehension + pronunciation_intonation + vocabulary;			
			avg = (double)sum/5;
			
			info.put("avg", avg);
		} else {
			info.put("avg", "");
		}
		
		mav.addObject("info", info);
		
		return mav;
	}
	
	// 회원가입하기	
	@RequestMapping("/student/join")
	public ModelAndView join(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/join");
		
		// site_no
		if(paramMap.containsKey("site_code")) {
			mav.addObject("site_code", paramMap.get("site_code"));
			mav.addObject("site", studentMapper.selectSiteOneByCode(paramMap));
		}
		
		return mav;
	}
	
	// 수강신청하기
	@RequestMapping("/student/enrolment")
	public ModelAndView enrolment(@RequestParam Map<String, Object> paramMap, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/enrolment");

		// 로그인 user 정보
		paramMap.put("user_no", session.getAttribute("login_no"));
		Map<String, Object> user_student = studentMapper.selectStudentOne(paramMap);
		
		Map<String, Object> site = studentMapper.selectSiteOne(user_student);
		String[] site_language = site.get("site_language").toString().split(",");

		if(user_student.containsKey("student_phone")) {
			mav.addObject("student_phone", user_student.get("student_phone"));
		}
		else {
			mav.addObject("student_phone", "");
		}
		
		if(user_student.containsKey("student_address")) {
			String[] address = user_student.get("student_address").toString().split("/");
			if(address.length > 0) {
				mav.addObject("zip_code", address[0]); 
				mav.addObject("address", address[1]); 
				mav.addObject("address_detail", address[2]);
			}
			else {
				mav.addObject("zip_code", "");
				mav.addObject("address", ""); 
				mav.addObject("address_detail", "");
			}
		}
		else { 
			mav.addObject("zip_code", "");
			mav.addObject("address", ""); 
			mav.addObject("address_detail", "");
		}
		
		mav.addObject("user_student_no", user_student.get("user_student_no"));
		mav.addObject("student_name", user_student.get("student_name"));
		mav.addObject("site_no", user_student.get("site_no"));
		mav.addObject("site_language", site_language);
		mav.addObject("product", studentMapper.selectSiteProduct(user_student));
		
		return mav;
	}
	
	// 학생 무료레벨테스트 신청 팝업
	@Auth(role= Role.ROLE_STUDENT)
	@RequestMapping("/student/levelTest/leveltestApp")
	public ModelAndView makeClass(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/student/popup_leveltestApp");
		Map<String, Object> student = adminMapper.selectStudentOne(paramMap);
		mav.addObject("user_student", student);
						
		Map<String, Object> studentSite = adminMapper.selectSiteOne(student);
		// 해당 학생 사이트 사용언어 Type
		String[] splitLanguage = studentSite.get("site_language").toString().split(",");
		mav.addObject("languageList", splitLanguage);
		
		paramMap.put("site_no", student.get("site_no"));
		mav.addObject("product", adminMapper.selectProductMakeClass(paramMap));
		mav.addObject("paramMap", paramMap);
		
		return mav;
	}
	
}
