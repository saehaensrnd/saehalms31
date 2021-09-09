package com.edutem.lms.controller.rest;

import java.io.IOException;
import java.net.MalformedURLException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.edutem.lms.component.S3Wrapper;
import com.edutem.lms.mapper.AdminMapper;
import com.edutem.lms.mapper.StudentMapper;

@RestController
public class SaehaLmsStudentRestController {
	
	@Autowired
	private StudentMapper studentMapper;
	@Autowired
	private AdminMapper adminMapper;
	@Autowired
	private S3Wrapper s3Wrapper;
	
	// myClass(마이노트 학습현황) - 수업입장 버튼 클릭시 비디오 학생로그 insert
	@RequestMapping("/student/myClass/videoStudentLogInsert")
   	public Map<String, Object> student_myClass_videoStudentLogInsert(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
   		paramMap.put("login_no", session.getAttribute("login_no").toString());		// 세션에 저장된 login_no(user_no)를 가져온다.   		    		
   		
   		studentMapper.insertClassVideoStudentLog(paramMap);   		   		
   		
   		// 해당 입장으로 생성된 PK를 가져온다 - 수업종료 후 팝업 닫힐 때 해당 log에 out_time update 하기위해
   		int class_video_student_log_no = studentMapper.selectLastAIClassVideoStudentLog() - 1;   		
   		paramMap.put("class_video_student_log_no", class_video_student_log_no);		
   		
   		return paramMap;
   	}
	
	// myClass(마이노트 학습현황) - videoClass 팝업 종료 시 비디오 학생로그 update (out_time)
	@RequestMapping("/student/myClass/videoStudentLogUpdate")
   	public void student_myClass_videoStudentLogUpdate(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
   		studentMapper.updateClassVideoStudentLog(paramMap);
   	}
	
	// 마이노트 휴강및보강신청 신청가능 휴강신청일 셀렉트박스
	@RequestMapping("/student/postpone/getHolidayComboByClass")
   	public List<Map<String, Object>> student_postpone_getHolidayComboByClass(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = studentMapper.getHolidayComboByClass(paramMap);		
   		return list;
   	}
	
	//휴강 보강신청 insert
	@RequestMapping("/student/postpone/insert")
   	public void student_postpone_insert(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
   		studentMapper.insertPostpone(paramMap);
   	}
	
	//휴강 보강신청 update
	@RequestMapping("/student/postpone/update")
   	public void student_postpone_update(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
		System.out.println(paramMap);
		
   		studentMapper.updatePostpone(paramMap);
   	}

	///////////////////////////////////////////////////////////// 수강신청
	@RequestMapping("/student/enrolment/langSel")
   	public Map<String, Object> langSel(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		paramMap.put("today", sdf.format(today));
		
		System.out.println("@@@@@@ langSel");
		System.out.println(paramMap);
		
		Map<String, Object> scheduleClassSite = studentMapper.selectScheduleClassSite(paramMap);
		resultMap.put("scheduleClassSite", scheduleClassSite);
		
		int study_not_used_site = studentMapper.countStudyNotUsedSite(paramMap);
		if(study_not_used_site > 0) {
			resultMap.put("study", studentMapper.selectStudyLanguageLIMIT(paramMap));
		}
		else {
			resultMap.put("study", studentMapper.selectStudyLanguage(paramMap));
		}
		
		return resultMap;
   	}
	
	@RequestMapping("/student/enrolment/textbookSelect")
	public int textbookSelect(@RequestParam Map<String, Object> paramMap) {
		return studentMapper.selectTextbookPriceOne(paramMap);
	}
	
