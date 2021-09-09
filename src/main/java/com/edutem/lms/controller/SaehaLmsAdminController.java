package com.edutem.lms.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.MapUtils;

import com.edutem.lms.interceptor.Auth;
import com.edutem.lms.interceptor.Auth.Role;
import com.edutem.lms.mapper.AdminMapper;
import com.edutem.lms.paging.AssignCriteria;
import com.edutem.lms.paging.CenterCriteria;
import com.edutem.lms.paging.CenterPageMaker;
import com.edutem.lms.paging.ClassDetailCriteria;
import com.edutem.lms.paging.ClassDetailPageMaker;
import com.edutem.lms.paging.ClassInfoCriteria;
import com.edutem.lms.paging.ClassInfoPageMaker;
import com.edutem.lms.paging.ClassStudentCriteria;
import com.edutem.lms.paging.ClassStudentPageMaker;
import com.edutem.lms.paging.Criteria;
import com.edutem.lms.paging.HistoryCriteria;
import com.edutem.lms.paging.HistoryPageMaker;
import com.edutem.lms.paging.ModifyClassDateCriteria;
import com.edutem.lms.paging.ModifyClassDatePageMaker;
import com.edutem.lms.paging.PageMaker;
import com.edutem.lms.paging.ScheduleCriteria;
import com.edutem.lms.paging.StartTimeCriteria;
import com.edutem.lms.paging.StartTimePageMaker;
import com.edutem.lms.paging.TeacherCriteria;
import com.edutem.lms.paging.TeacherPageMaker;
import com.edutem.lms.paging.TimeClassCriteria;
import com.edutem.lms.paging.TimeClassPageMaker;
import com.edutem.lms.paging.TimetableCriteria;
import com.edutem.lms.paging.TodayScheduleCriteria;
import com.edutem.lms.paging.TodaySchedulePageMaker;
import com.edutem.lms.paging.UserCriteria;
import com.edutem.lms.paging.UserPageMaker;

@Controller
public class SaehaLmsAdminController {
	
	@Autowired
	private AdminMapper adminMapper;
	
	// 국적 콤보박스
	String[] nationList = { "대한민국", "중국", "일본", "필리핀", "몽고", "대만", "베트남", "라오스", "미얀마", "캄보디아", "태국", "싱가포르", "말레이시아", "인도네시아"
			, "스리랑카", "인도", "파키스탄", "네팔", "티벳", "이란", "사우디아라비아", "쿠웨이트", "이스라엘", "요르단", "아프가니스탄", "카자흐스탄", "우즈베키스탄", "타지키스탄"
			, "키르키스스탄", "투르크메니스탄", "아제르바이젠", "북미", "미국", "캐나다", "멕시코", "파나마", "브라질", "아르헨티나", "칠레", "콜롬비아", "푸에르토리코", "우루과이"
			, "페루", "코스타리카", "베네수엘라", "과테말라", "수리남", "쿠바", "가이아나", "에콰도르", "도미니크", "엘살바도르", "자메이카", "벨리즈", "니카라구아", "트리니다드토바고"
			, "영국", "프랑스", "독일", "스위스", "이탈리아", "그리스", "터키", "네덜란드", "덴마크", "스웨덴", "노르웨이", "핀란드", "벨기에", "룩셈부르크", "오스트리아", "아일랜드"
			, "아이슬란드", "헝가리", "러시아", "체코", "슬로바키아", "폴란드", "유고", "루마니아", "불가리아", "오스트레일리아", "뉴질랜드", "사모아", "이집트", "리비아", "수단"
			, "남아공화국", "자이레", "가봉", "피지", "콩고", "우간다"
	};	
	// 런닝타임
	String[] runningTimeList = { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "70", "80", "90"};

	@Auth(role= Role.ROLE_ADMIN)	// 슈퍼관리자 권한인 user만 해당 메서드에 접근 가능
	@RequestMapping("/admin/user") // 회원 정보 메뉴

