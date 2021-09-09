package com.edutem.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.edutem.lms.interceptor.Auth;
import com.edutem.lms.interceptor.Auth.Role;
import com.edutem.lms.mapper.AdminMapper;
import com.edutem.lms.mapper.TeacherMapper;
import com.edutem.lms.paging.ScheduleCriteria;
import com.edutem.lms.paging.SchedulePageMaker;
import com.edutem.lms.paging.TimetableCriteria;
import com.edutem.lms.paging.TodayScheduleCriteria;
import com.edutem.lms.paging.TodaySchedulePageMaker;

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

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.MapUtils;


@Controller
public class SaehaLmsTeacherController {
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private TeacherMapper teacherMapper;
	
	
	// 강사 - Today Schedule
	@Auth(role= Role.ROLE_TEACHER)
	@RequestMapping("/teacher/todaySchedule")
	public ModelAndView teacher_todaySchedule(@RequestParam Map<String, Object> paramMap, TodayScheduleCriteria cri,
			HttpSession session) throws ParseException {	
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/teacher/teacher_todaySchedule");

		adminMapper.setRownum();

		mav.addObject("main_title", "Teacher's Page");
		mav.addObject("sub_title", "Today Schedule");		
		paramMap.put("cri", cri);
		
		if("".equals(cri.getSearchStartHour())) cri.setSearchStartHour("06"); 		// 최초 페이지 로드 시 검색 time 시작시간 06시 Setting 
		if("".equals(cri.getSearchEndHour())) cri.setSearchEndHour("24");			// 최초 페이지 로드 시 검색 time 종료시간 24시 Setting
		
		String[] hourList = { "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};				
		mav.addObject("hourList", hourList);
		
		
		// 로그인 user 정보 - user_teahcher_no 가져오기
		String user_no = session.getAttribute("login_no").toString();
		Map<String, Object> user = adminMapper.getUserByLoginNo(user_no);
		
		//System.out.println(user.get("user_teacher_no"));
		paramMap.put("user_teacher_no", user.get("user_teacher_no"));
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		if (!paramMap.containsKey("searchDate") || "".equals(paramMap.get("searchDate").toString())) {	// 검색일 최초 페이지 로드 시 오늘날짜로 Setting
			// 파라미터로 넘어오도록
			paramMap.put("searchDate", today);		// 기준일
			cri.setSearchDate(paramMap.get("searchDate").toString());
		}
		mav.addObject("searchDate", paramMap.get("searchDate"));
		
		int classCnt = teacherMapper.countTodaySchedule(paramMap);
		List<Map<String, Object>> classList = teacherMapper.selectTodaySchedule(paramMap);
			
		
		// product_type이 video가 들어간것들만 classVideo버튼 나오도록 btnFlag 추가
		for(Map<String, Object> map : classList) {			
			//System.out.println(map.get("product_type").toString().toLowerCase());			
			if (map.get("product_type").toString().toLowerCase().contains("video")){
				map.put("classVideoFlag", "1");
			} else {
				map.put("classVideoFlag", "0");
			}
		}
		/////////////////////////////////////////////////////////////////
		
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
				
				if(class_end_min >= 60) {		// 수업시작시간(분) + 런닝타임 시간(분) 더한게 60이상 이라면
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
		
		
		//System.out.println(removeIdxList);
		int idx = 0;
		if(classList.size() > 0 && removeIdxList.size() > 0) {
			for(int i=removeIdxList.size() - 1; i>=0; i--) {
				idx = removeIdxList.get(i);
				classList.remove(idx);
			}
		}
		
		
		//System.out.println(classList);
		
		
		TodaySchedulePageMaker pageMaker = new TodaySchedulePageMaker(cri, classCnt);

		pageMaker.setCri(cri);
		pageMaker.setTotalCount(classCnt);
		
		mav.addObject("pageMaker", pageMaker);
		mav.addObject("cri", cri);
		mav.addObject("classList", classList);
		mav.addObject("classCnt", classCnt);
		
		
		return mav;
	}
	
	
	
	
	
	// Teachers'Page  Weekly Schedule -> 선생님별 주간수업일정과 동일
	@Auth(role= Role.ROLE_TEACHER)
	@RequestMapping("/teacher/weeklySchedule")
	public ModelAndView teacher_weeklySchedule(@RequestParam Map<String, Object> paramMap, ScheduleCriteria cri,
			HttpSession session) throws ParseException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/teacher/teacher_weeklySchedule");

		mav.addObject("main_title", "Teacher's Page");
		mav.addObject("sub_title", "Weekly Schedule");
		paramMap.put("cri", cri);

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
		
		
		String user_no = session.getAttribute("login_no").toString();
	   	Map<String, Object> user = adminMapper.getUserByLoginNo(user_no);
	   	paramMap.put("user_teacher_no", user.get("user_teacher_no"));
	   	
	   	mav.addObject("flag", "selected"); // 강사 선택됨
	   	
	   	Map<String, Object> teacher = adminMapper.selectTeacherOne(paramMap); // param: user_teacher_no (teacher_no)
	   	mav.addObject("teacher", teacher);

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
		

		//////////////////////////////////////////////////////////////////////////

		mav.addObject("weeklyDays", weeklyDays);
		mav.addObject("cri", cri);

		return mav;
	}
	
	// Teacher's Page OnlineBook - 온라인교재관리 Online book과 기능 동일
	@Auth(role= Role.ROLE_TEACHER)
	@RequestMapping("/teacher/onlineBook")
	public ModelAndView teacher_teacherPage_onlineBook(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/teacher/teacher_onlineBook");		

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
	
	
	// 강사로그인 - 유저상세보기 (userDetail 학습현황)
	@Auth(role= Role.ROLE_TEACHER)
	@RequestMapping("/teacher/userDetail")
	public ModelAndView classLogg(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/teacher/teacher_userDetail");		
		
		//System.out.println("@@@@@@@@@@@@ teacher - userDetail_classLog");
		//System.out.println(paramMap);

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
		
		String flag = paramMap.get("flag").toString().toLowerCase();
		
		if(flag.equals("class")) {
			mav.addObject("info", adminMapper.selectClassTutor(paramMap));
			
			paramMap.put("study_status", 0);
			mav.addObject("class_log", adminMapper.selectClassLogStudent(paramMap));
			paramMap.put("study_status", 1);
			mav.addObject("class_log_done", adminMapper.selectClassLogStudent(paramMap));
			
			paramMap.put("class_no", paramMap.get("no"));
		}
		else if(flag.equals("leveltest")) {
			
			mav.addObject("info", adminMapper.selectLeveltestTutor(paramMap));
			System.out.println(adminMapper.selectLeveltestTutor(paramMap));
			
			paramMap.put("study_status", 0);
			mav.addObject("class_log", adminMapper.selectLeveltestStudent(paramMap));
			
			paramMap.put("study_status", 1);
			mav.addObject("class_log_done", adminMapper.selectLeveltestStudent(paramMap));
		}
		
		mav.addObject("paramMap", paramMap);
		
		return mav;
	}
	
	
	
	// ############## 공용 ##################
	// TodaySchedule 파일 버튼 클릭 시 (재생목록 팝업 오픈)
	@RequestMapping("/teacher/todaySchedule/fileList")
	public ModelAndView admin_teacher_timetable(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/teacher/teacher_todaySchedule_fileList");
		
		//System.out.println(paramMap);		
		
		String study_date = paramMap.get("study_date").toString();
		
		// BBB xml에서 받아온 url,recordId split 파라미터
		String urlSplitString = paramMap.get("urlSplitString").toString();
		String recordIdSplitString = paramMap.get("recordIdSplitString").toString();
		
		List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
		Map<String, Object> recordMap = new HashMap<String, Object>();
		
		if(!"".equals(urlSplitString)) {			
			String[] urlSplit = urlSplitString.split("@@");
			String[] recordIdSplit = recordIdSplitString.split("@@");
			
			// split으로 받아온 record를 List 객체에 담는다.
			for(int i=0; i<urlSplit.length; i++) {
				recordMap = new HashMap<String, Object>();
				recordMap.put("url", urlSplit[i]);
				recordMap.put("recordID", recordIdSplit[i]);
				
				recordList.add(recordMap);
			}
		}
		
		mav.addObject("study_date", study_date);
		mav.addObject("flag", paramMap.get("flag"));
		mav.addObject("no", paramMap.get("no"));
		mav.addObject("recordList", recordList);
		
		return mav;
	}
	
	
	// Today Schedule - videoLog 버튼 팝업	
	@RequestMapping("/teacher/todaySchedule/videoLogList")
	public ModelAndView teacher_todaySchedule_videoLogList(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/teacher/teacher_todaySchedule_videoLogList");
		
		String flag = paramMap.get("flag").toString();
		Map<String, Object> classInfo = new HashMap<String, Object>();
		
		
		if("class".equals(flag)) {
			paramMap.put("class_score_no", paramMap.get("no"));
			
			classInfo = teacherMapper.getVideoClassInfo(paramMap);	// param: class_score_no
			paramMap.put("class_log_no", classInfo.get("class_log_no"));
			
			List<Map<String, Object>> classVideoLogList = teacherMapper.selectClassVideoLogList(paramMap);		// 해당 수업 강사, 학생 입장/퇴장 log	param : class_log_no
			
			mav.addObject("classVideoLogList", classVideoLogList);
		} else {
			paramMap.put("leveltest_no", paramMap.get("no"));
			
			classInfo = teacherMapper.getVideoClassLevelInfo(paramMap);	// param: class_score_no
			List<Map<String, Object>> classVideoLogList = teacherMapper.selectLevelVideoLogList(paramMap);		// 해당 수업 강사, 학생 입장/퇴장 log	param : leveltest_no
			
			mav.addObject("classVideoLogList", classVideoLogList);
		}
		
		
		mav.addObject("room_no", classInfo.get("room_no"));
		mav.addObject("study_date", classInfo.get("study_date"));
		
		
		return mav;
	}
	
}