	@RequestMapping("/student/enrolment/searchTeacher")
   	public Map<String, Object> searchTeacher(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		System.out.println(">>>>> seachTeacher >>>>>");
		System.out.println("parameter >>>");
		System.out.println(paramMap);
		System.out.println();
		
		List<Map<String, Object>> able_teachers = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> product = adminMapper.selectProductOne(paramMap);
		List<Map<String, Object>> public_holiday = adminMapper.selectHolidayByLang(product);

		ArrayList<String> holiday = new ArrayList<>();
		for (Map<String, Object> ph : public_holiday) {
			holiday.add(ph.get("holiday_date").toString());
		}
		Collections.sort(holiday); // holiday = 공휴일 ArrayList

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(paramMap.get("start_date").toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			String study_weeks = product.get("product_week").toString().replace("sun", "0").replace("mon", "1")
					.replace("tue", "2").replace("wed", "3").replace("thu", "4").replace("fri", "5")
					.replace("sat", "6");

			int day_count = 0;
			int product_count = Integer.parseInt(product.get("product_count").toString());

			ArrayList<String> study_dates = new ArrayList<String>();

			while (day_count < product_count) {
				if (study_weeks.indexOf(String.valueOf(cal.get(Calendar.DAY_OF_WEEK) - 1)) != -1) {
					boolean isHoliday = false;
					for (String h : holiday) {
						if (h.equals(sdf.format(cal.getTime()))) {
							isHoliday = true;
						}
					}
					if (!isHoliday) {
						day_count++;
						study_dates.add(sdf.format(cal.getTime()));
					}
				}
				cal.add(Calendar.DATE, 1);
			}
			cal.add(Calendar.DATE, -1);
			Collections.sort(study_dates); // 수업일들
			
			System.out.println("수업일: ");
			System.out.println(study_dates);
			System.out.println();
			
			List<Map<String, Object>> teachers = studentMapper.selectTeacherLangAll(paramMap);
			for(Map<String, Object> teacher : teachers) {
				// 가능한 선생 찾기
				List<Map<String, Object>> teacher_holiday = adminMapper.getTeacherHoliday(teacher); // 조회하는 강사의 휴강일
				boolean isOverlap = false; // 겹치는지 여부
				for (String sd : study_dates) {
					for (Map<String, Object> th : teacher_holiday) {
						if (sd.equals(th.get("teacher_holiday_date").toString())) {
							isOverlap = true;
						}
					}
				}
				
				if(!isOverlap) {
					able_teachers.add(teacher);
				}
				
			}
			
			System.out.println("가능한 선생 목록 >>>");
			System.out.println(able_teachers);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		resultMap.put("able_teachers", able_teachers);
		resultMap.put("product", product);
		
		return resultMap;
		
   	}
	
	@RequestMapping("/student/enrolment/teacherSelect")
	public List<Map<String, Object>> teacherSelect(@RequestParam Map<String, Object> paramMap) {
		List<Map<String, Object>> able_times = new ArrayList<Map<String, Object>>();
		System.out.println(">>> teacherSelect");
		System.out.println(paramMap);
		System.out.println();
		
		Map<String, Object> product = adminMapper.selectProductOne(paramMap);
		List<Map<String, Object>> public_holiday = adminMapper.selectHolidayByLang(product);
		ArrayList<String> study_dates = new ArrayList<String>();

		ArrayList<String> holiday = new ArrayList<>();
		for (Map<String, Object> ph : public_holiday) {
			holiday.add(ph.get("holiday_date").toString());
		}
		Collections.sort(holiday); // holiday = 공휴일 ArrayList
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(paramMap.get("start_date").toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			String study_weeks = product.get("product_week").toString().replace("sun", "0").replace("mon", "1")
					.replace("tue", "2").replace("wed", "3").replace("thu", "4").replace("fri", "5")
					.replace("sat", "6");

			int day_count = 0;
			int product_count = Integer.parseInt(product.get("product_count").toString());

			while (day_count < product_count) {
				if (study_weeks.indexOf(String.valueOf(cal.get(Calendar.DAY_OF_WEEK) - 1)) != -1) {
					boolean isHoliday = false;
					for (String h : holiday) {
						if (h.equals(sdf.format(cal.getTime()))) {
							isHoliday = true;
						}
					}
					if (!isHoliday) {
						day_count++;
						study_dates.add(sdf.format(cal.getTime()));
					}
				}
				cal.add(Calendar.DATE, 1);
			}
			cal.add(Calendar.DATE, -1);
			Collections.sort(study_dates); // 수업일들
			
			System.out.println("수업일: ");
			System.out.println(study_dates);
			System.out.println();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("===================================================================");
		Map<String, Object> teacher = studentMapper.selectTeacherOne(paramMap);
		String[] teacher_study_time = teacher.get("teacher_study_time").toString().split("/");
		String teacher_s = teacher_study_time[0].substring(0, 2);
		String teacher_e = teacher_study_time[1].substring(0, 2);
		
		int start_time = Integer
				.parseInt(String.format("%02d", Integer.parseInt(teacher_s)) + "00");
		int end_time = Integer
				.parseInt(String.format("%02d", Integer.parseInt(teacher_e)) + "00");
		
		int running_time = Integer.parseInt(product.get("product_running_time").toString());
		if (running_time > 60) {
			running_time += 40;
		}

		String study_weeks = product.get("product_week").toString().replace("sun", "0").replace("mon", "1")
				.replace("tue", "2").replace("wed", "3").replace("thu", "4").replace("fri", "5")
				.replace("sat", "6");
		
		// ========================================================
		int shour = start_time / 100;
		int smin = start_time % 100;
		int ehour = end_time / 100;
		int emin = end_time % 100;

		int t_shour = 0;
		int t_smin = 0;

		int rhour = 0; // shour+running_time
		int rmin = 0; // smin+running_time

		boolean isok = true;
		while (!((shour * 60 + smin) > (ehour * 60 + emin))) {
			rmin = smin + running_time;
			rhour = shour;
			if (rmin >= 60) {
				rmin -= 60;
				rhour += 1;
			}

			String[] able_weeks = study_weeks.split("/");
			String s_time = "";
			isok = true;
			for (String at : able_weeks) {
				t_shour = shour;
				t_smin = smin;
				while (!((t_shour * 60 + t_smin) > (rhour * 60 + rmin))) {
					s_time = String.format("%02d", t_shour) + String.format("%02d", t_smin);
					paramMap.put("search_time", at + "_" + s_time);

					int isTime = adminMapper.countTeacherTimetable(paramMap);
					if (isTime <= 0) {
						isok = false;
					}

					t_smin += running_time;
					if (t_smin >= 60) {
						t_smin -= 60;
						t_shour += 1;
					}
				}
			}

			if (isok) {
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				System.out.println("가능\t" + shour + ":" + smin + " ~ " + rhour + ":" + rmin);
				tmpMap.put("start_time", addZero(shour) + addZero(smin));
				tmpMap.put("end_time", addZero(rhour) + addZero(rmin));
				able_times.add(tmpMap);
			}

			smin += running_time;
			if (smin >= 60) {
				smin -= 60;
				shour += 1;
			}
		}
		System.out.println(able_times);// 1차 가능한 시간대 (teacher_timetable 가능 시간대 비교)
		
		System.out.println("===================================================================");
		List<Map<String, Object>> classLog = adminMapper.selectClasslogTeacher(paramMap);

		System.out.println("**remove**************");
		ArrayList<Integer> removeData = new ArrayList<Integer>();
		for (Map<String, Object> cl : classLog) {
			for (String study_date : study_dates) {
				if (cl.get("study_date").toString().equals(study_date)) {
					for (int i = 0; i < able_times.size(); i++) {
						int class_time = Integer.parseInt(cl.get("start_time").toString().replace(":", ""));
						int class_end_time = class_time + Integer.parseInt(cl.get("product_running_time").toString());
						int stime = Integer.parseInt(able_times.get(i).get("start_time").toString());
						int etime = Integer.parseInt(able_times.get(i).get("end_time").toString());
//		   						System.out.println(stime+" <= "+class_time+" < "+etime+"\t"+stime+" < "+class_end_time+" <= "+etime);
						if ((stime <= class_time && etime > class_time) || (stime < class_end_time && etime >= class_end_time)) {
							removeData.add(i);
						}
					}
				}
			}
			System.out.println(removeData);
		}

		// removeData의 중복된 값 제거
		ArrayList<Integer> removeIndex = new ArrayList<Integer>();
		for (Integer d : removeData) {
			if (!removeIndex.contains(d)) {
				removeIndex.add(d);
			}
		}

		Collections.sort(removeIndex);
		Collections.reverse(removeIndex);

		System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
		System.out.println(removeIndex);
		System.out.println("**********************");

		for (int r : removeIndex) {
			able_times.remove(r);
		}

		System.out.println("== 최종 가능 시간대 ==");
		System.out.println(able_times);
		
		return able_times;
	}

	private String addZero(int num) {
		return (num < 10) ? "0" + num : "" + num;
	}

	@RequestMapping("/student/join/checkID")
	public boolean checkUserID(@RequestParam Map<String, Object> paramMap) {
		int cnt = adminMapper.userIDCheck(paramMap);
		return (cnt > 0) ? true : false;
	}
	
	@RequestMapping("/student/join/insertUser")
	public void insertUser(@RequestParam Map<String, Object> paramMap, MultipartFile profile) {
		if (!profile.isEmpty()) {
			paramMap.put("student_profile", paramMap.get("student_phone") + ".jpg");
			try {
				s3Wrapper.upload(profile, "user_student_profile", paramMap.get("student_phone") + ".jpg");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			paramMap.put("student_profile", "");
		}
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@ JOIN");
		System.out.println(paramMap);

		String student_address = "";
		if (paramMap.containsKey("zip_code")) {
			student_address = paramMap.get("zip_code") + "/" + paramMap.get("address") + "/"
					+ paramMap.get("address_detail");
		}
		paramMap.put("student_address", student_address);
		
		String pwd = paramMap.get("student_pwd").toString();
		String encrypted = BCrypt.hashpw(pwd, BCrypt.gensalt());
		paramMap.put("student_pwd", encrypted);

		adminMapper.insertUser(paramMap);
		int user_no = adminMapper.selectUserPK() - 1;
		paramMap.put("user_no", user_no);
		studentMapper.insertUserStudent(paramMap);
	}
	
	// 마이노트 내정보수정 수정하기 버튼 기능처리 User update
	@RequestMapping("/student/infoModify/updateUser")
	public void userUpdate(@RequestParam Map<String, Object> paramMap, MultipartFile profile) {
		Map<String, Object> student = studentMapper.getStudentInfo(paramMap);
		
		// 비밀번호 BCrypt 암호화 관련
		boolean pwChangedFlag = false;		
		
		if(!student.get("pwd").toString().equals(paramMap.get("student_pwd"))) {
			// 비밀번호를 수정했다면
			pwChangedFlag = true;
						
			String pwd = paramMap.get("student_pwd").toString();
			String encrypted = BCrypt.hashpw(pwd, BCrypt.gensalt());
			paramMap.put("student_pwd", encrypted);
		} 
		//////////////////////////////////////////////////////////////////////////
		
		if (!profile.isEmpty()) {
			paramMap.put("student_profile", paramMap.get("student_phone") + ".jpg");
			try {
				s3Wrapper.upload(profile, "user_student_profile", paramMap.get("student_phone") + ".jpg");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// 새로 입력한 profile 이미지가 없다면 기존 profile 유지
			paramMap.put("student_profile", student.get("student_profile"));
		}

		String student_address = "";
		if (paramMap.containsKey("zip_code")) {
			student_address = paramMap.get("zip_code") + "/" + paramMap.get("address") + "/"
					+ paramMap.get("address_detail");
		}
		paramMap.put("student_address", student_address);		
				
		if(pwChangedFlag) {
			studentMapper.updateUserByInfoModify(paramMap);	// (비밀번호가 수정됐다면 / user테이블은 비밀번호만 수정됨)
		}
		studentMapper.updateUserStudentByInfoModify(paramMap);
	}
	
	
	// 날짜 포함되는지 확인 후 boolean 반환
	public static boolean isWithinRange(String date, String startDate, String endDate) throws ParseException {
	    LocalDate localdate = LocalDate.parse(date);
	    LocalDate startLocalDate = LocalDate.parse(startDate);
	    LocalDate endLocalDate = LocalDate.parse(endDate);
	    endLocalDate = endLocalDate.plusDays(1); // endDate는 포함하지 않으므로 +1일을 해줘야함.
	    
	    return ( ! localdate.isBefore( startLocalDate ) ) && ( localdate.isBefore( endLocalDate ) );
	}
	
	@RequestMapping("/student/levelTest/leveltestApp/getTeacherCombo")
   	public List<Map<String, Object>> student_leveltestApp_getTeacherCombo(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = studentMapper.getTeacherComboByLanguageType(paramMap);
   		return list;
   	}
	
	// 학생 무료레벨테스트 신청
	@RequestMapping("/popup/leveltestApp/search")
	public Map<String, Object> leveltestApp_search(@RequestParam Map<String, Object> paramMap) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<Map<String, Object>> able_times = new ArrayList<Map<String, Object>>();
		
		
		// 해당 사이트의 강사그룹/티칭센터 강사이면서, leveltest만 하는 강사
		Map<String, Object> site = studentMapper.getSiteGroupNCenter(paramMap);
		site.put("leveltest_language", paramMap.get("leveltest_language"));		
		
		if(site.get("center_no").toString().equals("0") && site.get("teacher_group_no").toString().equals("0")) {
			System.out.println("해당 사이트에 연결되어있는 티칭센터/강사그룹이 없습니다.");
			resultMap.put("able_times", able_times);
			
		} else {			
			// 해당 강사그룹/티칭센터에 해당하는 강사들
			List<Map<String, Object>> siteTeachers = studentMapper.selectSiteTeachers(site);
						
			if(siteTeachers.size() == 0) { 
				resultMap.put("able_times", able_times);
				System.out.println("해당 티칭센터/강사그룹에 조건에 맞는 강사가 없습니다. (leveltest, 언어, 노출됨, 사용상태..)");
				return resultMap;				
			}			
			
			String[] time = paramMap.get("time").toString().split("~");
			paramMap.put("start_time", time[0]);
			paramMap.put("end_time", time[1]);
			
			List<Map<String, Object>> list_times = new ArrayList<Map<String,Object>>();
			
			
			
			// 조건에 맞는 강사들만큼 반복
			for(Map<String, Object> teacher : siteTeachers) {
				paramMap.put("user_teacher_no", teacher.get("user_teacher_no"));				
				
				// 강사마다 초기화
				able_times = new ArrayList<Map<String, Object>>();
				
				System.out.println(
						"==============================================================================================================================================");
				System.out.println(">>> 받아온 parameter");
				System.out.println(paramMap);
				// {user_student_no=7, student_phone=01064556867, site_no=1, start_date=2021-08-02, leveltest_type=Phone, class_phone=01064556867, leveltest_language=English, time=14~16}
		
				try {
		
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = sdf.parse(paramMap.get("start_date").toString());		// 날짜 따로 안받아오고 today 기준으로    new Date()
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					
					
					System.out.println(">>> 선택한 날짜");
					System.out.println(sdf.format(cal.getTime()));
		
					int day = cal.get(Calendar.DAY_OF_WEEK);
					day -= 1; // 해당 주차의 일요일로
					cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) - day);
		
					ArrayList<String> weeks = new ArrayList<String>();
					for (int i = 0; i < 7; i++) {
						weeks.add(sdf.format(cal.getTime()));
						cal.add(Calendar.DATE, 1);
					}
					Collections.sort(weeks);
		
					System.out.println();
					System.out.println("=== 해당 주차 ===");
					System.out.println(weeks);
					resultMap.put("weeks", weeks);			
							
		
					List<Map<String, Object>> holiday = adminMapper.selectHolidayByLang(paramMap);
					List<Map<String, Object>> t_holiday = adminMapper.getTeacherHoliday(paramMap);
					
					// 레벨테스트 신청일 날짜 리스트 가져오기 (levelAppDays)
					// 사이트별 수강신청 기수별 레벨테스트 신청기간을 우선적으로 보며, 없을 시 수업일정 기수별 레벨테스트 신청기간을 바라본다. 
					List<Map<String, Object>> levelAppDays = studentMapper.selectLevelAppDays(paramMap);	
					
					//System.out.println(paramMap);	// leveltest_language, site_no
					
					ArrayList<Integer> removeIndex = new ArrayList<Integer>();
					int removeChkCnt = 0;	// remove 일자 확인  레벨테스트 신청일에 포함되어있는 날짜가 아니라면 remove  (806)
					int containsDayCnt = 0;	
					
					for (String w : weeks) {
						removeChkCnt = 0;
						
						for (Map<String, Object> h : holiday) {
							if (w.equals(h.get("holiday_date").toString())) {
								cal.setTime(sdf.parse(w));
								removeChkCnt++;
								//removeIndex.add(cal.get(Calendar.DAY_OF_WEEK) - 1);
							}
						}
		
						for (Map<String, Object> th : t_holiday) {
							if (w.equals(th.get("teacher_holiday_date").toString())) {
								cal.setTime(sdf.parse(w));
								removeChkCnt++;
								//removeIndex.add(cal.get(Calendar.DAY_OF_WEEK) - 1);
							}
						}
						
						
						containsDayCnt = 0;					
						for (Map<String, Object> period : levelAppDays) {							
							String la_startDate = period.get("level_application_start_date").toString();				
							String la_endDate = period.get("level_application_end_date").toString();
							
							if(!la_startDate.equals("") && !la_endDate.equals("")) {
								//System.out.println(la_startDate +" ~ " + la_endDate);								
								// 포함되는지 확인	// 해당 날짜가 신청기간에 포함된다면 true
								if(isWithinRange(w, la_startDate, la_endDate)) {
									// levelAppDays 기간들중 신청기간에 포함되면 들어옴
									containsDayCnt++;									
								}								
							}							
						}
						
						// 신청기간에 포함 안되었다면 0
						if(containsDayCnt <= 0) {							
							removeChkCnt++;
						}						
						
						// 휴강일?, 강사휴강일?, 레벨테스트신청기간 미포함? 체크 후 0이상이면 remove index add
						if(removeChkCnt > 0) {
							cal.setTime(sdf.parse(w));
							removeIndex.add(cal.get(Calendar.DAY_OF_WEEK) - 1);
						}
						
						
						
						
						/* 기존
						for (Map<String, Object> h : holiday) {
							if (w.equals(h.get("holiday_date").toString())) {
								cal.setTime(sdf.parse(w));
								removeIndex.add(cal.get(Calendar.DAY_OF_WEEK) - 1);
							}
						}
		
						for (Map<String, Object> th : t_holiday) {
							if (w.equals(th.get("teacher_holiday_date").toString())) {
								cal.setTime(sdf.parse(w));
								removeIndex.add(cal.get(Calendar.DAY_OF_WEEK) - 1);
							}
						}
						*/
					}
					
					System.out.println("########################################");
					System.out.println("removeIndex : ");
					
					for (Integer d : removeIndex) {
						System.out.println(d);
					}
					
		
					ArrayList<Integer> removeDay = new ArrayList<Integer>();
					for (Integer d : removeIndex) {
						if (!removeDay.contains(d)) {
							removeDay.add(d);
						}
					}
					Collections.sort(removeDay);
		
					// 공휴일 20, 강사휴일 24, 일정표에 목요일 없음
					System.out.println(">>> 휴일과 겹치는 요일");
					System.out.println(removeDay);
					System.out.println();
		
					int start_time = Integer
							.parseInt(String.format("%02d", Integer.parseInt(paramMap.get("start_time").toString())) + "00");
					int end_time = Integer
							.parseInt(String.format("%02d", Integer.parseInt(paramMap.get("end_time").toString())) + "00");
					
					int running_time = 10; // leveltest는 10분 고정
					if(paramMap.containsKey("flag")) {
						if(paramMap.get("flag").toString().equals("class")) {
							running_time = Integer.parseInt(paramMap.get("product_running_time").toString());
							if (running_time > 60) {
								running_time += 40;
							}
						}
					}
		
					boolean able = true;
					String study_weeks = "";
					for (int i = 0; i < 7; i++) {
						able = true;
						for (int rd : removeDay) {
							if (i == rd) {
								able = false;
							}
						}
		
						if (able) {
							study_weeks += i + "/";
						}
					}		
					
					// 신청기간에 포함되지 않는다면 ""  -> 가능한 요일 없음					
					if(!study_weeks.equals("")) {
						study_weeks = study_weeks.substring(0, study_weeks.length() - 1);
						System.out.println(">>> 가능한 요일");
						System.out.println(study_weeks);
					}
					
					/*
					study_weeks = study_weeks.substring(0, study_weeks.length() - 1);
					System.out.println(">>> 가능한 요일");
					System.out.println(study_weeks);
					*/
					
					// 오늘 이전의 날짜들이라면
					Calendar today = Calendar.getInstance();
					String today_date = sdf.format(today.getTime());
					today.setTime(sdf.parse(weeks.get(weeks.size()-1)));
					String last_date = sdf.format(today.getTime());
					System.out.println(">>> 오늘과 마지막 주차 비교");
					System.out.println(today_date + "?" + last_date);
					System.out.println();
					int test = today_date.compareTo(last_date);
					if(test > 0) { // 오늘 이전의 날짜들
						study_weeks = ""; // 가능한 요일 없음
					}
		
					int shour = start_time / 100; // 8
					int smin = start_time % 100; // 0
					int ehour = end_time / 100; // 10
					int emin = end_time % 100; // 0
		
					int t_shour = 0;
					int t_smin = 0;
		
					int rhour = 0; // shour+running_time
					int rmin = 0; // smin+running_time
					
					boolean isok = true;
					while (!((shour * 60 + smin) > (ehour * 60 + emin))) {
						rmin = smin + running_time; // 10
						rhour = shour; // 8
						if (rmin >= 60) {
							rmin -= 60;
							rhour += 1;
						}
		
						String[] able_weeks = study_weeks.split("/"); // 0/1/3/4/5
						String s_time = "";
						for (String at : able_weeks) {
							isok = true;
							t_shour = shour;
							t_smin = smin;
							while (!((t_shour * 60 + t_smin) > (rhour * 60 + rmin))) {
								s_time = String.format("%02d", t_shour) + String.format("%02d", t_smin);
								paramMap.put("search_time", at + "_" + s_time);
		
								int isTime = adminMapper.countTeacherTimetable(paramMap);
								// System.out.println(">> "+at + "_" + s_time+"\t"+isTime);
								if (isTime <= 0) {
									isok = false;
								}
		
								t_smin += running_time; // 5
								if (t_smin >= 60) {
									t_smin -= 60;
									t_shour += 1;
								}
							}
		
							if (isok) {
								Map<String, Object> tmpMap = new HashMap<String, Object>();
								System.out.println("가능\t" + at + "요일_ " + shour + ":" + smin + " ~ " + rhour + ":" + rmin);
								tmpMap.put("day", at);
								tmpMap.put("start_time", addZero(shour) + addZero(smin));
								tmpMap.put("end_time", addZero(rhour) + addZero(rmin));
								tmpMap.put("study_date", weeks.get(Integer.parseInt(at)));
								//able_times.add(tmpMap);	// 학생 레벨테스트 신청은 신청날짜 익일부터 신청 가능
								
								//System.out.println(">>>>> 레벨테스트 신청 시 당일 포함 이전날짜들 remove"); 
								// if(comp >= 0) System.out.println("당일 포함 이전 날짜");
								int comp = today_date.compareTo(weeks.get(Integer.parseInt(at)));						
								if(comp < 0)	able_times.add(tmpMap);
								
								System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
								System.out.println(tmpMap);
								
								
							}
						}
		
						smin += running_time; // 5
						if (smin >= 60) {
							smin -= 60;
							shour += 1;
						}
					}
					System.out.println(able_times);// 1차 가능한 시간대 (teacher_timetable 가능 시간대 비교)
					System.out.println();
		
					// ==============================================================================
					paramMap.put("s_date", weeks.get(0)); // 일요일
					paramMap.put("e_date", weeks.get(6)); // 토요일
					List<Map<String, Object>> leveltestLog = adminMapper.selectLeveltestLogTeacher(paramMap);
					List<Map<String, Object>> tmp_times = able_times;
					ArrayList<Integer> removeData = new ArrayList<Integer>();
					
					System.out.println(">>> leveltest log");
					System.out.println(leveltestLog);
					System.out.println();
					
					for(Map<String, Object> ll : leveltestLog) {
						String class_time = ll.get("class_time").toString().replace(":", "");
						for(int i=0; i<tmp_times.size(); i++) {					
							if((ll.get("study_date").toString().equals(tmp_times.get(i).get("study_date").toString())) && (class_time.equals(tmp_times.get(i).get("start_time").toString()))) {
								removeData.add(i);
							}
						}
					}
					
					Collections.sort(removeData);
					Collections.reverse(removeData);
					
					System.out.println(">>> 제거할 Index");
					System.out.println(removeData);
					System.out.println();
					
					for(int rd : removeData) {
						able_times.remove(rd);
					}
					
					LinkedHashMap<String, Map<String, Object>> final_times = new LinkedHashMap<String, Map<String, Object>>();
					for(Map<String, Object> at : able_times) {
						Map<String, Object> tmp_final = new HashMap<String, Object>();
						if(!final_times.containsKey(at.get("start_time")+"/"+at.get("end_time"))) {
							final_times.put(at.get("start_time")+"/"+at.get("end_time"), tmp_final);
						}
						
						final_times.get(at.get("start_time")+"/"+at.get("end_time")).put(at.get("day").toString(), at.get("study_date"));
					}
		
					System.out.println(final_times);
					System.out.println("LinkedHashMap -> List<Map<String, Object>>");
					
					//List<Map<String, Object>> list_times = new ArrayList<Map<String,Object>>();
					for(String key : final_times.keySet()) {
						List<Map<String, Object>> days = new ArrayList<Map<String,Object>>();
						Map<String, Object> tmp_map = new HashMap<String, Object>();
						String[] seTime = key.split("/");
						tmp_map.put("start_time", seTime[0]);
						tmp_map.put("end_time", seTime[1]);
						
						for(String d_key : final_times.get(key).keySet()) {
							Map<String, Object> d_tmp_map = new HashMap<String, Object>();
							d_tmp_map.put("day", d_key);
							d_tmp_map.put("study_date", final_times.get(key).get(d_key));
							days.add(d_tmp_map);
						}
						tmp_map.put("days", days);
						
						tmp_map.put("user_teacher_no", teacher.get("user_teacher_no"));
						tmp_map.put("teacher_name", teacher.get("teacher_name"));						
						
						list_times.add(tmp_map);
					}
					
					System.out.println();
					System.out.println("최종 가능한 시간대");
					System.out.println(list_times);
					
					resultMap.put("able_times", list_times);
		
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				
			} // for(Map<String, Object> teacher : siteTeachers) {  END
			
		
		} // 해당 사이트에 연결되어있는 티칭센터/강사그룹이 있는지 if else END
		
		return resultMap;	
		
	}
	
	// 학생페이지 레벨테스트 신청 등록
	@RequestMapping("/popup/leveltestApp/insertLeveltest")
	public void leveltestApp_insertLeveltest(@RequestParam Map<String, Object> paramMap) {
		int room_no = adminMapper.selectROOMPK();
		adminMapper.insertROOM();

		paramMap.put("room_no", room_no);
		studentMapper.insertLeveltestByStudent(paramMap);
	}
	
	
	
	
	
}