	public ModelAndView adminUser(@ModelAttribute UserCriteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_user");
		
		mav.addObject("main_title", "회원정보");		
		paramMap.put("cri", cri);
		
		// 검색조건 - 콤보 리스트
		mav.addObject("siteList", adminMapper.getSiteCombo());
		
		mav.addObject("student", adminMapper.selectStudentAll(paramMap));
		int student_count = adminMapper.countStudentAll(paramMap);
		UserPageMaker pageMaker = new UserPageMaker(cri, student_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(student_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("studentCnt", student_count);
		mav.addObject("cri", cri);
		
		return mav;
	}

	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/user/detail")
	public ModelAndView adminUserDetail(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_user_detail");

		mav.addObject("main_title", "회원정보");
		mav.addObject("cri", cri);
		
		
		mav.addObject("site", adminMapper.selectSiteList());
		if(paramMap.containsKey("user_student_no")) {
			// 수정
			Map<String, Object> student = adminMapper.selectStudentOne(paramMap);
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
			mav.addObject("sub_title", "회원정보 수정");
			mav.addObject("flag", "update");
		}
		else {
			// 등록
			mav.addObject("sub_title", "회원 등록");
			mav.addObject("flag", "insert");
		}
		
		return mav;
	}
	
	// 슈퍼관리자 - 사이트관리 강사정보조회
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/teacher")
	public ModelAndView admin_teacher(HttpSession session, TeacherCriteria cri, @RequestParam Map<String, Object> paramMap) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_teacher");
		mav.addObject("main_title", "강사정보");
		
		adminMapper.setRownum();  
				
		// 검색 콤보 박스		
		mav.addObject("centerList", adminMapper.selectCenterCombo());	// 티칭센터 콤보박스
		mav.addObject("nationList", nationList);						// 국가 콤보박스
		mav.addObject("teacherList", adminMapper.selectTeacherCombo());	// 선생님 콤보박스

		paramMap.put("cri", cri);
		mav.addObject("teacher", adminMapper.selectTeacherAll(paramMap));
		int count = adminMapper.countTeacherAll(paramMap);
		TeacherPageMaker pageMaker = new TeacherPageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("teacherCnt", count);
		mav.addObject("cri", cri);

		return mav;
	} 
	
	// 슈퍼관리자 - 사이트관리 강사정보조회 상세보기(등록/수정)
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/teacher/detail")
	public ModelAndView admin_teacher_detail(@RequestParam Map<String, Object> paramMap, AssignCriteria cri,
			HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_teacher_detail");
		
		mav.addObject("main_title", "강사정보");		

		// 페이지 정보
		mav.addObject("cri", cri);
		
		// 시간 콤보박스
		String[] hourList = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
				"15", "16", "17", "18", "19", "20", "21", "22", "23" };
		mav.addObject("hourList", hourList);

		String[] minList = { "00", "10", "20", "30", "40", "50" };
		mav.addObject("minList", minList);
		
		// 티칭센터 콤보박스
		mav.addObject("centerList", adminMapper.selectCenterCombo());
		// 국가 콤보박스
		mav.addObject("nationList", nationList);
		
		

		// 상세보기 (수정)
		if (paramMap.containsKey("user_teacher_no")) {	
			// 수정
			Map<String, Object> teacher = adminMapper.selectTeacherOne(paramMap);	// 해당 강사 정보
			mav.addObject("teacher", teacher);
			mav.addObject("flag", "update");
			mav.addObject("sub_title", "수정");
			
			
			// 등록되어있는 근무시간표 가져오기 timetable
			List<String> timeTable = new ArrayList<String>();
			timeTable = adminMapper.getTimeTable(paramMap);		// 해당 강사 근무가능 시간표 리스트

			// 근무시간 (강사 배정시간)
			String start_time = teacher.get("start_time").toString();
			String end_time = teacher.get("end_time").toString();

			int start_hour = Integer.parseInt(teacher.get("start_hour").toString());
			int start_min = Integer.parseInt(teacher.get("start_min").toString());
			int end_hour = Integer.parseInt(teacher.get("end_hour").toString());
			int end_min = Integer.parseInt(teacher.get("end_min").toString());

			// 근무시간 시간 List			
			List<Map<String, Object>> timeTableList = new ArrayList<Map<String, Object>>();
			Map<String, Object> timeMap = new HashMap<String, Object>();
			String time, timeStamp;
			int checkCnt = 0;		// 배정 가능시간대와 근무 체크 시간이 같은지 count

			// 강사 상세보기 수업시간 테이블 생성 관련 if ~ else
			if (("0000".equals(start_time) && "0000".equals(end_time)) && start_time.equals(end_time)) { // 배정시간 수정 전																											
				timeMap.put("time", "00:00");	// (초기상태)
				
			} else { // 배정시간 지정 후 (강사 등록 후 해당 강사 배정시간까지 입력했다면)

				// timeTableList 초기값 add		1) 최초 시간대(배정 시작시간)로 체크
				time = String.format("%02d", start_hour) + String.format("%02d", start_min);
				timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);
				timeMap.put("time", timeStamp);
				for (int i = 0; i <= 6; i++) {					// 일요일 ~ 토요일 반복
					timeMap.put("d" + i, i + "_" + time);		// ex) 일요일 13:00 -> 0_1300

					checkCnt = 0; 
					for (String ableTime : timeTable) {						
						if (ableTime.equals(i + "_" + time)) {
							checkCnt++;	 // 배정 가능시간과 근무시간과 동일하면 cnt++
						}
					}
					timeMap.put("isCheck" + i, checkCnt);		// 배정 가능시간과 근무 가능시간이 동일하다면, 해당 요일 시간대 color 표시 여부에 사용

				}
				// timeMap.put("d1", "1_"+time);
				timeTableList.add(timeMap);

				int i = 0; // 초기값 설정
				while (i < 1) { // 조건식			2) 최초 시간대(배정 시작시간)에서 5분씩 더해가며 시간대 체크
					timeMap = new HashMap<String, Object>();

					//start_min += 10;
					start_min += 5;
					
					if (start_min == 60) {
						start_min = 0; // 분 0
						start_hour += 1; // 시간 +1시간
					}

					time = String.format("%02d", start_hour) + String.format("%02d", start_min);
					timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);

					timeMap.put("time", timeStamp);	// 수업시간에 표기될 시간 13:00, 13:05, ...
					for (int k = 0; k <= 6; k++) {
						timeMap.put("d" + k, k + "_" + time);

						checkCnt = 0; // 배정 가능시간과 근무시간과 동일하면 cnt++
						for (String ableTime : timeTable) {							
							if (ableTime.equals(k + "_" + time)) {
								checkCnt++;
							}
						}
						timeMap.put("isCheck" + k, checkCnt);
					}

					timeTableList.add(timeMap);

					if (start_hour == end_hour && start_min == end_min) { // 배정 시작시간과 종료시간이 같아졌다면 while문 빠져나감
						i++; // 증감식
					}

				} // timeTableList.add END

			}			
			mav.addObject("timeTableList", timeTableList);			
			
		} else {	
			// 등록
			mav.addObject("flag", "insert");
			mav.addObject("sub_title", "등록");
		}
		return mav;
	}
	
	// 사이트관리 강사정보조회 시간표 보기 (팝업)
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/teacher/timetable")
	public ModelAndView admin_teacher_timetable(@RequestParam Map<String, Object> paramMap, TimetableCriteria cri) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_teacher_timetable");
		
		// 페이지 정보
		mav.addObject("cri", cri);
		
		// 티칭센터 콤보박스
		mav.addObject("centerList", adminMapper.selectCenterCombo());
		mav.addObject("teacherList", adminMapper.selectTeacherComboByCenterNo(paramMap));
		
		paramMap.put("user_teacher_no", paramMap.get("searchTeacher"));
		
		// 강사정보
		Map<String, Object> teacher = adminMapper.selectTeacherOne(paramMap);
		mav.addObject("teacher", teacher);
		
		// 강사 휴일리스트 가져오기
		mav.addObject("holiday", adminMapper.getTeacherHoliday(paramMap));
		
		
		// 등록되어있는 근무시간표 가져오기 timetable
		List<String> timeTable = new ArrayList<String>();
		timeTable = adminMapper.getTimeTable(paramMap);

		// 근무시간
		String start_time = teacher.get("start_time").toString();
		String end_time = teacher.get("end_time").toString();

		int start_hour = Integer.parseInt(teacher.get("start_hour").toString());
		int start_min = Integer.parseInt(teacher.get("start_min").toString());
		int end_hour = Integer.parseInt(teacher.get("end_hour").toString());
		int end_min = Integer.parseInt(teacher.get("end_min").toString());

		// 근무시간 시간 List			
		List<Map<String, Object>> timeTableList = new ArrayList<Map<String, Object>>();
		Map<String, Object> timeMap = new HashMap<String, Object>();
		String time, timeStamp;
		int checkCnt = 0;

		
		// 시간표 테이블 Setting - 강사정보 상세보기(/admin/teacher/detail) 시간 테이블 생성 방식과 기능 동일
		if (("0000".equals(start_time) && "0000".equals(end_time)) && start_time.equals(end_time)) { // 배정시간 수정 전																											
			timeMap.put("time", "00:00");	// (초기상태)
			
		} else { // 배정시간 지정 후

			// timeTableList 초기값 add
			time = String.format("%02d", start_hour) + String.format("%02d", start_min);
			timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);
			timeMap.put("time", timeStamp);
			for (int i = 0; i <= 6; i++) {
				timeMap.put("d" + i, i + "_" + time);

				checkCnt = 0; // 배정 가능시간과 근무시간과 동일하면 cnt++
				for (String ableTime : timeTable) {
					// System.out.println(ableTime);
					if (ableTime.equals(i + "_" + time)) {
						checkCnt++;
					}
				}
				timeMap.put("isCheck" + i, checkCnt);

			}
			// timeMap.put("d1", "1_"+time);
			timeTableList.add(timeMap);

			int i = 0; // 초기값 설정
			while (i < 1) { // 조건식
				timeMap = new HashMap<String, Object>();

				//start_min += 10;
				start_min += 5;
				
				if (start_min == 60) {
					start_min = 0; // 분 0
					start_hour += 1; // 시간 +1시간
				}

				time = String.format("%02d", start_hour) + String.format("%02d", start_min);
				timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);

				timeMap.put("time", timeStamp);
				for (int k = 0; k <= 6; k++) {
					timeMap.put("d" + k, k + "_" + time);

					checkCnt = 0; // 배정 가능시간과 근무시간과 동일하면 cnt++
					for (String ableTime : timeTable) {
						// System.out.println(ableTime);
						if (ableTime.equals(k + "_" + time)) {
							checkCnt++;
						}
					}
					timeMap.put("isCheck" + k, checkCnt);
				}

				timeTableList.add(timeMap);

				if (start_hour == end_hour && start_min == end_min) { // 배정 시작시간과 종료시간이 같아졌다면 while문 빠져나감
					i++; // 증감식
				}

			} // timeTableList.add END

		}			
		mav.addObject("timeTableList", timeTableList);	
		
		
		
		
		
		return mav;
	}
	
	// 티칭센터 정보조회
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/center")
	public ModelAndView admin_center(HttpSession session, CenterCriteria cri, @RequestParam Map<String, Object> paramMap) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_center");

		mav.addObject("main_title", "사이트관리");	
		mav.addObject("sub_title", "티칭센터 정보조회");
		
		adminMapper.setRownum(); 

		paramMap.put("cri", cri);
		mav.addObject("center", adminMapper.selectCenterAll(paramMap));
		int count = adminMapper.countCenterAll(paramMap);
		CenterPageMaker pageMaker = new CenterPageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("centerCnt", count);
		mav.addObject("cri", cri);

		return mav;
	}
	
	// 티칭센터 정보조회 상세보기 등록/수정 페이지
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/center/detail")
	public ModelAndView adminCenterDetail(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_center_detail");

		mav.addObject("main_title", "사이트관리");
		mav.addObject("sub_title", "티칭센터 정보조회");
		mav.addObject("cri", cri);
		
		// 시간 콤보박스
		String[] hourList = { "06", "07", "08", "09", "10", "11", "12", "13", "14",	"15", "16", "17", "18", "19", "20", "21", "22", "23" };
		mav.addObject("hourList", hourList);

		String[] minList = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09"
				           , "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"
				           , "20", "21", "22", "23", "24", "25", "26", "27", "28", "29"
				           , "30", "31", "32", "33", "34", "35", "36", "37", "38", "39"
				           , "40", "41", "42", "43", "44", "45", "46", "47", "48", "49"
				           , "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" };				           
		mav.addObject("minList", minList);

		if(paramMap.containsKey("center_no")) {
			// 수정
			mav.addObject("item", adminMapper.selectCenterOne(paramMap));
			mav.addObject("sub_title2", "수정");
			mav.addObject("flag", "update");
		}
		else {
			// 등록
			mav.addObject("sub_title2", "등록");
			mav.addObject("flag", "insert");
		}
		
		return mav;
	}

	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/studyMng") // 수강과정 메뉴
	public ModelAndView adminStudy(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_study");
		
		mav.addObject("main_title", "수강과정");
		
		paramMap.put("cri", cri);
		mav.addObject("study", adminMapper.selectStudyAll(paramMap));
		int study_count = adminMapper.countStudyAll();
		PageMaker pageMaker = new PageMaker(cri, study_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(study_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("studyCnt", study_count);
		mav.addObject("cri", cri);
		
		return mav;
	}

	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/studyMng/detail") // 수강과정 상세보기
	public ModelAndView adminStudyDetail(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_study_detail");

		mav.addObject("main_title", "수강과정");
		mav.addObject("cri", cri);
		
		
		mav.addObject("site", adminMapper.selectSiteList());
		if(paramMap.containsKey("study_no")) {
			// 수정
			Map<String, Object> study = adminMapper.selectStudyOne(paramMap);
			List<Map<String, Object>> study_not_used_site = adminMapper.selectStudyNotUsedSite(paramMap);
			mav.addObject("study", study);
			mav.addObject("study_not_used_site", study_not_used_site);
			mav.addObject("sub_title", "수강과정 수정");
			mav.addObject("flag", "update");
		}
		else {
			// 등록
			mav.addObject("sub_title", "수강과정 등록");
			mav.addObject("flag", "insert");
		}
		
		return mav;
	}
	
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/studyLevel") // 수강과정 레벨 메뉴
	public ModelAndView adminStudyLevel(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_study_level");

		if(!paramMap.containsKey("study_language")) {
			paramMap.put("study_language", "English");
		}
		if(!paramMap.containsKey("study_status")) {
			paramMap.put("study_status", 1);
		}
		List<Map<String, Object>> study = adminMapper.selectStudyLang(paramMap);
		mav.addObject("study", study);
		
		if(!paramMap.containsKey("study_no")) {
			if(study.size() > 0) {
				paramMap.put("study_no", study.get(0).get("study_no"));
			}
		}
		
		mav.addObject("language", paramMap.get("study_language"));
		mav.addObject("status", paramMap.get("study_status"));
		mav.addObject("study_no", paramMap.get("study_no"));
		
		mav.addObject("study_level", adminMapper.selectStudyLevelAll(paramMap));
		
		return mav;
	}
	
	// 사이트정보조회
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/site")
	public ModelAndView admin_site(HttpSession session, CenterCriteria cri, @RequestParam Map<String, Object> paramMap) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_site");

		mav.addObject("main_title", "사이트관리");	
		mav.addObject("sub_title", "사이트 정보조회");
		
		adminMapper.setRownum();

		paramMap.put("cri", cri);
		mav.addObject("site", adminMapper.selectSiteAll(paramMap));
		int count = adminMapper.countSiteAll(paramMap);
		CenterPageMaker pageMaker = new CenterPageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("siteCnt", count);
		mav.addObject("cri", cri);

		return mav;
	}
	
	// 사이트정보조회 상세보기 등록/수정 페이지
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/site/detail")
	public ModelAndView adminSiteDetail(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_site_detail");

		mav.addObject("main_title", "사이트관리");
		mav.addObject("sub_title", "사이트정보조회");
		mav.addObject("cri", cri);
		
		// 시간 콤보박스
		String[] hourList = { "06", "07", "08", "09", "10", "11", "12", "13", "14",	"15", "16", "17", "18", "19", "20", "21", "22", "23" };
		mav.addObject("hourList", hourList);

		String[] minList = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09"
				           , "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"
				           , "20", "21", "22", "23", "24", "25", "26", "27", "28", "29"
				           , "30", "31", "32", "33", "34", "35", "36", "37", "38", "39"
				           , "40", "41", "42", "43", "44", "45", "46", "47", "48", "49"
				           , "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" };				           
		mav.addObject("minList", minList);
		
		// 레벨테스트 신청제한 횟수 콤보박스
		String[] limitList = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
		mav.addObject("limitList", limitList);
		
		// 은행명 콤보박스		
		String[] bankList = { "한국산업은행", "기업은행", "국민은행", "외환은행", "수협", "장기은행", "농협(중앙)", "농협(조합)", "축협", "우리은행", "조흥은행", "제일은행", "서울은행", "신한은행"
				           , "시티은행", "동화은행", "동남은행", "대동은행", "대구은행", "부산은행", "충청은행", "광주은행", "제주은행", "경기은행", "전북은행", "강원은행", "경남은행"
				           , "충북은행", "새마을금고", "신용협동조합", "우체국", "하나은행", "보람은행", "평화은행", "금융결제원"};		
		mav.addObject("bankList", bankList);
		
		// 택배사 콤보박스		
		String[] parcelList = { "CJ택배", "한진택배", "이노지스", "우체국택배", "옐로우캡", "로젠택배" };		
		mav.addObject("parcelList", parcelList);
		
		// 티칭센터 콤보박스
		mav.addObject("centerList", adminMapper.selectCenterCombo());
		// 강사그룹 콤보박스
		mav.addObject("teacherGroupList", adminMapper.selectTeacherGroupCombo());
		
		
		
		if(paramMap.containsKey("site_no")) {
			// 수정
			Map<String, Object> site = adminMapper.selectSiteOne(paramMap);
			
			// site_face_type	화상 레벨테스트 신청
			String site_face_type_en = "0";
			String site_face_type_cn = "0";
			
			if(site.containsKey("site_face_type")) {
				String site_face_type = site.get("site_face_type").toString();
				
				// DB에 저장되는 형식이 English,Chinese 이런식이기 때문에, 각각 체크박스에 체크 여부
				if(site_face_type.contains("English")) {
					site_face_type_en = "1";
				}
				if(site_face_type.contains("Chinese")) {
					site_face_type_cn = "1";
				}
			}
			
			// site_call_type	전화 레벨테스트 신청
			String site_call_type_en = "0";
			String site_call_type_cn = "0";
			
			if(site.containsKey("site_call_type")) {
				String site_call_type = site.get("site_call_type").toString();
				
				if(site_call_type.contains("English")) {
					site_call_type_en = "1";
				}
				if(site_call_type.contains("Chinese")) {
					site_call_type_cn = "1";
				}
			}
			
			site.put("site_face_type_en", site_face_type_en);
			site.put("site_face_type_cn", site_face_type_cn);
			site.put("site_call_type_en", site_call_type_en);
			site.put("site_call_type_cn", site_call_type_cn);
			
			
			mav.addObject("item", site);
			mav.addObject("sub_title2", "수정");
			mav.addObject("flag", "update");
		}
		else {
			// 등록
			mav.addObject("sub_title2", "등록");
			mav.addObject("flag", "insert");
		}
		
		return mav;
	}

	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/textbookMng") // 교재리스트 메뉴
	public ModelAndView textbook(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_textbook");
		
		mav.addObject("main_title", "교재리스트");

		if(!paramMap.containsKey("study_language")) {
			paramMap.put("study_language", "English");
		}
		if(!paramMap.containsKey("textbook_status")) {
			paramMap.put("textbook_status", 1);
		}
		
		paramMap.put("study_status", 1);
		List<Map<String, Object>> study = adminMapper.selectStudyLang(paramMap);
		mav.addObject("study", study);
		mav.addObject("language", paramMap.get("study_language"));
		mav.addObject("textbook_status", paramMap.get("textbook_status"));

		if(!paramMap.containsKey("study_no")) {
			paramMap.put("study_no", study.get(0).get("study_no"));
		}
		mav.addObject("study_no", paramMap.get("study_no"));
		
		paramMap.put("cri", cri);
		mav.addObject("textbook", adminMapper.selectTextbookAll(paramMap));
		int textbook_count = adminMapper.countTextbookAll(paramMap);
		PageMaker pageMaker = new PageMaker(cri, textbook_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(textbook_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("textbookCnt", textbook_count);
		mav.addObject("cri", cri);
		
		return mav;
	}

	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/textbookMng/detail") // 교재리스트 상세보기
	public ModelAndView adminTextbookDetail(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_textbook_detail");

		mav.addObject("main_title", "교재리스트");
		mav.addObject("cri", cri);
		
		
		if(paramMap.containsKey("textbook_no")) {
			// 수정
			List<Map<String, Object>> study = adminMapper.selectStudyHaveLevel();
			mav.addObject("study", study);
			paramMap.put("study_no", study.get(0).get("study_no"));
			mav.addObject("study_level", adminMapper.selectStudyLevelAll(paramMap));
			
			Map<String, Object> textbook = adminMapper.selectTextbookOne(paramMap);
			mav.addObject("textbook", textbook);
			
			mav.addObject("sub_title", "교재리스트 수정");
			mav.addObject("flag", "update");
			
			mav.addObject("language", study.get(0).get("study_language"));
		}
		else {
			// 등록
			
			List<Map<String, Object>> study = adminMapper.selectStudyHaveLevel();
			mav.addObject("study", study);
			paramMap.put("study_no", study.get(0).get("study_no"));
			mav.addObject("study_level", adminMapper.selectStudyLevelAll(paramMap));
			
			mav.addObject("sub_title", "교재리스트 등록");
			mav.addObject("flag", "insert");
			
			mav.addObject("language", study.get(0).get("study_language"));
		}
		
		return mav;
	}
	
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/textbookTopic") // 교재 토픽 메뉴
	public ModelAndView textbookTopic(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_textbook_topic");

		if(!paramMap.containsKey("study_language")) {
			paramMap.put("study_language", "English");
		}
		paramMap.put("study_status", 1);

		List<Map<String, Object>> study = adminMapper.selectStudyLang(paramMap);
		mav.addObject("study", study);
		if(!paramMap.containsKey("study_no")) {
			if(study.size() > 0) {
				paramMap.put("study_no", study.get(0).get("study_no"));
			}
		}
		
		List<Map<String, Object>> textbook = adminMapper.selectTextbookByStudy(paramMap);
		mav.addObject("textbook", textbook);
		if(!paramMap.containsKey("textbook_no")) {
			if(textbook.size() > 0) {
				paramMap.put("textbook_no", textbook.get(0).get("textbook_no"));
			}
		}
		
		mav.addObject("language", paramMap.get("study_language"));
		mav.addObject("study_no", paramMap.get("study_no"));
		mav.addObject("textbook_no", paramMap.get("textbook_no"));
		
		List<Map<String, Object>> textbookTopic = adminMapper.selectTextbookTopic(paramMap);
		for(Map<String, Object> tt : textbookTopic) {
			tt.put("page", adminMapper.countTextbookPage(tt));
		}
		mav.addObject("textbookTopic", textbookTopic);
		
		return mav;
	}
	
	// 수업관리 수업시작시간관리
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/startTime")
	public ModelAndView admin_startTime(HttpSession session, StartTimeCriteria cri, @RequestParam Map<String, Object> paramMap) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_startTime");

		mav.addObject("main_title", "수업관리");	
		mav.addObject("sub_title", "수업시작시간 관리");
		
		adminMapper.setRownum();
				
		mav.addObject("runningTimeList", runningTimeList);		

		paramMap.put("cri", cri);
		mav.addObject("startTime", adminMapper.selectStartTimeAll(paramMap));
		int count = adminMapper.countStartTimeAll(paramMap);
		StartTimePageMaker pageMaker = new StartTimePageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("startTimeCnt", count);
		mav.addObject("cri", cri);

		return mav;
	}
	
	// 수업관리 수업시작관리 상세보기 (등록/수정)
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/startTime/detail")
	public ModelAndView admin_startTime_detail(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_startTime_detail");
		
		mav.addObject("runningTimeList", runningTimeList);		
		
		// 수업시작시간
		int start_hour = 6;
		int start_min = 0;
		int end_hour = 23;
		int end_min = 0;		
		
		// 시작시간 List			
		List<Map<String, Object>> startTimeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> timeMap = new HashMap<String, Object>();
		String time, timeStamp;
		int checkCnt = 0;
		

		// 상세보기 (수정)
		if (paramMap.containsKey("study_start_time_no")) {	
			// 수정
			mav.addObject("flag", "update");
			
			Map<String, Object> studyStartTime = adminMapper.getStudyStartTime(paramMap);
			mav.addObject("studyStartTime", studyStartTime);
			
			// 등록해놓은 시작시간대 조회
			List<Map<String, Object>> startTimeLog = adminMapper.selectStartTimeLogAll(paramMap);			
			
			// startTimeList 초기값 add
			time = String.format("%02d", start_hour) + String.format("%02d", start_min);
			timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);
			timeMap.put("time", time);
			timeMap.put("timeStamp", timeStamp);			
			
			
			checkCnt = 0; // 배정 가능시간과 근무시간과 동일하면 cnt++
			for (Map<String, Object> log : startTimeLog) {				
				if(log.get("start_time").toString().equals(time)) {
					checkCnt++;	
				}
			}
			timeMap.put("isCheck", checkCnt);
			startTimeList.add(timeMap);
			
			int i = 0; // 초기값 설정
			while (i < 1) { // 조건식
				timeMap = new HashMap<String, Object>();

				start_min += 5;
				
				if (start_min == 60) {
					start_min = 0; // 분 0
					start_hour += 1; // 시간 +1시간
				}

				time = String.format("%02d", start_hour) + String.format("%02d", start_min);
				timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);

				timeMap.put("time", time);
				timeMap.put("timeStamp", timeStamp);	
				
				checkCnt = 0;
				for (Map<String, Object> log : startTimeLog) {				
					if(log.get("start_time").toString().equals(time)) {
						checkCnt++;	
					}
				}
				timeMap.put("isCheck", checkCnt);

				startTimeList.add(timeMap);

				if (start_hour == end_hour && start_min == end_min) { // 시작시간과 종료시간이 같아졌다면 while문 빠져나감
					i++; // 증감식
				}

			} // startTimeList.add END
			
			mav.addObject("startTimeList", startTimeList);
			//System.out.println(startTimeList);
			
		} else {	
			// 등록
			mav.addObject("flag", "insert");			
						
			// startTimeList.add(timeMap);		
			// 06:00 시작 초기값 세팅
			time = String.format("%02d", start_hour) + String.format("%02d", start_min);
			timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);
			timeMap.put("time", time);
			timeMap.put("timeStamp", timeStamp);
			
			startTimeList.add(timeMap);

			int i = 0; // 초기값 설정
			while (i < 1) { // 조건식
				timeMap = new HashMap<String, Object>();

				start_min += 5;
				
				if (start_min == 60) {
					start_min = 0; // 분 0
					start_hour += 1; // 시간 +1시간
				}

				time = String.format("%02d", start_hour) + String.format("%02d", start_min);
				timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);

				timeMap.put("time", time);
				timeMap.put("timeStamp", timeStamp);				

				startTimeList.add(timeMap);

				if (start_hour == end_hour && start_min == end_min) { // 시작시간과 종료시간이 같아졌다면 while문 빠져나감
					i++; // 증감식
				}

			} // startTimeList.add END
						
			mav.addObject("startTimeList", startTimeList);
		}
		return mav;
	}
	
	// 슈퍼관리자 - 수업관리 휴강일
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/holiday") // 휴강일 메뉴
	public ModelAndView admin_holiday(@ModelAttribute StartTimeCriteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_holiday");
		
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "휴강일");
		
		adminMapper.setRownum();
		
		paramMap.put("cri", cri);
		mav.addObject("holiday", adminMapper.selectHolidayAll(paramMap));
		int count = adminMapper.countHolidayAll(paramMap);
		StartTimePageMaker pageMaker = new StartTimePageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("holidayCnt", count);
		mav.addObject("cri", cri);
		
		return mav;
	}	
	
	// 수업관리 휴강일 상세보기 (등록/수정) 페이지
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/holiday/detail") // 휴강일 상세보기
	public ModelAndView admin_holiday_detail(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();	
		mav.setViewName("page/admin/admin_holiday_detail");	
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "휴강일");			
		
		// 상세보기 (수정)
		if (paramMap.containsKey("holiday_no")) {	
			// 수정
			Map<String, Object> holiday = adminMapper.selectHolidayOne(paramMap);
			mav.addObject("item", holiday);
			mav.addObject("flag", "update");
			mav.addObject("sub_title2", "수정");
			
		} else {	
			// 등록
			mav.addObject("flag", "insert");
			mav.addObject("sub_title2", "등록");
			
		}
		return mav;
	}
	
	// 슈퍼관리자 - 수업관리 수업일정
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/classSchedule")
	public ModelAndView admin_classSchedule(@ModelAttribute StartTimeCriteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_classSchedule");		
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "수업일정");		
		adminMapper.setRownum();		
		
		paramMap.put("cri", cri);
		mav.addObject("list", adminMapper.selectClassScheduleAll(paramMap));
		int count = adminMapper.countClassScheduleAll(paramMap);
		StartTimePageMaker pageMaker = new StartTimePageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("classScheduleCnt", count);
		mav.addObject("cri", cri);
		
		return mav;
	}
 
	// 수업일정 상세보기 (등록/수정)
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/classSchedule/detail")
	public ModelAndView admin_classSchedule_detail(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();	
		mav.setViewName("page/admin/admin_classSchedule_detail");	
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "수업일정");
				
		if (paramMap.containsKey("schedule_class_no")) {	
			// 수정
			Map<String, Object> classSchedule = adminMapper.selectClassScheduleOne(paramMap);
			mav.addObject("item", classSchedule);
			mav.addObject("flag", "update");
			mav.addObject("sub_title2", "수정");
		} else {	
			// 등록
			mav.addObject("flag", "insert");
			mav.addObject("sub_title2", "등록");
		}
		
		return mav;
	}
	
	// 슈퍼관리자 - 수업관리 사이트별수강신청
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/scheduleClassSite")
	public ModelAndView admin_classScheduleSite(@ModelAttribute StartTimeCriteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_scheduleClassSite");		
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "사이트별 수강신청");		
		adminMapper.setRownum();
		
		mav.addObject("siteList", adminMapper.getSiteCombo());					// 사이트 검색 콤보
		
		paramMap.put("cri", cri);
		mav.addObject("list", adminMapper.selectSiteClassScheduleAll(paramMap));
		int count = adminMapper.countSiteClassScheduleAll(paramMap);
		StartTimePageMaker pageMaker = new StartTimePageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("classScheduleSiteCnt", count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 사이트별 수강신청 상세보기 (등록/수정)
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/scheduleClassSite/detail")
	public ModelAndView admin_scheduleClassSite_detail(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();	
		mav.setViewName("page/admin/admin_scheduleClassSite_detail");	
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "수업일정");
		
		mav.addObject("siteList", adminMapper.selectSiteList());		
				
		if (paramMap.containsKey("schedule_class_site_no")) {	
			// 수정
			Map<String, Object> classScheduleSite = adminMapper.selectClassScheduleSiteOne(paramMap);
			
			paramMap.put("language_type", classScheduleSite.get("language_type"));			
			//mav.addObject("semesterYmList", adminMapper.selectSemesterYmComboByLanguage(paramMap));	// 언어에 따른 기수년월 리스트
			mav.addObject("item", classScheduleSite);
			mav.addObject("flag", "update");
			mav.addObject("sub_title2", "수정");
		} else {	
			// 등록
			mav.addObject("flag", "insert");
			mav.addObject("sub_title2", "등록");
		}
		
		return mav;
	}
	
	// 슈퍼관리자 - 수업관리 수업기본정보
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/classInfo")
	public ModelAndView admin_classInfo(@ModelAttribute ClassInfoCriteria cri, @RequestParam Map<String, Object> paramMap) throws ParseException{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_classInfo");		
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "수업기본정보");		
		adminMapper.setRownum();	
		paramMap.put("cri", cri);
				
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.selectClassInfoAll(paramMap);		// 수강정보 + 레벨테스트 정보 조회
		
		// 수업상태 - 수업전, 수업중, 종료 (수업기간)
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		Date date = format.parse(today); // ParseException throw
		
		Date start_dt, end_dt;
		int startCompare, endCompare;
		
		for(Map<String, Object> info : list) {
			String[] productWeekSplit = info.get("product_week").toString().split("/");		// 주당수업: tue/thu  // 주 몇회 수업인지
			String class_style = "";
			
			if("class".equals(info.get("flag").toString())) {
				class_style = info.get("product_type").toString() + " / 주" + productWeekSplit.length + "회 / " + info.get("product_personnel").toString() + " / " + info.get("class_student_cnt").toString() + "명";
			} else {
				class_style = info.get("product_type").toString() + " /  / " + info.get("product_personnel").toString() + " / " + info.get("class_student_cnt").toString() + "명";
			}
			info.put("class_style", class_style);
		}
		
		// 검색조건 - 콤보 리스트 (사용 상태)
		mav.addObject("siteList", adminMapper.getSiteCombo());					// 사이트 검색 콤보
		mav.addObject("centerList", adminMapper.getCenterCombo());				// 티칭센터 검색 콤보
		
		mav.addObject("studyList", adminMapper.getStudyCombo(paramMap));		// 언어 검색셀렉트에 따라 where절 추가
		mav.addObject("textbookList", adminMapper.getTextbookCombo(paramMap));	// 교육과정 검색셀렉트에 따라 where절 추가
		
		
		
		
		mav.addObject("list", list);
		int count = adminMapper.countClassInfoAll(paramMap);
		
		ClassInfoPageMaker pageMaker = new ClassInfoPageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("classInfoCnt", count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 수업관리 수업기본정보 상세보기 (등록/수정)
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/classInfo/detail")
	public ModelAndView admin_classInfo_detail(@RequestParam Map<String, Object> paramMap) throws ParseException {
		ModelAndView mav = new ModelAndView();	
		mav.setViewName("page/admin/admin_classInfo_detail");	
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "수업기본정보");
		
		String flag = paramMap.get("flag").toString();
		
		mav.addObject("flag", flag);
		
		Map<String, Object> classInfo = new HashMap<String, Object>();
		List<Map<String, Object>> studentList = new ArrayList<Map<String, Object>>();
		
		if("class".equals(flag)) {	// 정규수업 내용 클릭해서 들어올 시			
			classInfo = adminMapper.selectClassInfoOne(paramMap);
			
			String[] productWeekSplit = classInfo.get("product_week").toString().split("/");		// 주당수업: tue/thu  // 주 몇회 수업인지
			classInfo.put("weekCnt", productWeekSplit.length);
						
			// 수강중인 학생들 정보			
			studentList = adminMapper.selectClassInfoStudentAll(paramMap);
			
			// 수업상태 - 수업전, 수업중, 종료 (수업기간)
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String today = format.format(new Date());
			Date date = format.parse(today); 
			
			Date start_dt, end_dt;
			int startCompare, endCompare;
			
			for(Map<String, Object> map : studentList) {		
							
				start_dt = format.parse(map.get("start_date").toString());
				end_dt = format.parse(map.get("end_date").toString());

				startCompare = date.compareTo(start_dt);
				endCompare = date.compareTo(end_dt);

				if (startCompare < 0) { // 오늘날짜가 수업시작일보다 적다면
					// 수업 전
					map.put("condition", "수업전");
				} else {
					if (endCompare > 0) { // 오늘날짜가 종료일보다 크다면
						map.put("condition", "수업종료");
					} else {
						map.put("condition", "수업중");
					}
				}			
			}
		} else {
			classInfo = adminMapper.selectLevelTestInfoOne(paramMap);			
		}
		
		mav.addObject("classInfo", classInfo);
		
		// 학생별 정보
		mav.addObject("studentList", studentList);
		
		return mav;
	}
	
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/textbookTopic/pageDetail") // 교재 토픽 페이지 정보 (미사용)
	public ModelAndView pageDetail(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_page_detail");
		
		mav.addObject("textbook_topic", adminMapper.selectTextbookTopicOne(paramMap));
		mav.addObject("page", adminMapper.selectTextbookPage(paramMap));
		
		return mav;
	}
	
	// 회원정보 - 수강내역, 문자내역 등 상세보기 (슈퍼관리자 회원명 클릭 시 팝업)
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/userDetail")
	public ModelAndView userDetail(@RequestParam Map<String, Object> paramMap, ClassDetailCriteria cri) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail");
		
		Map<String, Object> student = adminMapper.getUserByStudentNo(paramMap);
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
		
		mav.addObject("user", student);
		
		adminMapper.setRownum();
		
		paramMap.put("cri", cri);	
		
		int classCnt = adminMapper.countClassAndLevelByStudentNo(paramMap);
		List<Map<String, Object>> classList = adminMapper.selectClassAndLevelByStudentNo(paramMap);	// class 및 levelTest 정보 조회
		
		// 수업상태 - 수업전, 수업중, 종료 (수업기간)
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		Date date = format.parse(today); // ParseException throw

		Date start_dt, end_dt;
		int startCompare, endCompare;
		
		ClassDetailPageMaker pageMaker = new ClassDetailPageMaker(cri, classCnt);

		pageMaker.setCri(cri);
		pageMaker.setTotalCount(classCnt);

		mav.addObject("paramMap", paramMap);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("cri", cri);
		mav.addObject("classList", classList);
		mav.addObject("classCnt", classCnt);
		
		if ("normal".equals(paramMap.get("flag").toString())) {	
			// 티칭센터 weekly schedule에서 class나 level테스트 클릭한게 아닌, 신청/배정 페이지에서 회원명 클릭으로 들어올 때
			Map<String, Object> info = new HashMap<String, Object>();
			info.put("flag", "normal");
			mav.addObject("info", info);
		} else if ("class".equals(paramMap.get("flag").toString())) {	
			// Weekly Schedule에서 해당 학생 수강 정보를 클릭하고 들어왔다면 -> 회원상세보기 팝업 수강상세정보에 클릭한 class에 대한 정보가 보여지기 위해 info를 담는다
			Map<String, Object> info = adminMapper.getClassDetailInfo(paramMap);
			
			start_dt = format.parse(info.get("start_date").toString());
			end_dt = format.parse(info.get("end_date").toString());

			startCompare = date.compareTo(start_dt);
			endCompare = date.compareTo(end_dt);
			
			if (startCompare < 0) { // 오늘날짜가 수업시작일보다 적다면
				// 수업 전
				info.put("status", "수업예정");
			} else {
				if (endCompare > 0) { // 오늘날짜가 종료일보다 크다면
					info.put("status", "수업종료");
				} else {
					info.put("status", "수업중");
				}
			}			
			mav.addObject("info", info);		
		} 		
		else if ("levelTest".equals(paramMap.get("flag").toString())) {
			// Weekly Schedule에서 해당 학생 레벨테스트 정보를 클릭하고 들어왔다면 -> 회원상세보기 팝업 수강상세정보에 클릭한 레벨테스트에 대한 정보가 보여지기 위해 info를 담는다.
			Map<String, Object> info = adminMapper.getLevelDetailInfo(paramMap);
			mav.addObject("info", info);
		}  
				
		return mav;
	}
	

	// 수강 상세 정보
	@RequestMapping("/userDetail/userDetail_info")
	public ModelAndView userDetail_info(@RequestParam Map<String, Object> paramMap) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_info");
		
		Map<String, Object> info = new HashMap<String, Object>(); 
   		
   		// 수업상태 - 수업전, 수업중, 종료 (수업기간)
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		Date date = format.parse(today); // ParseException throw

		Date start_dt, end_dt;
		int startCompare, endCompare;   		
   		
   		if ("class".equals(paramMap.get("flag").toString())) {
   			// 수강 목록에서 regular class 내용 클릭 시 가져오는 정보
   			info = adminMapper.getClassDetailInfo(paramMap);
			info.put("isEnd", 0);
			
   			if(info.get("class_status").toString().equals("1")) {
   				end_dt = format.parse(info.get("end_date").toString());
   				endCompare = date.compareTo(end_dt);
   				
   				if(endCompare > 0) {
   					info.put("isEnd", 1);
   				}
   			}
   			
		} else {
			// 수강 목록에서 level test 내용 클릭 시 가져오는 정보
			info = adminMapper.getLevelDetailInfo(paramMap);
			info.put("isEnd", 0);
		}
   		
		mav.addObject("info", info);
		
		return mav;
	}
	
	// 수업에 강사 배정
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/userDetail/userDetail_setTutorClass")
	public ModelAndView userDetail_setTutorClass(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_settutor_class");
		
		System.out.println("@@@@@@ set tutor class");
		System.out.println(paramMap);
		
		Map<String, Object> info = adminMapper.selectClassTutor(paramMap);
		mav.addObject("info", info);
		mav.addObject("study", adminMapper.selectStudyTutor(info.get("product_language").toString()));
		mav.addObject("center", adminMapper.selectCenterTutor());
		mav.addObject("group", adminMapper.selectGroupTutor());
		
		
		return mav;
	}
	
	// 레벨테스트에 강사 배정
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/userDetail/userDetail_setTutorLeveltest")
	public ModelAndView userDetail_setTutorLeveltest(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_settutor_leveltest");

		Map<String, Object> info = adminMapper.selectLeveltestTutor(paramMap);
		mav.addObject("info", info);
		mav.addObject("study", adminMapper.selectStudyTutor(info.get("leveltest_language").toString()));
		mav.addObject("center", adminMapper.selectCenterTutor());
		mav.addObject("group", adminMapper.selectGroupTutor());
		
		return mav;
	}
	
	// 학습 현황
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/userDetail/userDetail_classLog")
	public ModelAndView classLogg(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_class_log");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Calendar cal = Calendar.getInstance();
		
		if(!paramMap.containsKey("search_date")) {
			paramMap.put("search_date", sdf.format(cal.getTime()));
		}
		else {
			try {
				cal.setTime(sdf.parse(paramMap.get("search_date").toString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		cal.add(Calendar.MONTH, 1);
		paramMap.put("next_date", sdf.format(cal.getTime()));
		cal.add(Calendar.MONTH, -2);
		paramMap.put("prev_date", sdf.format(cal.getTime()));
		
		if(paramMap.get("flag").toString().equals("class")) {
			mav.addObject("info", adminMapper.selectClassTutor(paramMap));
			
			paramMap.put("study_status", 0);
			mav.addObject("class_log", adminMapper.selectClassLogStudent(paramMap));
			paramMap.put("study_status", 1);
			mav.addObject("class_log_done", adminMapper.selectClassLogStudent(paramMap));
			
			paramMap.put("class_no", paramMap.get("no"));
		}
		else if(paramMap.get("flag").toString().equals("leveltest")) {
			mav.addObject("info", adminMapper.selectLeveltestTutor(paramMap));
			
			paramMap.put("study_status", 0);
			mav.addObject("class_log", adminMapper.selectLeveltestStudent(paramMap));
			
			paramMap.put("study_status", 1);
			mav.addObject("class_log_done", adminMapper.selectLeveltestStudent(paramMap));
		}
		
		mav.addObject("paramMap", paramMap);
		
		return mav;
	}
	
	// 학생 찾기
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/userDetail/searchStudent")
	public ModelAndView searchStudent(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/search_student");
		
		mav.addObject("site", adminMapper.selectSiteList());
		
		return mav;
	}
	
	// 레벨테스트 생성
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/userDetail/makeLeveltest")
	public ModelAndView makeLeveltest(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/make_leveltest");
		
		mav.addObject("user_student", adminMapper.selectStudentOne(paramMap));
		
		return mav;
	}
	
	// 정규 수업 생성
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/userDetail/makeClass")
	public ModelAndView makeClass(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/make_class");
		Map<String, Object> student = adminMapper.selectStudentOne(paramMap);
		mav.addObject("user_student", student);
		
		paramMap.put("site_no", student.get("site_no"));
		mav.addObject("product", adminMapper.selectProductMakeClass(paramMap));
		mav.addObject("paramMap", paramMap);
		
		return mav;
	}


	// online Book 메뉴
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/onlineBook")
	public ModelAndView onlineBook(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_onlineBook");
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(paramMap);

		if(!paramMap.containsKey("study_language")) {
			paramMap.put("study_language", "English");
		}
		paramMap.put("study_status", 1);

		List<Map<String, Object>> study = adminMapper.selectStudyLang(paramMap);
		mav.addObject("study", study);
		if(!paramMap.containsKey("study_no")) {
			if(study.size() > 0) {
				paramMap.put("study_no", study.get(0).get("study_no"));
			}
		}
		
		List<Map<String, Object>> textbook = adminMapper.selectTextbookByStudy(paramMap);
		mav.addObject("textbook", textbook);
		if(!paramMap.containsKey("textbook_no")) {
			if(textbook.size() > 0) {
				paramMap.put("textbook_no", textbook.get(0).get("textbook_no"));
			}
		}
		
		List<Map<String, Object>> textbookTopic = adminMapper.selectTextbookTopic(paramMap);
		mav.addObject("textbookTopic", textbookTopic);
		if(!paramMap.containsKey("textbook_topic_no")) {
			if(textbookTopic.size() > 0) {
				paramMap.put("textbook_topic_no", textbookTopic.get(0).get("textbook_topic_no"));
			}
		}
		
		if(paramMap.containsKey("textbook_topic_no")) {
			mav.addObject("pdf_file", adminMapper.selectTextbookTopicOne(paramMap).get("pdf_file"));
		}
		
//		List<Map<String, Object>> textbookTopic = adminMapper.selectTextbookTopicHavePage(paramMap);
//		mav.addObject("textbookTopic", textbookTopic);
//		if(!paramMap.containsKey("textbook_topic_no")) {
//			if(textbookTopic.size() > 0) {
//				paramMap.put("textbook_topic_no", textbookTopic.get(0).get("textbook_topic_no"));
//			}
//		}
		
		mav.addObject("language", paramMap.get("study_language"));
		mav.addObject("study_no", paramMap.get("study_no"));
		mav.addObject("textbook_no", paramMap.get("textbook_no"));
		mav.addObject("textbook_topic_no", paramMap.get("textbook_topic_no"));
		
//		List<Map<String, Object>> textbookPage = adminMapper.selectTextbookPage(paramMap);
//		mav.addObject("textbookPage", textbookPage);
		
		return mav;
	}
	
	// onlineBook 상세보기
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/onlineBookDetail")
	public ModelAndView onlineBookDetail(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_onlineBook_detail");
		
		mav.addObject("textbook_topic_no", paramMap.get("textbook_topic_no"));
		
		cri.setPerPageNum(1);
		paramMap.put("cri", cri);
		mav.addObject("page", adminMapper.selectTextbookPageLimit(paramMap));
		int page_count = adminMapper.countTextbookPage(paramMap);
		PageMaker pageMaker = new PageMaker(cri, page_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(page_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("pageCnt", page_count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 휴강 및 보강 메뉴
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/postponeMng")
	public ModelAndView postponeMng(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_postpone");
		
		paramMap.put("cri", cri);
		mav.addObject("postpone", adminMapper.selectPostponeAll(paramMap));
		int postpone_count = adminMapper.countPostponeAll(paramMap);
		PageMaker pageMaker = new PageMaker(cri, postpone_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(postpone_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("postponeCnt", postpone_count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 슈퍼관리자 - 사이트관리 선생님별주간수업일정
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/weeklySchedule")
	public ModelAndView admin_weeklySchedule(@RequestParam Map<String, Object> paramMap, ScheduleCriteria cri,
			HttpSession session) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_weeklySchedule");

		mav.addObject("main_title", "사이트관리");
		mav.addObject("sub_title", "선생님별 주간수업일정");
		paramMap.put("cri", cri);
		
		mav.addObject("centerList", adminMapper.selectCenterCombo());						// 티칭센터 콤보박스
		mav.addObject("teacherList", adminMapper.selectTeacherComboByCenterNo(paramMap));	// 강사 콤보박스 

		// 금주 날짜정보 가져오기
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());		

		// 검색기준일: 최초 검색 시 오늘날짜를 기준으로 thisDay를 설정하며, 오늘날짜 기준 금주 일주일 내용을 검색한다.
		// 검색 후 Prev or Next버튼 클릭시 thisDay는 일주일 씩 변경값으로 설정됨
		if (!paramMap.containsKey("thisDay") || "".equals(paramMap.get("thisDay").toString())) { // 오늘날짜 기준 > 이번주 날짜 가져오기 // Next, Prev 버튼으로 다음주, 이번주 날짜 가져올땐 thisDay
												// 파라미터로 넘어오도록
			paramMap.put("thisDay", today);		// 기준일
			cri.setThisDay(paramMap.get("thisDay").toString());
		}
		mav.addObject("thisDay", paramMap.get("thisDay"));

		Map<String, Object> weeklyDays = adminMapper.getWeeklyDaysFromSun(paramMap); // 일요일시작 이번주 날짜 가져오기 param: thisDay																						
		//System.out.println(weeklyDays);		
		mav.addObject("period", weeklyDays.get("day0").toString() + " ~ " + weeklyDays.get("day6").toString());	// 검색되고있는 수업기간 표시
		
		paramMap.put("user_teacher_no", paramMap.get("searchTeacher"));
		
		// 강사 셀렉트박스가 선택되어있다면 내용 조회
		if (paramMap.get("searchTeacher") != null && paramMap.get("searchTeacher") != "") {
			mav.addObject("flag", "selected"); // 강사 선택됨
			
			Map<String, Object> teacher = adminMapper.selectTeacherOne(paramMap); // param: user_teacher_no (teacher_no)

			// 등록되어있는 근무시간표 가져오기 timetable
			List<String> timeTable = new ArrayList<String>();
			timeTable = adminMapper.getTimeTable(paramMap); // param: no (teacher_no)

			// 근무시간
			String start_time = teacher.get("start_time").toString();
			String end_time = teacher.get("end_time").toString();

			int start_hour = Integer.parseInt(teacher.get("start_hour").toString());
			int start_min = Integer.parseInt(teacher.get("start_min").toString());
			int end_hour = Integer.parseInt(teacher.get("end_hour").toString());
			int end_min = Integer.parseInt(teacher.get("end_min").toString());

			// 근무시간 시간 List
			// List<String> timeTableList = new ArrayList<String>();
			List<Map<String, Object>> timeTableList = new ArrayList<Map<String, Object>>();
			Map<String, Object> timeMap = new HashMap<String, Object>();
			String time, timeStamp;
			int checkCnt = 0;

			// 해당 날짜 Class 정보 가져오기 (잘못된 등록으로, 같은 시간대에 중복으로 잡혀있는 클래스가 있을 수 있어서 list로 받아옴)
			List<Map<String, Object>> classInfoList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> levelTestInfoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> classParam = new HashMap<String, Object>();

			// if~else : 배정시간 및 근무가능시간 가져오기 - 위 강사정보조회 상세보기에서 시간표 가져오는거랑 유사한 기능 처리 코드 (/admin/teacher/detail) 
			if (("0000".equals(start_time) && "0000".equals(end_time)) && start_time.equals(end_time)) { // 배정시간 수정 전 // (초기상태)
				timeMap.put("time", "00:00");
				
			} else { // 배정시간 지정 후

				// timeTableList 초기값 add
				time = String.format("%02d", start_hour) + String.format("%02d", start_min);
				timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);
				timeMap.put("time", timeStamp);
				for (int i = 0; i <= 6; i++) {
					timeMap.put("d" + i, i + "_" + time);

					checkCnt = 0; // 배정 가능시간과 근무시간과 동일하면 cnt++
					for (String ableTime : timeTable) {
						// System.out.println(ableTime);
						if (ableTime.equals(i + "_" + time)) {
							checkCnt++;
						}
					}

					// timeMap.put("isCheck" + i, checkCnt);
					timeMap.put("state" + i, (checkCnt == 1 ? "possible" : ""));
					
					/************************ 레벨테스트 정보 가져와서 시간표와 비교 *************************/
					String day = weeklyDays.get("day" + i).toString();
					timeMap.put("lessonTime" + i, 0);	// 초기값
					timeMap.put("tdText" + i, "");		// 초기값
					

					// teacher_no, 날짜, 시간으로 레벨테스트 정보 가져온다.
					classParam = new HashMap<String, Object>();
					classParam.put("user_teacher_no", teacher.get("user_teacher_no"));
					classParam.put("study_date", day);
					classParam.put("class_time", timeStamp);
					
					// 레벨테스트 정보 추가
					levelTestInfoList = adminMapper.getLevelTestInfoList(classParam);
					timeMap.put("levelTestInfoList"+i, levelTestInfoList);
					
					/************************ 레벨테스트 정보 가져와서 시간표와 비교 *************************/
					String levelStudentInfoListString = "";	// user_student_no,user_name#user_student_no,user_name... 

					if(levelTestInfoList.size() > 0) {
						timeMap.put("state" + i, "LevelTest");						
						int lessonTime = Integer.parseInt(levelTestInfoList.get(0).get("lesson_time").toString().substring(0, 2));
						
						for(int x=0; x<levelTestInfoList.size(); x++) {
							List<Map<String, Object>> levelTestStudentList = adminMapper.getLevelTestStudentList(levelTestInfoList.get(x));
							
							// 해당 class_no를 수강하고있는 학생번호,학생명 List
							for(int z=0; z < levelTestStudentList.size(); z++) {
								levelStudentInfoListString += levelTestStudentList.get(z).get("user_student_no").toString()+","+levelTestStudentList.get(z).get("student_name").toString()+","+levelTestStudentList.get(z).get("id").toString();
								
								if((z + 1) != levelTestStudentList.size()) {
									levelStudentInfoListString += "#";
								}
							}
							
							levelTestInfoList.get(x).put("levelStudentInfoListString", levelStudentInfoListString);
						}
						
						
						timeMap.put("lessonTime" + i, lessonTime);
						timeMap.put("tdText" + i, "LevelTest");
					}					
					
					
					/************************ 유료수강 정보 가져와서 시간표와 비교 *************************/
					// 해당강사, 날짜, 시간으로 class_log 정보 가져온다.
					classInfoList = adminMapper.getClassInfoList(classParam);
					String studentInfoListString = "";	// user_student_no,user_name#user_student_no,user_name... 

					// 해당 시간대에 수업이 있다면 In Class로 color 표시 및 클릭 기능 처리 하기위해
					if(classInfoList.size() > 0) {
						timeMap.put("state" + i, "inClass");						
						int lessonTime = Integer.parseInt(classInfoList.get(0).get("lesson_time").toString().substring(0, 2));
						
						for(int x=0; x<classInfoList.size(); x++) {
							List<Map<String, Object>> classStudentList = adminMapper.getClassStudentList(classInfoList.get(x));
							
							// 해당 class_no를 수강하고있는 학생번호,학생명 List
							for(int z=0; z < classStudentList.size(); z++) {
								studentInfoListString += classStudentList.get(z).get("user_student_no").toString()+","+classStudentList.get(z).get("student_name").toString()+","+classStudentList.get(z).get("id").toString();
								
								if((z + 1) != classStudentList.size()) {
									studentInfoListString += "#";
								}
							}
							
							classInfoList.get(x).put("studentInfoListString", studentInfoListString);	// class클릭 시 보여줄 해당 수업을 듣는 학생들 정보
							
							
							if(lessonTime < Integer.parseInt(classInfoList.get(x).get("lesson_time").toString().substring(0, 2))) {
								lessonTime = Integer.parseInt(classInfoList.get(x).get("lesson_time").toString().substring(0, 2));
							}
						}						
						
						timeMap.put("lessonTime" + i, lessonTime);
						timeMap.put("tdText" + i, "Class");
						
					}
										
					timeMap.put("levelTestInfoList"+ i, levelTestInfoList);		// 해당 강사, 날짜, 시간대에 표시될 leveltest 정보
					timeMap.put("classInfoList"+ i, classInfoList);				// 해당 강사, 날짜, 시간대에 표시될 class 정보
					timeMap.put("lessonTime" + i, 0);							// 수강 log 없는 td: default 0
					//timeMap.put("tdText" + i, "");
					
					
					if(levelTestInfoList.size() > 0 && classInfoList.size() > 0) {						
						timeMap.put("tdText" + i, "Class & LevelTest");
					}

				}

				timeTableList.add(timeMap);

				int i = 0; // 초기값 설정
				while (i < 1) { // 조건식
					timeMap = new HashMap<String, Object>();

					start_min += 10;

					if (start_min == 60) {
						start_min = 0; // 분 0
						start_hour += 1; // 시간 +1시간
					}

					time = String.format("%02d", start_hour) + String.format("%02d", start_min);
					timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);

					timeMap.put("time", timeStamp);
					for (int k = 0; k <= 6; k++) {
						timeMap.put("d" + k, k + "_" + time);

						checkCnt = 0; // 배정 가능시간과 근무시간과 동일하면 cnt++
						for (String ableTime : timeTable) {
							// System.out.println(ableTime);
							if (ableTime.equals(k + "_" + time)) {
								checkCnt++;
							}
						}
						// timeMap.put("isCheck" + j, checkCnt);
						timeMap.put("state" + k, (checkCnt == 1 ? "possible" : ""));

						/************************ 레벨테스트 정보 가져와서 시간표와 비교 *************************/
						String day = weeklyDays.get("day" + k).toString();
						timeMap.put("lessonTime" + k, 0);	// 초기값
						timeMap.put("tdText" + k, "");		// 초기값
						

						// teacher_no, 날짜, 시간으로 class_log 정보 가져온다.
						classParam = new HashMap<String, Object>();
						classParam.put("user_teacher_no", teacher.get("user_teacher_no"));
						classParam.put("study_date", day);
						classParam.put("class_time", timeStamp);					
						
						// 레벨테스트 정보 추가
						levelTestInfoList = adminMapper.getLevelTestInfoList(classParam);
						timeMap.put("levelTestInfoList"+k, levelTestInfoList);
						
						/************************ 레벨테스트 정보 가져와서 시간표와 비교 *************************/
						String levelStudentInfoListString = "";	// user_student_no,user_name#user_student_no,user_name... 

						if(levelTestInfoList.size() > 0) {
							timeMap.put("state" + k, "LevelTest");						
							int lessonTime = Integer.parseInt(levelTestInfoList.get(0).get("lesson_time").toString().substring(0, 2));
							
							for(int x=0; x<levelTestInfoList.size(); x++) {
								List<Map<String, Object>> levelTestStudentList = adminMapper.getLevelTestStudentList(levelTestInfoList.get(x));
								
								// 해당 class_no를 수강하고있는 학생번호,학생명 List
								for(int z=0; z < levelTestStudentList.size(); z++) {
									levelStudentInfoListString += levelTestStudentList.get(z).get("user_student_no").toString()+","+levelTestStudentList.get(z).get("student_name").toString()+","+levelTestStudentList.get(z).get("id").toString();
									
									if((z + 1) != levelTestStudentList.size()) {
										levelStudentInfoListString += "#";
									}
								}
								
								levelTestInfoList.get(x).put("levelStudentInfoListString", levelStudentInfoListString);
							}
							
							timeMap.put("lessonTime" + k, lessonTime);
							timeMap.put("tdText" + k, "LevelTest");
						}
						
						
						/************************ 유료수강 정보 가져와서 시간표와 비교 *************************/						
						classInfoList = adminMapper.getClassInfoList(classParam);
						String studentInfoListString = "";	// user_student_no,user_name#user_student_no,user_name... 

						if(classInfoList.size() > 0) {
							timeMap.put("state" + k, "inClass");						
							int lessonTime = Integer.parseInt(classInfoList.get(0).get("lesson_time").toString().substring(0, 2));
							
							for(int x=0; x<classInfoList.size(); x++) {
								List<Map<String, Object>> classStudentList = adminMapper.getClassStudentList(classInfoList.get(x));
								
								// 해당 class_no를 수강하고있는 학생번호,학생명 List
								for(int z=0; z < classStudentList.size(); z++) {
									studentInfoListString += classStudentList.get(z).get("user_student_no").toString()+","+classStudentList.get(z).get("student_name").toString()+","+classStudentList.get(z).get("id").toString();
									
									if((z + 1) != classStudentList.size()) {
										studentInfoListString += "#";
									}
								}
								classInfoList.get(x).put("studentInfoListString", studentInfoListString);
								
								
								if(lessonTime < Integer.parseInt(classInfoList.get(x).get("lesson_time").toString().substring(0, 2))) {
									lessonTime = Integer.parseInt(classInfoList.get(x).get("lesson_time").toString().substring(0, 2));
								}
							}
							
							timeMap.put("lessonTime" + k, lessonTime);
							timeMap.put("tdText" + k, "Class");					
							
						}
						
						timeMap.put("classInfoList"+k, classInfoList);								
						timeMap.put("levelTestInfoList"+ k, levelTestInfoList);
						
						if(levelTestInfoList.size() > 0 && classInfoList.size() > 0) {						
							timeMap.put("tdText" + k, "Class & LevelTest");
						}
						
						
						/****************************************************************************/
					}
					timeTableList.add(timeMap);

					if (start_hour == end_hour && start_min == end_min) { // 배정 시작시간과 종료시간이 같아졌다면 while문 빠져나감
						i++; // 증감식
					}

				} // timeTableList.add  While문 END

			}
			System.out.println("timeTableList timeTableList timeTableList timeTableList");
			System.out.println(timeTableList);			
			mav.addObject("timeTableList", timeTableList);

		}

		//////////////////////////////////////////////////////////////////////////

		mav.addObject("weeklyDays", weeklyDays);
		mav.addObject("cri", cri);

		return mav;
	}
	
	// 휴강 및 보강 상세보기
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/postponeMng/detail")
	public ModelAndView postponeDetail(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_postpone_detail");
		
		mav.addObject("cri", cri);
		mav.addObject("postpone", adminMapper.selectPostponeOne(paramMap));
		
		return mav;
	}
	
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/classStudentMng")
	public ModelAndView classStudentMng(@ModelAttribute ClassStudentCriteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_classStudent_mng");		
		paramMap.put("cri", cri);
		
		
		// 검색조건 - 콤보 리스트
		mav.addObject("siteList", adminMapper.getSiteCombo());
		mav.addObject("studyList", adminMapper.getStudyCombo(paramMap));		// 언어 검색셀렉트에 따라 where절 추가
		mav.addObject("productList", adminMapper.getProductCombo(paramMap));	// 언어 검색셀렉트에 따라 where절 추가
		
		mav.addObject("class_student", adminMapper.selectClassStudentAll(paramMap));
		int class_student_count = adminMapper.countClassStudentAll(paramMap);
		ClassStudentPageMaker pageMaker = new ClassStudentPageMaker(cri, class_student_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(class_student_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("classStudentCnt", class_student_count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 슈퍼관리자 - 수업관리 시간별수업현황
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/timeClass")
	public ModelAndView admin_timeClass(@ModelAttribute TimeClassCriteria cri, @RequestParam Map<String, Object> paramMap) throws ParseException{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_timeClass");		
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "시간별 수업현황");		
		adminMapper.setRownum();
		
		String class_time = "";
		int running_time = 0;
		
		int class_hour, class_min;
		String[] classTimeSplit = null;		// class_time -> 시간과 분으로 나누기 위해 사용
		
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		
		if(paramMap.get("searchStartDate") == null) {			
			cri.setSearchStartDate(today);	// 최초 페이지 로드 시 startdate, enddate 오늘날짜로 세팅
			cri.setSearchEndDate(today);
			
			paramMap.put("searchStartDate", today);
			paramMap.put("searchEndDate", today);
		}		
		paramMap.put("cri", cri);
		
		int end_hour, end_min;
		String class_endTime = "";
		
		// 시간별 수업현황 조회 리스트
		List<Map<String, Object>> list = adminMapper.selectTimeClassAll(paramMap);
		
		for(Map<String, Object> map : list) {
			// 수업타입이 video인 수업에 대해서만 Video Log 버튼이 보이도록
			if (map.get("class_type").toString().toLowerCase().contains("video")){
				map.put("classVideoFlag", "1");
			} else {
				map.put("classVideoFlag", "0");
			}
			
			
			// endTime 계산
			if(map.containsKey("class_time")) {
				class_time = map.get("class_time").toString();
				classTimeSplit = class_time.split(":");
				running_time = Integer.parseInt(map.get("running_time").toString());
				
				class_hour = Integer.parseInt(classTimeSplit[0]);
				class_min = Integer.parseInt(classTimeSplit[1]);
				
				end_hour = class_hour;
				end_min = class_min + running_time;
				
				if(end_min >= 60) {
					end_hour = end_hour + (end_min / 60);	// end_hour + end_min 몫			
					end_min = end_min % 60;					// 나머지
					if (end_hour >= 24) {
						end_hour = end_hour - 24;
					}
				}				
				class_endTime = String.format("%02d", end_hour) + ":" + String.format("%02d", end_min);
			}
			
			map.put("class_endTime", class_endTime);
		}
		
		// 검색조건 - 콤보 리스트
		mav.addObject("siteList", adminMapper.getSiteCombo());				// 사이트 셀렉트박스 내용
		mav.addObject("centerList", adminMapper.getCenterCombo());			// 티칭센터 셀렉트박스 내용
		
		mav.addObject("studyList", adminMapper.getStudyCombo(paramMap));	// 언어 검색셀렉트에 따라 where절 추가
		mav.addObject("textbookList", adminMapper.getTextbookCombo(paramMap));	// 교육과정 검색셀렉트에 따라 where절 추가
		
		mav.addObject("teacherList", adminMapper.getTeacherComboByLanguage(paramMap));	// 언어 검색셀렉트에 따라 where절 추가
		
		
		mav.addObject("list", list);
		int count = adminMapper.countTimeClassAll(paramMap);		
		
		TimeClassPageMaker pageMaker = new TimeClassPageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("timeClassCnt", count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 레벨테스트 신청 리스트 메뉴
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/leveltestMng")
	public ModelAndView leveltestMng(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_leveltest_mng");
		
		paramMap.put("cri", cri);
		mav.addObject("leveltest", adminMapper.selectLevelTestAll(paramMap));
		int leveltest_count = adminMapper.countLevelTestAll(paramMap);
		PageMaker pageMaker = new PageMaker(cri, leveltest_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(leveltest_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("leveltestCnt", leveltest_count);
		mav.addObject("cri", cri);
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@");
		System.out.println(adminMapper.selectLevelTestAll(paramMap));
		return mav;
	}
		
	// 수강상품 메뉴
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/productMng")
	public ModelAndView productMng(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_product_mng");
		
		mav.addObject("main_title", "주문정보관리");
		
		paramMap.put("cri", cri);
		mav.addObject("product", adminMapper.selectProductAll(paramMap));
		int product_count = adminMapper.countProductAll(paramMap);
		PageMaker pageMaker = new PageMaker(cri, product_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(product_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("productCnt", product_count);
		mav.addObject("cri", cri);
		
		return mav;
	}

	// 수강상품 상세보기
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/productMng/detail")
	public ModelAndView productDetail(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_product_detail");

		mav.addObject("main_title", "주문정보관리");
		mav.addObject("cri", cri);
		
		
		mav.addObject("site", adminMapper.selectSiteList());
		if(paramMap.containsKey("product_no")) {
			// 수정
			Map<String, Object> product = adminMapper.selectProductOne(paramMap);
			mav.addObject("product", product);
			mav.addObject("sub_title", "수강상품 수정");
			mav.addObject("flag", "update");
		}
		else {
			// 등록
			mav.addObject("sub_title", "수강상품 등록");
			mav.addObject("flag", "insert");
		}
		
		return mav;
	}
	
	// 신청내역 메뉴
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/paymentMng")
	public ModelAndView paymentMng(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_payment_mng");
		
		mav.addObject("main_title", "신청내역");
		
		paramMap.put("cri", cri);
		List<Map<String, Object>> payment = adminMapper.selectPaymentAll(paramMap);
		for(Map<String, Object> p : payment) {
			String[] address = p.get("payment_address").toString().split("/");
			if(address.length > 0) {
				p.put("address", address[0]+" "+address[1]+" "+address[2]);
			}
			else {
				p.put("address", "");
			}
		}
		mav.addObject("payment", payment);
		int product_count = adminMapper.countPaymentAll(paramMap);
		PageMaker pageMaker = new PageMaker(cri, product_count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(product_count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("paymentCnt", product_count);
		mav.addObject("cri", cri);
		
		return mav;
	}
	
	// 슈퍼관리자 - 사이트관리 강사그룹관리
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/tutorGroup")
	public ModelAndView admin_teacherGroup(@RequestParam Map<String, Object> paramMap) throws ParseException{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_tutorGroup");		
		mav.addObject("main_title", "사이트관리");
		mav.addObject("sub_title", "강사그룹관리");	
		
		List<Map<String, Object>> centerArr = adminMapper.selectCenterByTeacherGroup();	// 티칭센터 List
		mav.addObject("centerArr", centerArr);		
		mav.addObject("groupArr", adminMapper.selectGroupByTeacherGroup());				// 강사그룹 List
		
		return mav;
	}
	
	
	// 관리자 - Teacher' Page Today Schedule	// 수업관리 시간별 수업현황과 유사한 기능
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/todaySchedule")
	public ModelAndView admin_todaySchedule(@RequestParam Map<String, Object> paramMap, TodayScheduleCriteria cri) throws ParseException {	
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_todaySchedule");

		adminMapper.setRownum();

		mav.addObject("main_title", "Teacher' Page");
		mav.addObject("sub_title", "Today Schedule");		
		paramMap.put("cri", cri);
		
		if("".equals(cri.getSearchStartHour())) cri.setSearchStartHour("06"); 
		if("".equals(cri.getSearchEndHour())) cri.setSearchEndHour("24");
		
		String[] hourList = { "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};				
		mav.addObject("hourList", hourList);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		if (!paramMap.containsKey("searchDate") || "".equals(paramMap.get("searchDate").toString())) {
			// 파라미터로 넘어오도록
			paramMap.put("searchDate", today);		// 기준일
			cri.setSearchDate(paramMap.get("searchDate").toString());
		}
		mav.addObject("searchDate", paramMap.get("searchDate"));
		
		int classCnt = adminMapper.countTodayScheduleByAdmin(paramMap);
		List<Map<String, Object>> classList = adminMapper.selectTodayScheduleByAdmin(paramMap);
		
		String class_time = "";
		int running_time = 0;
		
		int class_start_hour = 0;
		int class_start_min = 0;
		String[] classTimeSplit = null;
		
		int class_end_hour = 0;
		int class_end_min = 0;
		String class_endTime = "";
		
		int searchStartHour = Integer.parseInt(cri.getSearchStartHour());
		int searchEndHour = Integer.parseInt(cri.getSearchEndHour());
		
		boolean isClear = false;
		ArrayList<Integer> removeIdxList = new ArrayList<Integer>();
		
		for(int i=0; i<classList.size(); i++) {
			// endTime 계산
			if(classList.get(i).containsKey("class_time")) {
				class_time = classList.get(i).get("class_time").toString();
				classTimeSplit = class_time.split(":");
				running_time = Integer.parseInt(classList.get(i).get("running_time").toString());
				
				class_start_hour = Integer.parseInt(classTimeSplit[0]);
				class_start_min = Integer.parseInt(classTimeSplit[1]);
				
				class_end_hour = class_start_hour;
				class_end_min = class_start_min + running_time;
				
				if(class_end_min >= 60) {
					class_end_hour = class_end_hour + (class_end_min / 60);	// end_hour + end_min 몫			
					class_end_min = class_end_min % 60;					// 나머지
					if (class_end_hour >= 24) {
						class_end_hour = class_end_hour - 24;
					}
				}				
				class_endTime = String.format("%02d", class_end_hour) + ":" + String.format("%02d", class_end_min);
				
				
				// 시간 검색조건에 따른 처리				
				// class_start_hour, class_start_min, class_end_hour, class_end_min
				if(class_end_hour == 0) class_end_hour = 24;
								
				
				if (searchStartHour >= searchEndHour ) {
					isClear = true;
				} else {
					if ((searchStartHour <= class_start_hour && class_start_hour <= searchEndHour) && (searchStartHour <= class_end_hour && class_end_hour <= searchEndHour)) {
						// class_start_time 관련..
						if(class_start_hour == searchEndHour && class_start_min > 0) {
							removeIdxList.add(i);
						}						
						// class_end_time 관련..
						else if(class_end_hour == searchEndHour && class_end_min > 0) {
							removeIdxList.add(i);
						}
						
					} else {
						removeIdxList.add(i);
					}
				}
			}
			
			classList.get(i).put("class_endTime", class_endTime);
		}
		
		// 시간 검색조건 잘못되어 있을 시
		if(isClear) {
			classList = new ArrayList<Map<String, Object>>();
		}
		
		int idx = 0;
		if(classList.size() > 0 && removeIdxList.size() > 0) {
			for(int i=removeIdxList.size() - 1; i>=0; i--) {
				idx = removeIdxList.get(i);
				classList.remove(idx);
			}
		}
		
		TodaySchedulePageMaker pageMaker = new TodaySchedulePageMaker(cri, classCnt);

		pageMaker.setCri(cri);
		pageMaker.setTotalCount(classCnt);
		
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("cri", cri);
		mav.addObject("classList", classList);
		mav.addObject("classCnt", classCnt);
		
		return mav;
	}
	
	
		
	// 관리자 Teachers'Page  Weekly Schedule -> 선생님별 주간수업일정과 동일  
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/tPage/weeklySchedule")
	public ModelAndView admin_teachersPage_weeklySchedule(@RequestParam Map<String, Object> paramMap, ScheduleCriteria cri,
			HttpSession session) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_teachersPage_weeklySchedule");

		mav.addObject("main_title", "Teacher's Page");
		mav.addObject("sub_title", "Weekly Schedule");
		paramMap.put("cri", cri);
		
		mav.addObject("centerList", adminMapper.selectCenterCombo());	// 티칭센터 콤보박스
		mav.addObject("teacherList", adminMapper.selectTeacherComboByCenterNo(paramMap));	// 강사 콤보박스 searchCenter

		// 금주 날짜정보 가져오기
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		// Date date = format.parse(today); // ParseException throw

		if (!paramMap.containsKey("thisDay") || "".equals(paramMap.get("thisDay").toString())) { // 오늘날짜 기준 > 이번주 날짜 가져오기 // Next, Prev 버튼으로 다음주, 이번주 날짜 가져올땐 thisDay
												// 파라미터로 넘어오도록
			paramMap.put("thisDay", today);		// 기준일
			cri.setThisDay(paramMap.get("thisDay").toString());
		}
		mav.addObject("thisDay", paramMap.get("thisDay"));

		Map<String, Object> weeklyDays = adminMapper.getWeeklyDaysFromSun(paramMap); // 일요일시작 이번주 날짜 가져오기 param: thisDay																						
		//System.out.println(weeklyDays);		
		mav.addObject("period", weeklyDays.get("day0").toString() + " ~ " + weeklyDays.get("day6").toString());
		
		paramMap.put("user_teacher_no", paramMap.get("searchTeacher"));
		

		if (paramMap.get("searchTeacher") != null && paramMap.get("searchTeacher") != "") {
			mav.addObject("flag", "selected"); // 강사 선택됨
			
			Map<String, Object> teacher = adminMapper.selectTeacherOne(paramMap); // param: user_teacher_no (teacher_no)

			// 등록되어있는 근무시간표 가져오기 timetable
			List<String> timeTable = new ArrayList<String>();
			timeTable = adminMapper.getTimeTable(paramMap); // param: no (teacher_no)

			// 근무시간
			String start_time = teacher.get("start_time").toString();
			String end_time = teacher.get("end_time").toString();

			int start_hour = Integer.parseInt(teacher.get("start_hour").toString());
			int start_min = Integer.parseInt(teacher.get("start_min").toString());
			int end_hour = Integer.parseInt(teacher.get("end_hour").toString());
			int end_min = Integer.parseInt(teacher.get("end_min").toString());

			// 근무시간 시간 List
			// List<String> timeTableList = new ArrayList<String>();
			List<Map<String, Object>> timeTableList = new ArrayList<Map<String, Object>>();
			Map<String, Object> timeMap = new HashMap<String, Object>();
			String time, timeStamp;
			int checkCnt = 0;

			// 해당 날짜 Class 정보 가져오기 (잘못된 등록으로, 같은 시간대에 중복으로 잡혀있는 클래스가 있을 수 있어서 list로 받아옴)
			List<Map<String, Object>> classInfoList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> levelTestInfoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> classParam = new HashMap<String, Object>();

			if (("0000".equals(start_time) && "0000".equals(end_time)) && start_time.equals(end_time)) { // 배정시간 수정 전
																											// (초기상태)
				timeMap.put("time", "00:00");
				/*
				 * for(int i=0; i<=6; i++) { timeMap.put("d"+i , i+"_0000"); }
				 * timeTableList.add(timeMap);
				 */
			} else { // 배정시간 지정 후

				// timeTableList 초기값 add
				time = String.format("%02d", start_hour) + String.format("%02d", start_min);
				timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);
				timeMap.put("time", timeStamp);
				for (int i = 0; i <= 6; i++) {
					timeMap.put("d" + i, i + "_" + time);

					checkCnt = 0; // 배정 가능시간과 근무시간과 동일하면 cnt++
					for (String ableTime : timeTable) {
						// System.out.println(ableTime);
						if (ableTime.equals(i + "_" + time)) {
							checkCnt++;
						}
					}

					// timeMap.put("isCheck" + i, checkCnt);
					timeMap.put("state" + i, (checkCnt == 1 ? "possible" : ""));
					
					/************************ 레벨테스트 정보 가져와서 시간표와 비교 *************************/
					String day = weeklyDays.get("day" + i).toString();
					timeMap.put("lessonTime" + i, 0);	// 초기값
					timeMap.put("tdText" + i, "");		// 초기값
					

					// teacher_no, 날짜, 시간으로 class_log 정보 가져온다.
					classParam = new HashMap<String, Object>();
					classParam.put("user_teacher_no", teacher.get("user_teacher_no"));
					classParam.put("study_date", day);
					classParam.put("class_time", timeStamp);					
					
					/*	0712 */
					// 레벨테스트 정보 추가
					levelTestInfoList = adminMapper.getLevelTestInfoList(classParam);						
					//System.out.println(levelTestInfoList);
					timeMap.put("levelTestInfoList"+i, levelTestInfoList);
					
					/************************ 레벨테스트 정보 가져와서 시간표와 비교 *************************/
					String levelStudentInfoListString = "";	// user_student_no,user_name#user_student_no,user_name... 

					if(levelTestInfoList.size() > 0) {
						timeMap.put("state" + i, "LevelTest");						
						int lessonTime = Integer.parseInt(levelTestInfoList.get(0).get("lesson_time").toString().substring(0, 2));
						
						for(int x=0; x<levelTestInfoList.size(); x++) {
							List<Map<String, Object>> levelTestStudentList = adminMapper.getLevelTestStudentList(levelTestInfoList.get(x));
							
							// 해당 class_no를 수강하고있는 학생번호,학생명 List
							for(int z=0; z < levelTestStudentList.size(); z++) {
								levelStudentInfoListString += levelTestStudentList.get(z).get("user_student_no").toString()+","+levelTestStudentList.get(z).get("student_name").toString()+","+levelTestStudentList.get(z).get("id").toString();
								
								if((z + 1) != levelTestStudentList.size()) {
									levelStudentInfoListString += "#";
								}
							}
							
							levelTestInfoList.get(x).put("levelStudentInfoListString", levelStudentInfoListString);
						}
						
						
						timeMap.put("lessonTime" + i, lessonTime);
						timeMap.put("tdText" + i, "LevelTest");
					}					
					
					
					/************************ 유료수강 정보 가져와서 시간표와 비교 *************************/
					classInfoList = adminMapper.getClassInfoList(classParam);
					String studentInfoListString = "";	// user_student_no,user_name#user_student_no,user_name... 

					if(classInfoList.size() > 0) {
						timeMap.put("state" + i, "inClass");						
						int lessonTime = Integer.parseInt(classInfoList.get(0).get("lesson_time").toString().substring(0, 2));
						
						for(int x=0; x<classInfoList.size(); x++) {
							List<Map<String, Object>> classStudentList = adminMapper.getClassStudentList(classInfoList.get(x));
							
							// 해당 class_no를 수강하고있는 학생번호,학생명 List
							for(int z=0; z < classStudentList.size(); z++) {
								studentInfoListString += classStudentList.get(z).get("user_student_no").toString()+","+classStudentList.get(z).get("student_name").toString()+","+classStudentList.get(z).get("id").toString();
								
								if((z + 1) != classStudentList.size()) {
									studentInfoListString += "#";
								}
							}
							
							classInfoList.get(x).put("studentInfoListString", studentInfoListString);
							
							
							if(lessonTime < Integer.parseInt(classInfoList.get(x).get("lesson_time").toString().substring(0, 2))) {
								lessonTime = Integer.parseInt(classInfoList.get(x).get("lesson_time").toString().substring(0, 2));
							}
						}
						
						
						timeMap.put("lessonTime" + i, lessonTime);
						timeMap.put("tdText" + i, "Class");
					}
					
					
					
					//System.out.println("######################################################################");
					//System.out.println(day + "   " + timeStamp);
					//System.out.println(classInfoList);
					timeMap.put("levelTestInfoList"+ i, levelTestInfoList);
					timeMap.put("classInfoList"+ i, classInfoList);
					timeMap.put("lessonTime" + i, 0);	// 수강 log 없는 td: default 0
					timeMap.put("tdText" + i, "");
					
					if(levelTestInfoList.size() > 0 && classInfoList.size() > 0) {						
						timeMap.put("tdText" + i, "Class & LevelTest");
					}

				}

				timeTableList.add(timeMap);
				

				int i = 0; // 초기값 설정
				while (i < 1) { // 조건식
					timeMap = new HashMap<String, Object>();

					start_min += 10;

					if (start_min == 60) {
						start_min = 0; // 분 0
						start_hour += 1; // 시간 +1시간
					}

					time = String.format("%02d", start_hour) + String.format("%02d", start_min);
					timeStamp = String.format("%02d", start_hour) + ":" + String.format("%02d", start_min);

					timeMap.put("time", timeStamp);
					for (int k = 0; k <= 6; k++) {
						timeMap.put("d" + k, k + "_" + time);

						checkCnt = 0; // 배정 가능시간과 근무시간과 동일하면 cnt++
						for (String ableTime : timeTable) {
							// System.out.println(ableTime);
							if (ableTime.equals(k + "_" + time)) {
								checkCnt++;
							}
						}
						// timeMap.put("isCheck" + j, checkCnt);
						timeMap.put("state" + k, (checkCnt == 1 ? "possible" : ""));

						/************************ 레벨테스트 정보 가져와서 시간표와 비교 *************************/
						String day = weeklyDays.get("day" + k).toString();
						timeMap.put("lessonTime" + k, 0);	// 초기값
						timeMap.put("tdText" + k, "");		// 초기값
						

						// teacher_no, 날짜, 시간으로 class_log 정보 가져온다.
						classParam = new HashMap<String, Object>();
						classParam.put("user_teacher_no", teacher.get("user_teacher_no"));
						classParam.put("study_date", day);
						classParam.put("class_time", timeStamp);					
						
						/*	0712 */
						// 레벨테스트 정보 추가
						levelTestInfoList = adminMapper.getLevelTestInfoList(classParam);
						timeMap.put("levelTestInfoList"+k, levelTestInfoList);
						
						/************************ 레벨테스트 정보 가져와서 시간표와 비교 *************************/
						String levelStudentInfoListString = "";	// user_student_no,user_name#user_student_no,user_name... 

						if(levelTestInfoList.size() > 0) {
							timeMap.put("state" + k, "LevelTest");						
							int lessonTime = Integer.parseInt(levelTestInfoList.get(0).get("lesson_time").toString().substring(0, 2));
							
							for(int x=0; x<levelTestInfoList.size(); x++) {
								List<Map<String, Object>> levelTestStudentList = adminMapper.getLevelTestStudentList(levelTestInfoList.get(x));
								
								// 해당 class_no를 수강하고있는 학생번호,학생명 List
								for(int z=0; z < levelTestStudentList.size(); z++) {
									levelStudentInfoListString += levelTestStudentList.get(z).get("user_student_no").toString()+","+levelTestStudentList.get(z).get("student_name").toString()+","+levelTestStudentList.get(z).get("id").toString();
									
									if((z + 1) != levelTestStudentList.size()) {
										levelStudentInfoListString += "#";
									}
								}
								
								levelTestInfoList.get(x).put("levelStudentInfoListString", levelStudentInfoListString);
							}
							
							timeMap.put("lessonTime" + k, lessonTime);
							timeMap.put("tdText" + k, "LevelTest");
						}
						
						
						/************************ 유료수강 정보 가져와서 시간표와 비교 *************************/						
						classInfoList = adminMapper.getClassInfoList(classParam);
						String studentInfoListString = "";	// user_student_no,user_name#user_student_no,user_name... 

						if(classInfoList.size() > 0) {
							timeMap.put("state" + k, "inClass");						
							int lessonTime = Integer.parseInt(classInfoList.get(0).get("lesson_time").toString().substring(0, 2));
							
							for(int x=0; x<classInfoList.size(); x++) {
								List<Map<String, Object>> classStudentList = adminMapper.getClassStudentList(classInfoList.get(x));
								
								// 해당 class_no를 수강하고있는 학생번호,학생명 List
								for(int z=0; z < classStudentList.size(); z++) {
									studentInfoListString += classStudentList.get(z).get("user_student_no").toString()+","+classStudentList.get(z).get("student_name").toString()+","+classStudentList.get(z).get("id").toString();
									
									if((z + 1) != classStudentList.size()) {
										studentInfoListString += "#";
									}
								}
								classInfoList.get(x).put("studentInfoListString", studentInfoListString);
								
								
								if(lessonTime < Integer.parseInt(classInfoList.get(x).get("lesson_time").toString().substring(0, 2))) {
									lessonTime = Integer.parseInt(classInfoList.get(x).get("lesson_time").toString().substring(0, 2));
								}
							}
							
							timeMap.put("lessonTime" + k, lessonTime);
							timeMap.put("tdText" + k, "Class");
						}
						
						timeMap.put("classInfoList"+k, classInfoList);								
						timeMap.put("levelTestInfoList"+ k, levelTestInfoList);
						
						if(levelTestInfoList.size() > 0 && classInfoList.size() > 0) {						
							timeMap.put("tdText" + k, "Class & LevelTest");
						}
						
						
						/****************************************************************************/
					}
					timeTableList.add(timeMap);		
					

					if (start_hour == end_hour && start_min == end_min) { // 배정 시작시간과 종료시간이 같아졌다면 while문 빠져나감
						i++; // 증감식
					}

				} // timeTableList.add  While문 END

			}
			// System.out.println(timeTableList);			
			mav.addObject("timeTableList", timeTableList);
			
			System.out.println("22222222222222222 반복중 timeTableList ★★★★★★★★★★★★★★★★★★★★★★★★★");
			System.out.println(timeTableList);

		}

		//////////////////////////////////////////////////////////////////////////

		mav.addObject("weeklyDays", weeklyDays);
		mav.addObject("cri", cri);

		return mav;
	}
	
	// 슈퍼관리자 - Teacher's Page Online book - 온라인교재관리 Online book과 기능 동일
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/tPage/onlineBook")
	public ModelAndView admin_teacherPage_onlineBook(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_teachersPage_onlineBook");		

		if(!paramMap.containsKey("study_language")) {
			paramMap.put("study_language", "English");
		}
		paramMap.put("study_status", 1);

		List<Map<String, Object>> study = adminMapper.selectStudyLang(paramMap);
		mav.addObject("study", study);
		if(!paramMap.containsKey("study_no")) {
			if(study.size() > 0) {
				paramMap.put("study_no", study.get(0).get("study_no"));
			}
		}
		
		List<Map<String, Object>> textbook = adminMapper.selectTextbookByStudy(paramMap);
		mav.addObject("textbook", textbook);
		if(!paramMap.containsKey("textbook_no")) {
			if(textbook.size() > 0) {
				paramMap.put("textbook_no", textbook.get(0).get("textbook_no"));
			}
		}
		
		List<Map<String, Object>> textbookTopic = adminMapper.selectTextbookTopic(paramMap);
		mav.addObject("textbookTopic", textbookTopic);
		if(!paramMap.containsKey("textbook_topic_no")) {
			if(textbookTopic.size() > 0) {
				paramMap.put("textbook_topic_no", textbookTopic.get(0).get("textbook_topic_no"));
			}
		}
		
		if(paramMap.containsKey("textbook_topic_no")) {
			mav.addObject("pdf_file", adminMapper.selectTextbookTopicOne(paramMap).get("pdf_file"));
		}
		
		mav.addObject("language", paramMap.get("study_language"));
		mav.addObject("study_no", paramMap.get("study_no"));
		mav.addObject("textbook_no", paramMap.get("textbook_no"));
		mav.addObject("textbook_topic_no", paramMap.get("textbook_topic_no"));
		
		return mav;
	}
	
	
	// ############## 공용 ##################
	
	// check 선택된 회원 sms보내기 팝업 open
	@RequestMapping("/sms/openPopup")
	public ModelAndView sms_popupOpen(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/smsPopup");
		
		String[] userNoArr = paramMap.get("selectedArr").toString().split(",");
   		
   		List<String> listArr = new ArrayList<String>();
   		
   		for(int i=0; i<userNoArr.length; i++) {   			
   			listArr.add(userNoArr[i]);
   		}
   		
   		// HashSet 중복 제거 (수신자 중복 제거)
   		HashSet<String> arr2 = new HashSet<String>(listArr);
        List<String> resArr = new ArrayList<String>(arr2);		// 중복 제거 후 user_student_no List
		
		mav.addObject("paramMap", paramMap);
		mav.addObject("size", resArr.size()); 		// 중복 제거한 수신자 몇명인지 SMS 팝업에 표시
		
		
		// 시간 콤보박스 (hour)
		String[] hourList = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
				"15", "16", "17", "18", "19", "20", "21", "22", "23" };
		mav.addObject("hourList", hourList);

		String[] minList = { "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55" };
		mav.addObject("minList", minList);
		
		return mav;
	}
	
	// 슈퍼관리자 - 회원상세보기 팝업 문자내역
	@RequestMapping("/smsLog")
	public ModelAndView smsLog(@RequestParam Map<String, Object> paramMap, ClassDetailCriteria cri) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_smsLog");
		
		Map<String, Object> student = adminMapper.getUserByStudentNo(paramMap);
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
		mav.addObject("user", student);				
		
		adminMapper.setRownum();
		
		paramMap.put("cri", cri);	
		
		int smsLogCnt = adminMapper.countSmsLogByStudentNo(paramMap);
		List<Map<String, Object>> smsLogList = adminMapper.selectSmsLogByStudentNo(paramMap);		// 해당 학생 문자내역 조회	
		
		ClassDetailPageMaker pageMaker = new ClassDetailPageMaker(cri, smsLogCnt);

		pageMaker.setCri(cri);
		pageMaker.setTotalCount(smsLogCnt);

		mav.addObject("paramMap", paramMap);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("cri", cri);
		mav.addObject("smsLogList", smsLogList);
		mav.addObject("smsLogCnt", smsLogCnt);
		
		return mav;
	}
	
	
	// Report 팝업
	@RequestMapping("/reportPopup")
	public ModelAndView reportPopup(@RequestParam Map<String, Object> paramMap, ClassDetailCriteria cri) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/reportPopup");
		
		//System.out.println(paramMap);
		mav.addObject("paramMap", paramMap);
		
		adminMapper.setRownum();		
		paramMap.put("cri", cri);
	
		mav.addObject("flag", paramMap.get("flag").toString());
		
		
		if ("class".equals(paramMap.get("flag").toString())) {
			paramMap.put("class_score_no", paramMap.get("no"));
			
			//System.out.println(paramMap);
			Map<String, Object> classInfo = adminMapper.getReportClassInfo(paramMap);		// param: class_score_no	
			mav.addObject("classInfo", classInfo);
			mav.addObject("running_time", classInfo.get("product_running_time"));			
			
			// Last Book
			paramMap.put("class_no", classInfo.get("class_no"));
			paramMap.put("study_date", classInfo.get("study_date"));
			
			Map<String, Object> lastBook = new HashMap<String, Object>();
			lastBook = adminMapper.getLastBook(paramMap);				// param: class_no, study_date
					
			if(MapUtils.isEmpty(lastBook)) {
				lastBook = new HashMap<String, Object>();
				lastBook.put("textbook_name", "");
				lastBook.put("topic_name", "");				
				lastBook.put("textbook_no", 0);
				lastBook.put("textbook_topic_no", 0);
				lastBook.put("slide_number", "");
				lastBook.put("pdf_file", "");
			}			
			
			mav.addObject("lastBook", lastBook);
			
			// Today Book 
			// textbook combo - 해당 수업의 교육과정 - Junior, Senior, I Can Speak, Free talking.. 분류에 있는 교재리스트만 콤보에 나옴
			Map<String, Object> textbookParam = new HashMap<String, Object>();
			textbookParam.put("study_no", classInfo.get("study_no"));			
			mav.addObject("textbookList", adminMapper.selectTextbookCombo(textbookParam));		// param : study_no
			
			//textbookParam.put("textbook_no", classInfo.get("textbook_no"));
			Map<String, Object> todayBook = adminMapper.getTodayBook(paramMap);
			
			// open시 book 셀렉트
			int today_textbook_topic_no = Integer.parseInt(todayBook.get("textbook_topic_no").toString());
			
			if(today_textbook_topic_no != 0) {
				textbookParam.put("textbook_no", todayBook.get("textbook_no"));
				textbookParam.put("textbook_topic_no", today_textbook_topic_no);
				textbookParam.put("pdf_file", todayBook.get("pdf_file"));
			} else {
				if (Integer.parseInt(lastBook.get("textbook_no").toString()) != 0) {
					textbookParam.put("textbook_no", lastBook.get("textbook_no"));
					textbookParam.put("textbook_topic_no", lastBook.get("textbook_topic_no"));
					textbookParam.put("pdf_file", lastBook.get("pdf_file"));
				} else {
					textbookParam.put("textbook_no", classInfo.get("textbook_no"));
					textbookParam.put("textbook_topic_no", 0);
					textbookParam.put("pdf_file", "");
				}
			}			
			
			mav.addObject("textbookTopicList", adminMapper.selectTextbookTopicCombo(textbookParam));		// param : textbook_no
			mav.addObject("textbookParam", textbookParam);
			
			// Student List info
			// classInfo.get("study_date");
			paramMap.put("study_date", classInfo.get("study_date"));			
			
			List<Map<String, Object>> studentList = new ArrayList<Map<String, Object>>();
			studentList = adminMapper.selectStudentInfoAll(paramMap);		// param: class_no, study_date	
			
			mav.addObject("studentList", studentList);
			
		} 		
		else if ("leveltest".equals(paramMap.get("flag").toString())) {
			Map<String, Object> levelLogInfo = adminMapper.getLevelLogInfo(paramMap);
			
			mav.addObject("info", levelLogInfo);
			mav.addObject("running_time", levelLogInfo.get("running_time"));
		}
		
		return mav;
	}
		
	// 정규 보충 수업
	@RequestMapping("/userDetail/makeUpClass")
	public ModelAndView makeUpClass(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_makeUpClass");
		
		Map<String, Object> classInfo = adminMapper.selectClassOne(paramMap);
		if(classInfo.containsKey("user_teacher_no")) {
			mav.addObject("teacher", adminMapper.selectTeacherByNo(classInfo));
		}
		
		Map<String, Object> textbookInfo = adminMapper.selectTextbookInfo(paramMap);
		mav.addObject("textbook", textbookInfo);
		mav.addObject("textbook_topic", adminMapper.selectTextbookTopic(textbookInfo));
		
		mav.addObject("student", adminMapper.selectStudentOne(paramMap));
		mav.addObject("center", adminMapper.selectCenterTutor());
		mav.addObject("group", adminMapper.selectGroupTutor());
		mav.addObject("paramMap", paramMap);
		
		return mav;
	}
	
	// 학습현황 진도 변경
	@RequestMapping("/userDetail/changeProgress")
	public ModelAndView changeProgress(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_changeProgress");
		
		Map<String, Object> study = adminMapper.getStudyByClass(paramMap);
		mav.addObject("study", study);
		mav.addObject("textbook", adminMapper.selectTextbookByStudy(study));
		mav.addObject("paramMap", paramMap);
		
		return mav;
	}
	
	// 정규수업 수정
	@RequestMapping("/userDetail/modifyClass")
	public ModelAndView modifyClass(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_modifyClass");
		
		Map<String, Object> classInfo = adminMapper.selectClassOne(paramMap);
		if(classInfo.containsKey("user_teacher_no")) {
			mav.addObject("teacher", adminMapper.selectTeacherByNo(classInfo));
		}
		
		Map<String, Object> textbookInfo = adminMapper.selectTextbookInfo(paramMap);
		mav.addObject("textbook", textbookInfo);
		mav.addObject("textbook_topic", adminMapper.selectTextbookTopic(textbookInfo));
		
		mav.addObject("student", adminMapper.selectStudentOne(paramMap));
		mav.addObject("center", adminMapper.selectCenterTutor());
		mav.addObject("group", adminMapper.selectGroupTutor());
		mav.addObject("paramMap", paramMap);
		
		return mav;
	}
	
	// 레벨테스트 수정
	@RequestMapping("/userDetail/modifyLeveltest")
	public ModelAndView modifyLeveltest(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/userDetail_modifyLeveltest");
		
		System.out.println("@@@@@@@@@@@@@");
		System.out.println(paramMap);

		Map<String, Object> leveltest = adminMapper.selectLeveltestTutor(paramMap);
		if(leveltest.containsKey("user_teacher_no")) {
			mav.addObject("teacher", adminMapper.selectTeacherByNo(leveltest));
		}
		
		mav.addObject("leveltest", leveltest);
		mav.addObject("student", adminMapper.selectStudentOne(paramMap));
		mav.addObject("center", adminMapper.selectCenterTutor());
		mav.addObject("group", adminMapper.selectGroupTutor());
		mav.addObject("paramMap", paramMap);
		
		return mav;
	}
	
	// 슈퍼관리자 회원상세보기 팝업 - 특이사항 상세보기 팝업
	@RequestMapping("/userInfo/ectPopup")
	public ModelAndView admin_etcPopup(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/user_etcPopup");		
		Map<String, Object> user = adminMapper.getUserByEtcPopup(paramMap);		
		mav.addObject("user", user);		
		return mav;
	}	
	
	
	// 강사, 관리자 사용 
	
	// Report 팝업 - report 탭
	@RequestMapping("/reportPopup/report")
	public ModelAndView reportPopup_report(@ModelAttribute Criteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/reportBottom_report");		
		mav.addObject("flag", paramMap.get("flag").toString());
		
		
		if("class".equals(paramMap.get("flag").toString())) {
			mav.addObject("classScoreList", adminMapper.selectClassScoreCombo(paramMap));	// report탭 해당 수업 학생 셀렉트박스
			mav.addObject("class_score_no", paramMap.get("class_score_no"));				// 어떤 학생에대한 report 정보를 가져올건지 (셀렉트박스에 선택되어있는 학생)
			
			Map<String, Object> reportInfo = adminMapper.selectClassScoreOne(paramMap);
			
			mav.addObject("reportInfo", reportInfo);
			mav.addObject("class_no", reportInfo.get("class_no"));
			mav.addObject("class_student_no", reportInfo.get("class_student_no"));
			mav.addObject("class_log_no", reportInfo.get("class_log_no"));
		} else {		
			Map<String, Object> reportInfo = adminMapper.selectLevelTestOne(paramMap);	// leveltest_no
			mav.addObject("reportInfo", reportInfo);
			mav.addObject("leveltest_no", reportInfo.get("leveltest_no"));
						
			// result course - study 콤보
			paramMap.put("language", reportInfo.get("leveltest_language"));
			mav.addObject("studyList", adminMapper.selectStudyComboByLanguage(paramMap));
			
			// result level - study_level 콤보
			paramMap.put("study_no", reportInfo.get("study_no"));			
			mav.addObject("studyLevelList", adminMapper.selectStudyLevelCombo(paramMap));
		}
		
		
		
		return mav;
	}
	
	// Report 팝업 - history 탭
	@RequestMapping("/reportPopup/history")
	public ModelAndView reportPopup_history(@ModelAttribute HistoryCriteria cri, @RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/reportBottom_history");
		
		mav.addObject("flag", paramMap.get("flag").toString());
		adminMapper.setRownum();
		
		//System.out.println(paramMap);		
		paramMap.put("cri", cri);
		
		List<Map<String, Object>> historyList = new ArrayList<Map<String, Object>>();
		int count;
		
		if("class".equals(paramMap.get("flag").toString())) {	// class 수업내용을 클릭해서 report 팝업이 열렸다면
			mav.addObject("classStudentList", adminMapper.selectClassStudentCombo(paramMap));
			mav.addObject("class_no", paramMap.get("class_no"));
			mav.addObject("class_student_no", paramMap.get("class_student_no"));
			mav.addObject("class_score_no", paramMap.get("class_score_no"));	// 다시 report 탭으로 넘어갈 때 넘겨준다. 어떤 class_score_no로 클릭해서 들어온건지
						
			historyList = adminMapper.selectClassHistoryAll(paramMap);			// class에 대한 history를 불러온다.
			count = adminMapper.countClassHistoryAll(paramMap);
			
		} else {	// leveltest 수업내용을 클릭해서 report 팝업이 열렸다면 
			mav.addObject("leveltest_no", paramMap.get("leveltest_no"));
			Map<String, Object> leveltestInfo = adminMapper.selectLevelTestOne(paramMap);			
			mav.addObject("leveltestInfo", leveltestInfo);
			
			paramMap.put("user_student_no", leveltestInfo.get("user_student_no"));		// 해당 학생 leveltest history 조회
			
			historyList = adminMapper.selectLevelTestHistoryAll(paramMap);				// leveltest에 대한 history를 불러온다.
			count = adminMapper.countLevelTestHistoryAll(paramMap);
			
		}
		
		
		// 점수 평균
		for(Map<String, Object> h : historyList) {
			if(h.containsKey("grammer")) {
				int grammer = Integer.parseInt(h.get("grammer").toString());
				int speaking = Integer.parseInt(h.get("speaking_fluency").toString());
				int listening = Integer.parseInt(h.get("listening_comprehension").toString());
				int pronunciation = Integer.parseInt(h.get("pronunciation_intonation").toString());
				int vocabulary = Integer.parseInt(h.get("vocabulary").toString());
				
				double avg = (grammer + speaking + listening + pronunciation + vocabulary) / 5.0;
				String avgResult = String.format("%.1f", avg);		// 소수점 첫째 자리까지만			
				
				h.put("avg", avgResult);				
			}
		}
		
		mav.addObject("historyList", historyList);
		
		
		HistoryPageMaker pageMaker = new HistoryPageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("historyCnt", count);
		mav.addObject("cri", cri);		
		
		return mav;
	}	
	
	// 슈퍼관리자 - 수업관리 수업날짜일괄변경
	@Auth(role= Role.ROLE_ADMIN)
	@RequestMapping("/admin/modifyClassDate")
	public ModelAndView admin_changeClassDate(@ModelAttribute ModifyClassDateCriteria cri, @RequestParam Map<String, Object> paramMap) throws ParseException{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/admin/admin_modifyClassDate");		
		mav.addObject("main_title", "수업관리");
		mav.addObject("sub_title", "수업날짜 일괄변경");		
		adminMapper.setRownum();	
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		
		// 최초 페이지 로드 시 검색조건 Setting
		if(paramMap.get("searchDate") == null) {			
			cri.setSearchDate(today);					
			paramMap.put("searchDate", today);
		}		
		
		paramMap.put("cri", cri);
		mav.addObject("cri", cri);
		
		// 검색조건 - 콤보 리스트 (사용 상태)
		mav.addObject("siteList", adminMapper.getSiteCombo());					// 사이트 검색 콤보
		mav.addObject("centerList", adminMapper.getCenterCombo());				// 티칭센터 검색 콤보
		mav.addObject("studyList", adminMapper.getStudyCombo(paramMap));		// 교육과정 - 언어 검색셀렉트에 따라 where절 추가
		mav.addObject("textbookList", adminMapper.getTextbookCombo(paramMap));	// 학습교재 - 교육과정 검색셀렉트에 따라 where절 추가		
		mav.addObject("teacherList", adminMapper.getTeacherCombo(paramMap));	// 강사 - 언어, 티칭센터에 따라 where절 추가
				
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.selectClassDateAll(paramMap);
		
		mav.addObject("list", list);
		int count = adminMapper.countClassDateAll(paramMap);
		
		
		ModifyClassDatePageMaker pageMaker = new ModifyClassDatePageMaker(cri, count);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(count);
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("classDateCnt", count);
		
		
		return mav;
	}
	
	
	
}
