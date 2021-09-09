package com.edutem.lms.controller.rest;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.MapUtils;

import com.edutem.lms.component.Common;
import com.edutem.lms.component.S3Wrapper;
import com.edutem.lms.mapper.AdminMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

@RestController
public class SaehaLmsAdminRestController {

	@Autowired
	private AdminMapper adminMapper;

	@Autowired
	private S3Wrapper s3Wrapper;

	// User delete
	@RequestMapping("/admin/user/deleteUser")
	public void userDelete(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteUser(paramMap);
	}

	// User restore
	@RequestMapping("/admin/user/restoreUser")
	public void userRestore(@RequestParam Map<String, Object> paramMap) {
		adminMapper.restoreUser(paramMap);
	}

	// User insert
	@RequestMapping("/admin/user/insertUser")
	public void userInsert(@RequestParam Map<String, Object> paramMap, MultipartFile profile) {
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
		adminMapper.insertUserStudent(paramMap);
	}

	// User update
	@RequestMapping("/admin/user/updateUser")
	public void userUpdate(@RequestParam Map<String, Object> paramMap, MultipartFile profile) {
		if (!profile.isEmpty()) {
			paramMap.put("student_profile", paramMap.get("student_phone") + ".jpg");
			try {
				s3Wrapper.upload(profile, "user_student_profile", paramMap.get("student_phone") + ".jpg");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String student_address = "";
		if (paramMap.containsKey("zip_code")) {
			student_address = paramMap.get("zip_code") + "/" + paramMap.get("address") + "/"
					+ paramMap.get("address_detail");
		}
		paramMap.put("student_address", student_address);

		adminMapper.updateUser(paramMap);
		adminMapper.updateUserStudent(paramMap);
	}

	@RequestMapping("/admin/user/checkID")
	public boolean checkUserID(@RequestParam Map<String, Object> paramMap) {
		int cnt = adminMapper.userIDCheck(paramMap);
		return (cnt > 0) ? true : false;
	}

	// select User: status=1
	@RequestMapping("/admin/user/sendAllUser")
	public List<Integer> sendAllUser() {
		return adminMapper.selectUserStatus1();
	}

	// Teacher insert
	@RequestMapping("/admin/teacher/insert")
	public void teacherInsert(@RequestParam Map<String, Object> paramMap) {
		String pwd = paramMap.get("pwd").toString();
		String encrypted = BCrypt.hashpw(pwd, BCrypt.gensalt());		// 입력한 비밀번호 -> 해쉬코드로 암호화해서 DB 저장
		paramMap.put("pwd", encrypted);
		
		// user insert
		adminMapper.insertUserByTeacher(paramMap);
		// user_teacher insert
		paramMap.put("user_no", adminMapper.selectLastAIUser() - 1);
		adminMapper.insertTeacher(paramMap);
	}

	// 강사정보조회 선택 삭제처리
	@RequestMapping("/admin/teacher/checkedDelete")
	public void teacherDelete(@RequestParam Map<String, Object> paramMap,
			@RequestParam(value = "selectedArr[]") List<String> selectedArr) {
		try {
			int cnt = selectedArr.size();

			for (int i = 0; i < cnt; i++) {		// 체크 선택된 숫자만큼 반복 
				int userNo = Integer.parseInt(selectedArr.get(i));
				adminMapper.deleteTeacherCheckedItem(userNo); // user delete -> user_teacher delete (cascade)
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// 강사정보조회 강사 정보 update
	@RequestMapping("/admin/teacher/update")
	public void teacherUpdate(@RequestParam Map<String, Object> paramMap) {
		String start_time, end_time;

		start_time = paramMap.get("start_hour").toString() + paramMap.get("start_min").toString(); // 1210
		end_time = paramMap.get("end_hour").toString() + paramMap.get("end_min").toString(); // 2200

		paramMap.put("teacher_study_time", start_time + "/" + end_time);	// 선생님 배정시간 DB 저장방식 1210/2200		
		
		Map<String, Object> teacher = adminMapper.getTeacherInfo(paramMap);
		
		// 비밀번호 BCrypt 암호화 관련
		boolean pwChangedFlag = false;
		
		if(!teacher.get("pwd").toString().equals(paramMap.get("pwd"))) {
			// 비밀번호를 수정했다면
			pwChangedFlag = true;
						
			String pwd = paramMap.get("pwd").toString();
			String encrypted = BCrypt.hashpw(pwd, BCrypt.gensalt());
			paramMap.put("pwd", encrypted);
		} 
		
		if(pwChangedFlag) {
			adminMapper.updateUserByTeacher(paramMap);	// (비밀번호가 수정됐다면 / user테이블은 비밀번호만 수정됨)
		}
		adminMapper.updateTeacher(paramMap); // user_teacher update
	}

	// 강사정보조회 시간표 팝업 -> 시간표(근무가능 시간대 체크) 수정
	@RequestMapping("/admin/teacher/timeTableUpdate")
	public void teacherTimeTableUpdate(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_teacher_no", paramMap.get("user_teacher_no"));

		adminMapper.deleteTimeTable(param); // 해당 강사 timetable 삭제 후 insert

		for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			if (entry.getKey().contains("weekTime")) {
				param.put("able_time", entry.getValue());

				adminMapper.insertTimeTable(param);
			}
		}
	}

	// 강사정보조회 강사 콤보박스 - 센터별 강사 가져오도록
	@PostMapping(value = "/admin/teacher/getTeacherComboByCenterNo")
	public List<Map<String, Object>> center_getTeacherCombo(@RequestParam Map<String, Object> paramMap)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.selectTeacherComboByCenterNo(paramMap);
		return list;
	}

	// 강사정보조회 시간표 팝업 - 강사 휴강일 삭제 버튼 기능처리
	@RequestMapping("/admin/teacher/deleteHoliday")
	public void delete_teacherHoliday(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteTeacherHoliday(paramMap);
	}

	// 강사정보조회 시간표 팝업 - 강사 휴강일 추가 버튼 기능처리
	@RequestMapping("/admin/teacher/insertHoliday")
	public Map<String, Object> insert_teacherHoliday(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();

		// 중복 여부 확인
		int duplCnt = adminMapper.getDuplTeacherHoliday(paramMap);

		if (duplCnt <= 0) {
			adminMapper.insertTeacherHoliday(paramMap);
			result.put("result", 1);
			result.put("resultMsg", "등록되었습니다.");
		} else {
			result.put("result", 0);
			result.put("resultMsg", "이미 등록되어 있는 날짜입니다.");
		}

		return result;
	}

	// 슈퍼관리자 티칭센터정보조회 - 센터 등록
	@RequestMapping("/admin/center/insert")
	public void centerInsert(@RequestParam Map<String, Object> paramMap) {
		// center_code max number 4자리 가져오기 ex) 0010
		int codeMaxNumber;

		Map<String, Object> map = adminMapper.getCenterCodeMaxNumber();		// 

		if (MapUtils.isEmpty(map)) { // 테이블에 최초 채번 시
			codeMaxNumber = 0;
		} else {
			codeMaxNumber = Integer.parseInt(map.get("codeMaxNumber").toString());
		}

		String center_code = "C" + String.format("%04d", codeMaxNumber + 1); // ex) C0001
		paramMap.put("center_code", center_code);

		adminMapper.insertCenter(paramMap);
	}

	// 슈퍼관리자 티칭센터정보조회 - 센터 정보 수정 DB Update
	@RequestMapping("/admin/center/update")
	public void centerUpdate(@RequestParam Map<String, Object> paramMap) {
		adminMapper.updateCenter(paramMap);
	}

	// 슈퍼관리자 사이트정보조회 - 사이트 등록 DB insert
	@RequestMapping("/admin/site/insert")
	public void siteInsert(@RequestParam Map<String, Object> paramMap, MultipartFile site_top_log,
			MultipartFile site_bottom_log) {
		// center_code max number 4자리 가져오기 ex) 0010
		int codeMaxNumber;

		Map<String, Object> map = adminMapper.getSiteCodeMaxNumber();		// 사이트코드 S0008 -> 등록되어있는 maxNumber 가져오기 - 코드 채번에 사용

		if (MapUtils.isEmpty(map)) { // 테이블에 최초 채번 시
			codeMaxNumber = 0;
		} else {
			codeMaxNumber = Integer.parseInt(map.get("codeMaxNumber").toString());
		}

		String site_code = "S" + String.format("%04d", codeMaxNumber + 1); // ex) S0001
		paramMap.put("site_code", site_code);

		paramMap.put("site_top_logo", "");
		paramMap.put("site_bottom_logo", "");

		int siteNo = adminMapper.selectLastAISite(); // 생성될 사이트번호 pk
		String fileName = "";

		if (!site_top_log.isEmpty()) {		// 사이트 상단 로고 이미지파일을 입력했다면
			try {
				fileName = "top_" + siteNo + ".jpg";					// 파일명: top_해당코드PK.jpg

				paramMap.put("site_top_logo", fileName);
				s3Wrapper.upload(site_top_log, "site_logo", fileName);	// aws site_logo 폴더에 이미지 업로드

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (!site_bottom_log.isEmpty()) {
			try {
				fileName = "bottom_" + siteNo + ".jpg";

				paramMap.put("site_bottom_logo", fileName);
				s3Wrapper.upload(site_bottom_log, "site_logo", fileName);

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		adminMapper.insertSite(paramMap); // teacher_group_no, center_no 없으면 '' -> null처리 (xml)

	}

	// Study insert
	@RequestMapping("/admin/studyMng/insertStudy")
	public void studyInsert(@RequestParam Map<String, Object> paramMap) {
		adminMapper.insertStudy(paramMap);
		if (paramMap.containsKey("study_not_used_site")) {
			String[] site_arr = paramMap.get("study_not_used_site").toString().split("/");
			paramMap.put("study_no", adminMapper.selectStudyPK() - 1);
			for (String no : site_arr) {
				paramMap.put("site_no", no);
				adminMapper.insertStudyNotUsedSite(paramMap);
			}
		}
	}

	// Study update
	@RequestMapping("/admin/studyMng/updateStudy")
	public void studyUpdate(@RequestParam Map<String, Object> paramMap) {
		adminMapper.updateStudy(paramMap);
		adminMapper.deleteStudyNotUsedSite(paramMap);
		
		if (paramMap.containsKey("study_not_used_site")) {
			String[] site_arr = paramMap.get("study_not_used_site").toString().split("/");
			paramMap.put("study_no", adminMapper.selectStudyPK() - 1);
			for (String no : site_arr) {
				paramMap.put("site_no", no);
				adminMapper.insertStudyNotUsedSite(paramMap);
			}
		}
	}

	// Study delete
	@RequestMapping("/admin/studyMng/deleteStudy")
	public void studyDelete(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteStudy(paramMap);
	}

	// 슈퍼관리자 - 사이트정보조회  
	@RequestMapping("/admin/site/update")
	public void siteUpdate(@RequestParam Map<String, Object> paramMap, MultipartFile site_top_log,
			MultipartFile site_bottom_log) {
		// 기존 로고 이미지 파일명
		Map<String, Object> site = adminMapper.selectSiteOne(paramMap);

		String siteNo = paramMap.get("site_no").toString();
		String fileName = "";

		// 상단 로고 수정여부
		if (paramMap.containsKey("site_top_logo_del")) {
			if ("1".equals(paramMap.get("site_top_logo_del").toString())) { // 상단로고 이미지 삭제 체크
				// System.out.println("상단로고 이미지삭제 체크 on");
				paramMap.put("site_top_logo", "");
			} else {
				// 상단로고 이미지 삭제 체크 안함
				if (!site_top_log.isEmpty()) {
					// 새로 입력한 상단로고 이미지가 있다면 새로 업로드
					try {
						fileName = "top_" + siteNo + ".jpg";

						paramMap.put("site_top_logo", fileName);
						s3Wrapper.upload(site_top_log, "site_logo", fileName);		// aws site_logo 폴더에 업로드	// 기존 같은 파일명 이미지파일은 덮어씌워진다.

					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					// 새로 입력한 상단로고 이미지가 없다면 기존 site_top_logo (url) 유지
					paramMap.put("site_top_logo", site.get("site_top_logo"));
				}
			}
		} else { // paramMap.containsKey("site_top_logo_del") 없을 경우 - 신규등록

			if (!site_top_log.isEmpty()) {
				try {
					fileName = "top_" + siteNo + ".jpg";

					paramMap.put("site_top_logo", fileName);
					s3Wrapper.upload(site_top_log, "site_logo", fileName);

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}

		// 하단 로고 수정여부
		if (paramMap.containsKey("site_bottom_logo_del")) {
			if ("1".equals(paramMap.get("site_bottom_logo_del").toString())) { // 상단로고 이미지 삭제 체크
				paramMap.put("site_bottom_logo", "");
			} else {
				// 하단로고 이미지 삭제 체크 안함
				if (!site_bottom_log.isEmpty()) {
					// 새로 입력한 상단로고 이미지가 있다면 새로 업로드
					try {
						fileName = "bottom_" + siteNo + ".jpg";

						paramMap.put("site_bottom_logo", fileName);
						s3Wrapper.upload(site_bottom_log, "site_logo", fileName);

					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					// 새로 입력한 하단로고 이미지가 없다면 기존 site_bottom_logo 유지
					paramMap.put("site_bottom_logo", site.get("site_bottom_logo"));
				}
			}
		} else { // paramMap.containsKey("site_bottom_logo_del") 없을 경우 - 신규등록

			if (!site_bottom_log.isEmpty()) {
				try {
					fileName = "bottom_" + siteNo + ".jpg";

					paramMap.put("site_bottom_logo", fileName);
					s3Wrapper.upload(site_bottom_log, "site_logo", fileName);

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		adminMapper.updateSite(paramMap); // teacher_group_no, center_no 없으면 '' -> null처리 (xml)
	}

	// 수업시작시간 관리 DB insert
	@RequestMapping("/admin/startTime/insert")
	public void startTimeInsert(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
		String[] selectedArr = null;
		selectedArr = req.getParameterValues("selectedArr[]");		// 수업시간 체크 선택된 리스트(배열)

		int study_start_time_no;

		// study_start_time	
		Map<String, Object> studyStartTime = adminMapper.selectStartTimeOne(paramMap);

		
		if (MapUtils.isEmpty(studyStartTime)) {		// 해당 언어, 런닝타임으로 등록되어있는 정보가 없다면 
			adminMapper.insertStartTime(paramMap);	// 해당 언어, 런닝타임 DB insert
			study_start_time_no = adminMapper.selectLastAIStudyStartTime() - 1;
		} else {
			study_start_time_no = Integer.parseInt(studyStartTime.get("study_start_time_no").toString());	// // 해당 언어, 런닝타임으로 등록되어있는 정보가 있다면 PK 가져오기
		}

		Map<String, Object> param;
		int duplCnt = 0;

		for (int i = 0; i < selectedArr.length; i++) {
			param = new HashMap<String, Object>();
			param.put("study_start_time_no", study_start_time_no);
			param.put("start_time", selectedArr[i]);

			// 등록 시 중복 데이터는 insert 안되고 통과
			duplCnt = adminMapper.getStartTimeDuplCnt(param);
			if (duplCnt == 0) {
				adminMapper.insertStartTimeLog(param);
			}
		}
	}

	// 수업시작시간 관리 DB update
	@Transactional(rollbackFor = Exception.class)
	@RequestMapping("/admin/startTime/update")
	public void startTimeUpdate(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
		String[] selectedArr = null;
		selectedArr = req.getParameterValues("selectedArr[]");

		// log 테이블 delete
		adminMapper.deleteStartTimeLog(paramMap);

		Map<String, Object> param;

		for (int i = 0; i < selectedArr.length; i++) {
			param = new HashMap<String, Object>();
			param.put("study_start_time_no", paramMap.get("study_start_time_no"));
			param.put("start_time", selectedArr[i]);

			adminMapper.insertStartTimeLog(param);		// 체크 선택된 시작시간 log insert
		}

	}

	// 수업시작시간 관리 체크선택 DB delete
	@RequestMapping("/admin/startTime/checkedDelete")
	public void startTimeDelete(@RequestParam Map<String, Object> paramMap,
			@RequestParam(value = "selectedArr[]") List<String> selectedArr) {
		try {
			int cnt = selectedArr.size();

			for (int i = 0; i < cnt; i++) {
				int study_start_time_no = Integer.parseInt(selectedArr.get(i));
				adminMapper.deleteStartTimeCheckedItem(study_start_time_no); // cascade
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// 휴강일 등록 - DB insert (엑셀 등록 x)
	@RequestMapping("/admin/holiday/insert")
	public Map<String, Object> insert_holiday(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();

		// 중복 여부 확인 (같은 날짜에 등록되어있는 휴강일 있는지)
		int duplCnt = adminMapper.getDuplHoliday(paramMap);

		if (duplCnt <= 0) {
			adminMapper.insertHoliday(paramMap);
			result.put("result", 1);
			result.put("resultMsg", "등록되었습니다.");
		} else {
			result.put("result", 0);
			result.put("resultMsg", "이미 등록되어 있는 휴무일입니다.");
		}
		return result;
	}

	// 휴강일 수정 - DB update
	@RequestMapping("/admin/holiday/update")
	public Map<String, Object> update_holiday(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		int duplCnt = 0;

		// 날짜 변경 여부
		if (!paramMap.get("holiday_date").toString().equals(paramMap.get("old_holiday_date").toString())) {
			// 날짜 변경 -> 중복여부 확인
			duplCnt = adminMapper.getDuplHoliday(paramMap);
		}

		if (duplCnt <= 0) {
			adminMapper.updateHoliday(paramMap);
			result.put("result", 1);
			result.put("resultMsg", "수정되었습니다.");
		} else {
			result.put("result", 0);
			result.put("resultMsg", "이미 등록되어 있는 휴무일입니다.");
		}

		return result;
	}

	// 수업관리 휴강일 선택삭제
	@RequestMapping("/admin/holiday/checkedDelete")
	public void holiday_checkedDelete(@RequestParam Map<String, Object> paramMap,
			@RequestParam(value = "selectedArr[]") List<String> selectedArr) {
		try {
			int cnt = selectedArr.size();

			for (int i = 0; i < cnt; i++) {
				int holiday_no = Integer.parseInt(selectedArr.get(i));
				adminMapper.deleteHolidayCheckedItem(holiday_no); // user delete -> user_teacher delete (cascade)
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// 수업일정 schedule_class
	@RequestMapping("/admin/classSchedule/insert")
	public Map<String, Object> insert_classSchedule(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		// System.out.println(paramMap);

		// 중복 여부 확인 (같은 언어, 기수인 중복 데이터 있는지)
		int duplCnt = adminMapper.getDuplClassSchedule(paramMap);

		if (duplCnt <= 0) {
			if ("".equals(paramMap.get("level_application_start_date").toString())) {
				paramMap.put("level_application_start_date", null);
			}
			if ("".equals(paramMap.get("level_application_end_date").toString())) {
				paramMap.put("level_application_end_date", null);
			}
			
			// 수업시작일 수업종료일 관련 체크 추가 - 해당언어 다른 기수년월에 이미 등록하려는 수업시작일~수업종료일 사이 날짜를 포함하고있다면 등록 실패 및 alert
			int containCnt = adminMapper.getContainClassSchedule(paramMap);
			int containReAppCnt = adminMapper.getContainReAppCntClassSchedule(paramMap);	// 재수강 신청일 포함여부
			int containAppCnt = adminMapper.getContainAppCntClassSchedule(paramMap);		// 일반 신청일 포함여부
			
			
			if (containCnt <= 0 && containReAppCnt <= 0 && containAppCnt <= 0) {
				adminMapper.insertClassSchedule(paramMap);
				result.put("result", 1);
				result.put("resultMsg", "등록되었습니다.");
				
			} else if (containCnt > 0) {
				result.put("result", 0);
				result.put("resultMsg", "등록하려는 수업 기간이 해당 언어 다른 기수 수업 기간에 포함됩니다.");
			} else if (containReAppCnt > 0) {
				result.put("result", 0);
				result.put("resultMsg", "등록하려는 재수강 신청 기간이 해당 언어 다른 기수 재수강 신청 기간에 포함됩니다.");
			} else if (containAppCnt > 0) {
				result.put("result", 0);
				result.put("resultMsg", "등록하려는 일반 신청 기간이 해당 언어 다른 기수 일반 신청 기간에 포함됩니다.");
			}			
			///////////////////////////////////////////////////////////////////////////////////////////////////////
			
		} else {
			result.put("result", 0);
			result.put("resultMsg", "이미 등록되어 있는 기수입니다.");
		}

		return result;
	}

	// 수업관리 수업일정 수정 - DB update 
	@RequestMapping("/admin/classSchedule/update")
	public Map<String, Object> update_classSchedule(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();

		int duplCnt = 0;
		// 기수 변경됐는지 확인 후, 변경됐다면 중복여부 확인
		if (!paramMap.get("semester_ym").toString().equals(paramMap.get("old_semester_ym").toString())) {
			duplCnt = adminMapper.getDuplClassSchedule(paramMap);
		}

		if (duplCnt <= 0) {
			// 레벨테스트 신청 시작일, 종료일은 입력 안될경우 null 값으로 db update
			if ("".equals(paramMap.get("level_application_start_date").toString())) {
				paramMap.put("level_application_start_date", null);
			}
			if ("".equals(paramMap.get("level_application_end_date").toString())) {
				paramMap.put("level_application_end_date", null);
			}
			
			// 수업시작일 수업종료일 관련 체크 추가 - 해당언어 다른 기수년월에 이미 등록하려는 수업시작일~수업종료일 사이 날짜를 포함하고있다면 등록 실패 및 alert (현재 수정하려는 수업일정 제외)
			int containCnt = adminMapper.getContainClassScheduleByUpdate(paramMap);
			int containReAppCnt = adminMapper.getContainReAppCntClassScheduleByUpdate(paramMap);	// 재수강 신청일 포함여부
			int containAppCnt = adminMapper.getContainAppCntClassScheduleByUpdate(paramMap);		// 일반 신청일 포함여부
			
			// 다른 기수랑 겹치는 신청일이 없다면 update 처리
			if (containCnt <= 0 && containReAppCnt <= 0 && containAppCnt <= 0) {
				adminMapper.updateClassSchedule(paramMap);
				result.put("result", 1);
				result.put("resultMsg", "수정되었습니다.");
				
			} else if (containCnt > 0) {
				result.put("result", 0);
				result.put("resultMsg", "수정하려는 수업 기간이 해당 언어 다른 기수 수업 기간에 포함됩니다.");
			} else if (containReAppCnt > 0) {
				result.put("result", 0);
				result.put("resultMsg", "수정하려는 재수강 신청 기간이 해당 언어 다른 기수 재수강 신청 기간에 포함됩니다.");
			} else if (containAppCnt > 0) {
				result.put("result", 0);
				result.put("resultMsg", "수정하려는 일반 신청 기간이 해당 언어 다른 기수 일반 신청 기간에 포함됩니다.");
			}	
			///////////////////////////////////////////////////////////////////////////////////////////////////////

			
		} else {
			result.put("result", 0);
			result.put("resultMsg", "이미 등록되어 있는 기수입니다.");
		}

		return result;
	}

	// 수업일정 삭제 - DB delete
	@RequestMapping("/admin/classSchedule/delete")
	public void classScheduleDelete(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteClassSchedule(paramMap);
	}

	// 사이트별 수강신청 등록 시 - 기수년월 셀렉트박스 Setting (선택된 언어에 등록되어있는 수업일정 기준 기수년월들을 가져온다.)
	@PostMapping(value = "/admin/scheduleClassSite/getSemesterYmComboByLanguage")
	public List<Map<String, Object>> admin_getSemesterYmComboByLanguage(@RequestParam Map<String, Object> paramMap)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.selectSemesterYmComboByLanguage(paramMap);
		return list;
	}

	// 사이트별 수강신청 등록 - DB insert
	@RequestMapping("/admin/scheduleClassSite/insert")
	   public Map<String, Object> insert_scheduleClassSite(@RequestParam Map<String, Object> paramMap) {
	      Map<String, Object> result = new HashMap<String, Object>();

	      // 중복 여부 확인 (같은 언어, 기수년월, 사이트인 중복 데이터 있는지)
	      int duplCnt = adminMapper.getDuplClassScheduleSite(paramMap);

	      if (duplCnt <= 0) {   
	         // 해당 사이트에 수강상품이 하나도 등록되어있지 않다면, 등록 실패 (수업일 계산이 안되기 때문에)         
	         List<Map<String, Object>> productList = new ArrayList<Map<String, Object>>();   // 해당 사이트 수강상품 리스트
	         productList = adminMapper.selectProductBySite(paramMap);
	         
	         if(productList.size() <= 0) {
	            result.put("result", 0);
	            result.put("resultMsg", "해당 사이트, 해당 언어로 등록된 수강상품이 없습니다.\n먼저 수강상품을 등록해주세요.");
	         } else {            
	            
	            
	            if ("".equals(paramMap.get("level_application_start_date").toString())) {
	               paramMap.put("level_application_start_date", null);
	            }
	            if ("".equals(paramMap.get("level_application_end_date").toString())) {
	               paramMap.put("level_application_end_date", null);
	            }
	            // 수업시작일 필수 아님
	            if ("".equals(paramMap.get("class_start_date").toString())) {
	               paramMap.put("class_start_date", null);
	            } 
	            /* 
	            // 그 전 기수 수업시작일, 수강상품으로 종료일 계산하는건 새하LMS 참고.. 아닌것같아서 확인 필요
	            else {
	               int containCnt = 0;   // 등록된 다른 기수년월 데이터 없다면 containCnt는 그대로 0 
	               
	               // 같은 언어, 같은 사이트로 등록된 다른 기수년월 데이터 있는지
	               List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	               list = adminMapper.selectScheduleClassSite(paramMap);
	               
	               // 등록된 다른 기수년월 데이터가 있다면 수업일 겹치는거 있는지 확인
	               if(list.size() > 0) {
	                  
	                  for(Map<String, Object> map : list) {
	                     if(map.containsKey("class_start_date")) {   // 수업시작일이 있다면
	                        System.out.println(map);                        
	                        System.out.println(productList.get(0));
	                     }
	                  }
	                  
	               }
	               
	               System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
	               System.out.println(list.size());
	               
	            }
				*/
	            
	            
	            // 수업시작일 수업종료일 관련 체크 추가 - 해당언어 다른 기수년월에 이미 등록하려는 수업시작일~수업종료일 사이 날짜를 포함하고있다면 등록 실패 및 alert
	            int containReAppCnt = adminMapper.getContainReAppCntClassScheduleBySite(paramMap);	// 재수강 신청일 포함여부
				int containAppCnt = adminMapper.getContainAppCntClassScheduleBySite(paramMap);		// 일반 신청일 포함여부
	            
	            // 포함되는 신청일이 없다면 등록 처리
				if (containReAppCnt <= 0 && containAppCnt <= 0) {
					adminMapper.insertClassScheduleSite(paramMap);	// insert ######################## 
					result.put("result", 1);
					result.put("resultMsg", "등록되었습니다.");
					
				} else if (containReAppCnt > 0) {
					result.put("result", 0);
					result.put("resultMsg", "등록하려는 재수강 신청 기간이 해당 언어 다른 기수 재수강 신청 기간에 포함됩니다.");
				} else if (containAppCnt > 0) {
					result.put("result", 0);
					result.put("resultMsg", "등록하려는 일반 신청 기간이 해당 언어 다른 기수 일반 신청 기간에 포함됩니다.");
				}	
				///////////////////////////////////////////////////////////////////////////////////////////////////////
	            
	         }   // if(adminMapper.getProductCntBySite(paramMap) <= 0) {     else END
	         
	      } else {
	         result.put("result", 0);
	         result.put("resultMsg", "이미 등록되어 있는 기수입니다.");
	      }
	      return result;
	   }

	// 사이트별 수강신청 수정 - DB update
	@RequestMapping("/admin/scheduleClassSite/update")
	public Map<String, Object> update_scheduleClassSite(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 수업시작일, 레벨테스트 신청 시작일, 종료일 필수아님
		if ("".equals(paramMap.get("class_start_date").toString())) {
			paramMap.put("class_start_date", null);
		}
		if ("".equals(paramMap.get("level_application_start_date").toString())) {
			paramMap.put("level_application_start_date", null);
		}
		if ("".equals(paramMap.get("level_application_end_date").toString())) {
			paramMap.put("level_application_end_date", null);
		}
		
		// 수업시작일 수업종료일 관련 체크 추가 - 해당언어 다른 기수년월에 이미 등록하려는 수업시작일~수업종료일 사이 날짜를 포함하고있다면 등록 실패 및 alert
		//int containCnt = adminMapper.getContainClassSchedule(paramMap);
		int containReAppCnt = adminMapper.getContainReAppCntClassScheduleByUpdateSite(paramMap);	// 재수강 신청일 포함여부
		int containAppCnt = adminMapper.getContainAppCntClassScheduleByUpdateSite(paramMap);		// 일반 신청일 포함여부
				
		
		if (containReAppCnt <= 0 && containAppCnt <= 0) {
			adminMapper.updateClassScheduleSite(paramMap);
			result.put("result", 1);
			result.put("resultMsg", "수정되었습니다.");
			
		} else if (containReAppCnt > 0) {
			result.put("result", 0);
			result.put("resultMsg", "수정하려는 재수강 신청 기간이 해당 언어 다른 기수 재수강 신청 기간에 포함됩니다.");
		} else if (containAppCnt > 0) {
			result.put("result", 0);
			result.put("resultMsg", "수정하려는 일반 신청 기간이 해당 언어 다른 기수 일반 신청 기간에 포함됩니다.");
		}			
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		return result;
	}

	// 사이트별 수강신청 삭제 - DB delete
	@RequestMapping("/admin/scheduleClassSite/delete")
	public void scheduleClassSiteDelete(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteClassScheduleSite(paramMap);
	}

	// 수업기본정보 수업상세정보 스케줄삭제 버튼 기능처리
	@RequestMapping("/admin/classInfo/deleteClass")
	public void classInfo_deleteClass(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteClass(paramMap);
	}

	// 수강과정 레벨 insert
	@RequestMapping("/admin/studyLevel/insertStudyLevel")
	public void insertStudyLevel(@RequestParam Map<String, Object> paramMap) {
		int level = adminMapper.selectStudyLevelMax(paramMap) + 1;
		paramMap.put("level", level);

		adminMapper.insertStudyLevel(paramMap);
	}

	// 수강과정 레벨 update
	@RequestMapping("/admin/studyLevel/updateStudyLevel")
	public void updateStudyLevel(@RequestParam Map<String, Object> paramMap) {
		adminMapper.updateStudyLevel(paramMap);
	}

	// 수강과정 레벨 delete
	@RequestMapping("/admin/studyLevel/deleteStudyLevel")
	public void deleteStudyLevel(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteStudyLevel(paramMap);
	}

	// 교재리스트 교육과정 레벨 select
	@RequestMapping("/admin/textbookMng/selectStudyLevel")
	public List<Map<String, Object>> selectStudyLevel(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectStudyLevelAll(paramMap);
	}

	// 교재리스트 insert
	@RequestMapping("/admin/textbookMng/insertTextbook")
	public void insertTextbook(@RequestParam Map<String, Object> paramMap, MultipartFile mfile) {
		int pk = adminMapper.selectTextbookPK();

		if (!mfile.isEmpty()) {
			String[] file = mfile.getOriginalFilename().split("\\.");
			try {
				s3Wrapper.upload(mfile, "textbook", "textbook" + pk + "." + file[file.length - 1]);
				paramMap.put("textbook_download", "textbook" + pk + "." + file[file.length - 1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		paramMap.put("textbook_order", pk);
		adminMapper.insertTextbook(paramMap);
	}

	// 교재리스트 update
	@RequestMapping("/admin/textbookMng/updateTextbook")
	public void updateTextbook(@RequestParam Map<String, Object> paramMap, MultipartFile mfile) {
		if (!mfile.isEmpty()) {
			String[] file = mfile.getOriginalFilename().split("\\.");
			try {
				s3Wrapper.upload(mfile, "textbook", "textbook" + paramMap.get("textbook_order") + "." + file[file.length - 1]);
				paramMap.put("textbook_download", "textbook" + paramMap.get("textbook_order") + "." + file[file.length - 1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		adminMapper.updateTextbook(paramMap);
	}

	// 교재리스트 delete
	@RequestMapping("/admin/textbookMng/deleteTextbook")
	public void deleteTextbook(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteTextbook(paramMap);
	}

	// 교재 토픽리스트 insert
	@RequestMapping("/admin/textbookTopic/insertTextbookTopic")
	public void insertTextbookTopic(@RequestParam Map<String, Object> paramMap, MultipartHttpServletRequest request) {
		int pk = adminMapper.selectTextbookTopicPK();

		List<MultipartFile> mpf = request.getFiles("mfile");
		for(MultipartFile mf : mpf) {
			String[] file = mf.getOriginalFilename().split("\\.");
			
			if(file[file.length - 1].equals("mp3")) {
				try {
					s3Wrapper.upload(mf, "textbook_audio", "topic_audio" + pk + "." + file[file.length - 1]);
					paramMap.put("audio_file", "topic_audio" + pk + "." + file[file.length - 1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(file[file.length - 1].equals("pdf")) {
				try {
					s3Wrapper.upload(mf, "textbook_pdf", "topic_pdf" + pk + "." + file[file.length - 1]);
					paramMap.put("pdf_file", "topic_pdf" + pk + "." + file[file.length - 1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		int unit = adminMapper.selectTextbookTopicUnitMax(paramMap) + 1;
		paramMap.put("unit", unit);

		adminMapper.insertTextbookTopic(paramMap);
	}

	// 교재 토픽리스트 update
	@RequestMapping("/admin/textbookTopic/updateTextbookTopic")
	public void updateTextbookTopic(@RequestParam Map<String, Object> paramMap, MultipartHttpServletRequest request) {
		int pk = Integer.parseInt(paramMap.get("textbook_topic_no").toString());
		
		System.out.println("111111111111111111");
		System.out.println(paramMap);
		
		List<MultipartFile> mpf = request.getFiles("mfile");
		for(MultipartFile mf : mpf) {
			String[] file = mf.getOriginalFilename().split("\\.");
			
			if(file[file.length - 1].equals("mp3")) {
				try {
					s3Wrapper.upload(mf, "textbook_audio", "topic_audio" + pk + "." + file[file.length - 1]);
					paramMap.put("audio_file", "topic_audio" + pk + "." + file[file.length - 1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(file[file.length - 1].equals("pdf")) {
				try {
					s3Wrapper.upload(mf, "textbook_pdf", "topic_pdf" + pk + "." + file[file.length - 1]);
					paramMap.put("pdf_file", "topic_pdf" + pk + "." + file[file.length - 1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("22222222222222222");
		System.out.println(paramMap);
		
		if(!paramMap.containsKey("audio_file")) {
			paramMap.put("audio_file", adminMapper.selectTextbookTopicOne(paramMap).get("audio_file"));
		}
		if(!paramMap.containsKey("pdf_file")) {
			paramMap.put("pdf_file", adminMapper.selectTextbookTopicOne(paramMap).get("pdf_file"));
		}
		
		System.out.println("3333333333333333333");
		System.out.println(paramMap);

		adminMapper.updateTextbookTopic(paramMap);
	}

	// 교재 토픽리스트 delete
	@RequestMapping("/admin/textbookTopic/deleteTextbookTopic")
	public void deleteTextbookTopic(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteTextbookTopic(paramMap);
	}

	// 체크선택한 회원 SMS 보내기
	/* coolSMS 사용
	@RequestMapping("/sms/checkedSend")
	public void checked_smsSend(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) throws Exception {
		System.out.println(paramMap);

		
		String[] userNoArr = paramMap.get("selectedArr").toString().split(",");

		List<String> listArr = new ArrayList<String>();

		for (int i = 0; i < userNoArr.length; i++) {
			listArr.add(userNoArr[i]);
		}

		// SMS 팝업 open시 체크된 user 중복 제거
		// HashSet 중복 제거
		HashSet<String> arr2 = new HashSet<String>(listArr);
		List<String> userStudentNoArr = new ArrayList<String>(arr2); // 중복 제거 후 user_no List		

		Map<String, Object> smsMap = new HashMap<String, Object>();

		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat time_format = new SimpleDateFormat("HH:mm:ss");

		Date today = new Date();

		String send_date = date_format.format(today); // SMS 내역 날짜 저장용
		String send_time = time_format.format(today); // SMS 내역 시간 저장용

		// SMS 전송
		if ("sms".equals(paramMap.get("type").toString())) {
			for (int i = 0; i < userStudentNoArr.size(); i++) {
				Map<String, Object> user = adminMapper.getUserByUserStudentNo(userStudentNoArr.get(i)); // log 테이블 저장할 때 사용할 user Map	//user_no..

				smsMap.put("text", paramMap.get("text")); // 내용
				smsMap.put("send_number", paramMap.get("send_number")); // 발신번호
				smsMap.put("receipt_number", user.get("student_phone")); // 수신자번호

				// sms_log 테이블 저장 정보
				smsMap.put("user_student_no", user.get("user_student_no"));
				smsMap.put("send_date", send_date);
				smsMap.put("send_time", send_time);

				smsMap.put("type", "SMS"); // 전송 방식

				smsMap.put("reserve_yn", paramMap.get("reserve_flag")); // 예약발송 사용여부 '1','0'
				smsMap.put("reserve_dt", paramMap.get("reserve_dt").toString().replace(".", "").replace("-", ""));
				smsMap.put("reserve_hour", paramMap.get("reserve_hour"));
				smsMap.put("reserve_min", paramMap.get("reserve_min"));

				// Common.coolSMSSend(logMap, "sms");
				Map<String, Object> logMap = Common.coolSMSSend2(smsMap, "sms");		// SMS Log에 저장할 정보를 받아온다.

				adminMapper.insertSmslog(logMap);
			}

		} else {	// LMS 전송

			for (int i = 0; i < userStudentNoArr.size(); i++) {
				Map<String, Object> user = adminMapper.getUserByUserStudentNo(userStudentNoArr.get(i)); // log 테이블 저장할 때 사용할 user Map
				
				smsMap.put("text", paramMap.get("text")); // 내용
				smsMap.put("send_number", paramMap.get("send_number")); // 발신번호
				smsMap.put("receipt_number", user.get("student_phone")); // 수신자번호

				// sms_log 테이블 저장 정보
				smsMap.put("user_student_no", user.get("user_student_no"));
				smsMap.put("send_date", send_date);
				smsMap.put("send_time", send_time);

				smsMap.put("type", "LMS"); // 전송 방식

				smsMap.put("reserve_yn", paramMap.get("reserve_flag")); // 예약발송 사용여부 '1','0'
				smsMap.put("reserve_dt", paramMap.get("reserve_dt").toString().replace(".", "").replace("-", ""));	// 예약일 설정
				smsMap.put("reserve_hour", paramMap.get("reserve_hour"));											// 예약 시간(hour)
				smsMap.put("reserve_min", paramMap.get("reserve_min"));												// 예약 분(min)

				// Common.coolSMSSend(logMap, "sms");
				Map<String, Object> logMap = Common.coolSMSSend2(smsMap, "lms");

				adminMapper.insertSmslog(logMap);
			}
		}
	}
	*/
	
	
	@PostMapping(value = "/userInfo/getLogInfo")
	public Map<String, Object> center_classDetail_getLogInfo(@RequestParam Map<String, Object> paramMap)
			throws ParseException {
		// System.out.println(paramMap); // no, flag ( no / level_test_log_no /
		// user_student_no )

		Map<String, Object> info = new HashMap<String, Object>();

		// 수업상태 - 수업전, 수업중, 종료 (수업기간)
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		Date date = format.parse(today); // ParseException throw

		Date start_dt, end_dt;
		int startCompare, endCompare;

		if ("class".equals(paramMap.get("flag").toString())) {
			System.out.println(paramMap);

			info = adminMapper.getClassDetailInfo(paramMap);

			if (info.containsKey("end_date")) {
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
			} else {
				info.put("end_date", "");
				info.put("status", "Waiting");
			}

		} else {
			// 레벨테스트
			info = adminMapper.getLevelDetailInfo(paramMap);
		}
		return info;
	}

	// 회원상세보기 팝업 - 회원정보 update
	@PostMapping(value = "/userInfo/userUpdate")
	public void center_classDetail_userUpdate(@RequestParam Map<String, Object> paramMap) throws Exception {
		String student_address = "";
		if (paramMap.containsKey("zip_code")) {
			student_address = paramMap.get("zip_code") + "/" + paramMap.get("address") + "/"
					+ paramMap.get("address_detail");
		}
		paramMap.put("student_address", student_address);

		adminMapper.updateUserByUserDetail(paramMap);
	}

	// 선생님별 주간수업일정 - 강사 셀렉트박스 내용 (선택 되어있는 티칭센터 셀렉트박스 값에 따라 나오도록)
	@PostMapping(value = "/admin/weeklySchedule/getTeacherComboByCenter")
	public List<Map<String, Object>> admin_getTeacherComboByCenter(@RequestParam Map<String, Object> paramMap)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.selectTeacherComboByCenterNo(paramMap);
		return list;
	}

	// 교재 페이지 insert
	@RequestMapping("/admin/textbookTopic/pageDetail/insertTextbookPage")
	public void insertTextbookPage(@RequestParam Map<String, Object> paramMap, MultipartFile file_url) {
		System.out.println("@@@@@@@@@@@@@@");
		System.out.println(paramMap);

		int pk = adminMapper.selectTextbookPagePK();

		if (!file_url.isEmpty()) {
			String[] file = file_url.getOriginalFilename().split("\\.");
			try {
				s3Wrapper.upload(file_url, "textbook_page", "page" + pk + "." + file[file.length - 1]);
				paramMap.put("file_url", "page" + pk + "." + file[file.length - 1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int page = adminMapper.selectTextbookPagePageMax(paramMap) + 1;
		paramMap.put("page", page);

		adminMapper.insertTextbookPage(paramMap);
	}

	// 교재 페이지 update
	@RequestMapping("/admin/textbookTopic/pageDetail/updateTextbookPage")
	public void updateTextbookPage(@RequestParam Map<String, Object> paramMap, MultipartFile mfile) {
		int pk = Integer.parseInt(paramMap.get("textbook_page_no").toString());

		if (!mfile.isEmpty()) {
			String[] file = mfile.getOriginalFilename().split("\\.");
			try {
				s3Wrapper.upload(mfile, "textbook_page", "page" + pk + "." + file[file.length - 1]);
				paramMap.put("file_url", "page" + pk + "." + file[file.length - 1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			paramMap.put("file_url", adminMapper.selectTextbookPageOne(paramMap).get("file_url"));
		}

		adminMapper.updateTextbookPage(paramMap);
	}

	// 교재 페이지 delete
	@RequestMapping("/admin/textbookTopic/pageDetail/deleteTextbookPage")
	public void deleteTextbookPage(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteTextbookPage(paramMap);
	}

	// 온라인 북 재생
	@RequestMapping("/admin/onlineBook/playOnlineBook")
	public Map<String, Object> playOnlineBook(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectTextbookTopicOne(paramMap);
	}

	// 휴강 및 보강 처리
	@RequestMapping("/admin/postponeMng/updatePostpone")
	public void updatePostpone(@RequestParam Map<String, Object> paramMap) {
		adminMapper.updatePostpone(paramMap);
	}

	// 교재토픽 콤보
   	@PostMapping(value = "/reportPopup/getTopicCombo")
   	public List<Map<String, Object>> getTopicCombo(@RequestParam Map<String, Object> paramMap) throws Exception {
   		List<Map<String, Object>> list = adminMapper.selectTextbookTopicCombo(paramMap);   		   		
   		return list;
   	}
   	
   	// 교재페이지 콤보
   	@PostMapping(value = "/reportPopup/getPageCombo")
   	public List<Map<String, Object>> getPageCombo(@RequestParam Map<String, Object> paramMap) throws Exception {
   		List<Map<String, Object>> list = adminMapper.selectTextbookPageCombo(paramMap);
   		return list;
   	}   	
   	
   	// report 팝업 - 진도 update
   	// report: class_log -> textbook_topic_no update
   	@RequestMapping("/reportPopup/updateClassLogPage")
   	public void updateClassLogPage(@RequestParam Map<String, Object> paramMap) {
   		if("".equals(paramMap.get("slide_number").toString())) paramMap.put("slide_number", null);
   		adminMapper.updateClassLogPage(paramMap);
   	}
   	
   	// report 팝업 SMS 보내기  
   	@RequestMapping("/reportPopup/smsSend")   
   	public void reportPopup_smsSend(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) throws Exception {
   		Map<String, Object> smsMap = new HashMap<String, Object>();
   		
   		SimpleDateFormat date_format = new SimpleDateFormat ("yyyy-MM-dd");
        SimpleDateFormat time_format = new SimpleDateFormat ("HH:mm:ss");
        
        Date today = new Date();
		
        String send_date = date_format.format(today);			// 2021-06-01
        String send_time = time_format.format(today);			// 15:11:48
    	
        String smsText = "";
        
        if("sound".equals(paramMap.get("flag").toString())) {			// No Sound 버튼 클릭 시 문구
        	smsText = "회원님의 소리가 들리지않습니다.\n헤드셋 연결 확인 바랍니다.";
        } else {														// No Show 버튼 클릭 시 문구
        	smsText = "회원님 수업시간입니다.\n수업참여바랍니다.";
        }
               
    	smsMap.put("text", smsText);									// 내용
    	smsMap.put("send_number", "07040824063");						// 발신번호
    	smsMap.put("receipt_number", paramMap.get("student_phone"));	// 수신자번호
    	
    	
    	// sms_log 테이블 저장 정보
    	smsMap.put("user_student_no", paramMap.get("user_student_no"));
    	smsMap.put("send_date", send_date);
    	smsMap.put("send_time", send_time);
    	
    	smsMap.put("type", "SMS");										// 전송 방식    	
    	
    	smsMap.put("reserve_yn", "0");			// 예약발송 사용여부 '1','0'
    	smsMap.put("reserve_dt", "");
    	smsMap.put("reserve_hour", "");
    	smsMap.put("reserve_min", "");    	
    	
    	//Common.coolSMSSend(logMap, "sms");
    	Map<String, Object> logMap = Common.coolSMSSend2(smsMap, "sms");
    	
    	adminMapper.insertSmslog(logMap);
   	}
   	
   	// reportPopup report update	// 1.Register 버튼 클릭
   	@RequestMapping("/reportPopup/report/register")
   	public void updateReport(@RequestParam Map<String, Object> paramMap) {
   		adminMapper.updateReport(paramMap);
   	}
   	
   	// reportPopup report makeup	// Make Up 버튼 클릭
   	@RequestMapping("/reportPopup/report/makeup")
   	public void makeupReport(@RequestParam Map<String, Object> paramMap) {
   		// 0 - before / 1 - complete / 2 - cancel / 3 - makeup
   		adminMapper.makeupReport(paramMap);
   	}
   	
   	// 시간별 수업현황 Makeup 취소 버튼 (make up 상태인 수업에 대해서 Makeup 취소 버튼이 활성화됨)  
   	@RequestMapping("/reportPopup/report/cancelMakeup")
   	public void cancelMakeupReport(@RequestParam Map<String, Object> paramMap) {
   		// 0 - before / 1 - complete / 2 - cancel / 3 - makeup
   		adminMapper.cancelMakeupReport(paramMap);
   	}
   	
   	// reportPopup report 2.Completed (일반수강)
   	@RequestMapping("/reportPopup/report/complete")
   	public Map<String, Object> completeReport(@RequestParam Map<String, Object> paramMap) {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		
   		Map<String, Object> registerInfo = adminMapper.getRegisterInfo(paramMap);
   		
   		if(registerInfo.get("totalCnt").toString().equals(registerInfo.get("regCnt").toString())) {
   			resultMap.put("completeFlag", "1");
   			adminMapper.completeReport(paramMap);
   		} else {
   			resultMap.put("completeFlag", "0");
   		}
   		

   		return resultMap;
   	}
   	
   	// reportPopup report 2.Completed (레벨테스트)
   	@RequestMapping("/reportPopup/report/levelTestComplete")
   	public Map<String, Object> completelevelTestReport(@RequestParam Map<String, Object> paramMap) {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		
   		Map<String, Object> registerInfo = adminMapper.getRegisterLevelTestInfo(paramMap); 
   		
   		if(registerInfo.get("totalCnt").toString().equals(registerInfo.get("regCnt").toString())) {
   			resultMap.put("completeFlag", "1");
   			adminMapper.completeLevelTestReport(paramMap);
   		} else {
   			resultMap.put("completeFlag", "0");
   		}

   		return resultMap;
   	}
   	
   	// reportPopup 레벨테스트 결과레벨 셀렉트박스
   	@RequestMapping("/reportPopup/report/selectStudyLevelCombo")
   	public List<Map<String, Object>> report_selectStudyLevelCombo(@RequestParam Map<String, Object> paramMap) {
   		List<Map<String, Object>> list = adminMapper.selectStudyLevelCombo(paramMap);
   		return list;
   	}
   	
   	// reportPopup leveltest 1.Resister 버튼
   	@RequestMapping("/reportPopup/report/levelRegister")
   	public void updateLevelReport(@RequestParam Map<String, Object> paramMap) {   		
	   	if("".equals(paramMap.get("study_level_no").toString())) {
	   		paramMap.put("study_level_no", null);
	   	}
   		adminMapper.updateReportLevelTest(paramMap);
   	}

	// 수강상품 등록
	@RequestMapping("/admin/productMng/insertProduct")
	public void insertProduct(@RequestParam Map<String, Object> paramMap, MultipartFile mfile) {
		System.out.println(paramMap);
		System.out.println(paramMap.get("product_price").toString().equals(""));

		if (paramMap.get("product_price").toString().equals("")) {
			paramMap.put("product_price", 0);
		}

		int pk = adminMapper.selectProductPK();
		if (!mfile.isEmpty()) {
			String[] file = mfile.getOriginalFilename().split("\\.");
			try {
				s3Wrapper.upload(mfile, "product_img", "product" + pk + "." + file[file.length - 1]);
				paramMap.put("product_img", "product" + pk + "." + file[file.length - 1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		adminMapper.insertProduct(paramMap);
	}

	// 수강상품 update
	@RequestMapping("/admin/productMng/updateProduct")
	public void updateProduct(@RequestParam Map<String, Object> paramMap, MultipartFile mfile) {
		int pk = Integer.parseInt(paramMap.get("product_no").toString());

		if (!mfile.isEmpty()) {
			String[] file = mfile.getOriginalFilename().split("\\.");
			try {
				s3Wrapper.upload(mfile, "product_img", "product" + pk + "." + file[file.length - 1]);
				paramMap.put("product_img", "product" + pk + "." + file[file.length - 1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			paramMap.put("product_img", adminMapper.selectProductOne(paramMap).get("product_img"));
		}

		adminMapper.updateProduct(paramMap);
	}
	
	// 신청내역 상태변경
	@RequestMapping("/admin/paymentStatus")
	public void paymentStatus(@RequestParam Map<String, Object> paramMap) {
		adminMapper.paymentStatus(paramMap);
	}

	// 레벨테스트 생성
	@RequestMapping("/userDetail/insertLeveltest")
	public int insertLeveltest(@RequestParam Map<String, Object> paramMap) {
		int limit = adminMapper.selectSiteLeveltestLimit(paramMap);
		int count = adminMapper.countUserLeveltest(paramMap);

		if (count < limit) {
			int room_no = adminMapper.selectROOMPK();
			adminMapper.insertROOM();

			paramMap.put("room_no", room_no);
			paramMap.put("class_phone", paramMap.get("student_phone"));
			adminMapper.insertLeveltest(paramMap);

			return -1;
		} else {
			return count;
		}
	}

	// 정규수업 생성
	@RequestMapping("/userDetail/getProductSel")
	public List<Map<String, Object>> getProductSel(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectProductMakeClass(paramMap);
	}

	@RequestMapping("/userDetail/getClassSel")
	public List<Map<String, Object>> getClassSel(@RequestParam Map<String, Object> paramMap) {
		List<Map<String, Object>> classSel = adminMapper.getClassSel(paramMap);

		for (int i = 0; i < classSel.size(); i++) {
			String[] personnel = classSel.get(i).get("product_personnel").toString().split(":");
			int totPerson = Integer.parseInt(personnel[1]);
			int nowPerson = Integer.parseInt(classSel.get(i).get("class_student").toString());
			System.out.println(totPerson + " = " + nowPerson);
			if (totPerson == nowPerson) {
				classSel.remove(i);
				i--;
			}
		}

		return classSel;
	}

	@RequestMapping("/userDetail/insertClass")
	public boolean insertClass(@RequestParam Map<String, Object> paramMap) {
		int room_no = adminMapper.selectROOMPK();
		adminMapper.insertROOM();

		paramMap.put("class_phone", paramMap.get("student_phone"));
		paramMap.put("room_no", room_no);
		System.out.println("@@@@@ insertClass @@@@@");
		System.out.println(paramMap);

		if (paramMap.get("class_no").toString().equals("")) {
			// 생성
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
				String end_date = sdf.format(cal.getTime()); // 마지막 수업일
				Collections.sort(study_dates); // 수업일들

				int class_no = adminMapper.selectClassPK();
				paramMap.put("class_no", class_no);
				paramMap.put("end_date", end_date);
				paramMap.put("class_phone", paramMap.get("student_phone"));

				adminMapper.insertNewClass(paramMap); // class 생성

				int class_student_no = adminMapper.selectClassStudentPK();
				adminMapper.insertClassStudent(paramMap); // class_student 생성

				for (String s : study_dates) {
					paramMap.put("study_date", s);
					adminMapper.insertClassLogFirst(paramMap); // class_log 생성
				}

				List<Map<String, Object>> classLog = adminMapper.selectClassLogClass(paramMap);
				paramMap.put("class_student_no", class_student_no);
				for (Map<String, Object> cl : classLog) {
					paramMap.put("class_log_no", cl.get("class_log_no"));
					adminMapper.insertClassScore(paramMap); // class_score 생성
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			// 참가

			// class_student 생성
			int class_student_no = adminMapper.selectClassStudentPK();
			paramMap.put("class_phone", paramMap.get("student_phone"));
			adminMapper.insertClassStudent(paramMap);

			// class_score 생성
			List<Map<String, Object>> classLog = adminMapper.selectClassLogClass(paramMap);
			paramMap.put("class_student_no", class_student_no);
			for (Map<String, Object> cl : classLog) {
				paramMap.put("class_log_no", cl.get("class_log_no"));
				adminMapper.insertClassScore(paramMap);
			}
		}

		return true;
	}

	// 수강상세정보 수정
	@RequestMapping("/userDetail/updateClass")
	public boolean updateClass(@RequestParam Map<String, Object> paramMap) {
		System.out.println("@@@@@@@@@");
		System.out.println(paramMap);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (paramMap.get("flag").toString().equals("class")) {
			try {
				Date start_date = sdf.parse(paramMap.get("start_date").toString());
				Date end_date = sdf.parse(paramMap.get("end_date").toString());
				if(end_date.compareTo(start_date) < 0) { // end_date가 start_date 이전인 경우
					return false;
				}
				else {
					Map<String, Object> classOne = adminMapper.selectClassOne(paramMap);
					Date before_date = sdf.parse(classOne.get("end_date").toString());
					Date after_date = sdf.parse(paramMap.get("end_date").toString());

					if (after_date.compareTo(before_date) < 0) { // after_date가 before_date 이전인 경우
						adminMapper.deleteClassLogTermChange(paramMap);
					}

					adminMapper.updateClassDetail(paramMap);
					adminMapper.updateClassStudentDetail(paramMap);
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (paramMap.get("flag").toString().equals("leveltest")) {
			adminMapper.updateLeveltestDetail(paramMap);
		}
		
		return true;
	}

	// 수강자 완전삭제
	@RequestMapping("/userDetail/deleteClass")
	public void deleteClass(@RequestParam Map<String, Object> paramMap) {
		String[] product_personnel = paramMap.get("product_personnel").toString().split(":");
		int personnel = Integer.parseInt(product_personnel[1]);
		if (personnel == 1) {
			adminMapper.deleteClass(paramMap);
		} else {
			adminMapper.deleteClassStudent(paramMap);
		}
	}

	@RequestMapping("/userDetail/deleteLeveltest")
	public void deleteLeveltest(@RequestParam Map<String, Object> paramMap) {
		adminMapper.deleteLeveltest(paramMap);
	}

	// 강사배정
	@RequestMapping("/userDetail/studySelect")
	public List<Map<String, Object>> studySelect(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectTextbookStudy(paramMap);
	}

	@RequestMapping("/userDetail/centerSelect")
	public List<Map<String, Object>> centerSelect(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectTeacherCenterClass(paramMap);
	}

	@RequestMapping("/userDetail/groupSelect")
	public List<Map<String, Object>> groupSelect(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectTeacherGroupClass(paramMap);
	}

	@RequestMapping("/userDetail/centerSelectLeveltest")
	public List<Map<String, Object>> centerSelectLeveltest(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectTeacherCenterLeveltest(paramMap);
	}

	@RequestMapping("/userDetail/groupSelectLeveltest")
	public List<Map<String, Object>> groupSelectLeveltest(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectTeacherGroupLeveltest(paramMap);
	}

	@RequestMapping("/userDetail/searchTeacher")
	public List<Map<String, Object>> searchTeacher(@RequestParam Map<String, Object> paramMap) {
		// final_able_times
		List<Map<String, Object>> able_times = new ArrayList<Map<String, Object>>();

		// 1. 강사의 휴강일이 수업일과 겹치면 그 강사는 배정할 수 없다.
		ArrayList<String> study_dates = new ArrayList<String>();
		for (Map<String, Object> cl : adminMapper.selectClassLogSeachDate(paramMap)) {
			study_dates.add(cl.get("study_date").toString());
		}
		Collections.sort(study_dates); // 해당 class의 class_log에서부터 받아온 수업일들
		System.out.println("<< 1 >> 수업일들");
		System.out.println(study_dates);

		List<Map<String, Object>> teacher_holiday = adminMapper.getTeacherHoliday(paramMap); // 조회하는 강사의 휴강일
		boolean isOverlap = false; // 겹치는지 여부
		for (String sd : study_dates) {
			for (Map<String, Object> th : teacher_holiday) {
				if (sd.equals(th.get("teacher_holiday_date").toString())) {
					isOverlap = true;
				}
			}
		}

		if (isOverlap) {
			return null; // 강사의 휴강일이 수업일과 겹치므로 해당 강사는 배정 불가
		} else {
			// 강사의 휴강일과 수업일이 겹치지 않음

			System.out.println(">>> parameter 받아온 값");
			System.out.println(paramMap);
			Map<String, Object> product = adminMapper.selectProductOne(paramMap);
			Map<String, Object> tutorInfo = null;
			if(paramMap.get("center_no").toString().equals("0")) {
				tutorInfo = adminMapper.selectSetTutorInfoGroup(paramMap);
			}
			else {
				tutorInfo = adminMapper.selectSetTutorInfoClass(paramMap);
			}
			System.out.println(">>> product 정보");
			System.out.println(product);

			System.out.println("===================================================================");
			int start_time = Integer
					.parseInt(String.format("%02d", Integer.parseInt(paramMap.get("start_time").toString())) + "00");
			int end_time = Integer
					.parseInt(String.format("%02d", Integer.parseInt(paramMap.get("end_time").toString())) + "00");

			int running_time = Integer.parseInt(product.get("product_running_time").toString());
			if (running_time > 60) {
				running_time += 40;
			}

			String study_weeks = paramMap.get("week").toString().replace("sun", "0").replace("mon", "1")
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

						t_smin += running_time; // 5
						if (t_smin >= 60) {
							t_smin -= 60;
							t_shour += 1;
						}
					}
				}

				if (isok) {
					Map<String, Object> tmpMap = new HashMap<String, Object>();
					System.out.println("가능\t" + shour + ":" + smin + " ~ " + rhour + ":" + rmin);
					tmpMap.put("center_name", tutorInfo.get("center_name"));
					tmpMap.put("teacher_name", tutorInfo.get("teacher_name"));
					tmpMap.put("start_time", addZero(shour) + addZero(smin));
					tmpMap.put("end_time", addZero(rhour) + addZero(rmin));
					able_times.add(tmpMap);
				}

				smin += running_time; // 5
				if (smin >= 60) {
					smin -= 60;
					shour += 1;
				}
			}
			System.out.println(able_times);// 1차 가능한 시간대 (teacher_timetable 가능 시간대 비교)

			System.out.println("===================================================================");
			List<Map<String, Object>> classLog = adminMapper.selectClasslogTeacher(paramMap);
			System.out.println("classLog");
			System.out.println(classLog);

			System.out.println("**remove*****************************");
			ArrayList<Integer> removeData = new ArrayList<Integer>();
			for (Map<String, Object> cl : classLog) {
				for (String study_date : study_dates) {
					if (cl.get("study_date").toString().equals(study_date)) {
						System.out.println(cl.get("study_date")+" = "+study_date);
						for (int i = 0; i < able_times.size(); i++) {
							int class_time = Integer.parseInt(cl.get("start_time").toString().replace(":", ""));
							int class_end_time = class_time + Integer.parseInt(cl.get("product_running_time").toString());
							int stime = Integer.parseInt(able_times.get(i).get("start_time").toString());
							int etime = Integer.parseInt(able_times.get(i).get("end_time").toString());
	   						System.out.println(stime+" <= "+class_time+" < "+etime+"\t"+stime+" < "+class_end_time+" <= "+etime);
							
							if ((stime <= class_time && etime > class_time) || (stime < class_end_time && etime >= class_end_time)) {
								removeData.add(i);
							}
						}
					}
				}
				System.out.println(removeData);
				System.out.println();
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
	}

	@RequestMapping("/userDetail/setTutor")
	public void setTutor(@RequestParam Map<String, Object> paramMap) {
		System.out.println(">>> parameter 받아온 값");
		System.out.println(paramMap);

		Map<String, Object> classInfo = adminMapper.selectClassOne(paramMap);
		if (!classInfo.containsKey("user_teacher_no")) {
			paramMap.put("no_teacher", 1);
		} else {
			paramMap.put("no_teacher", 0);
		}

		adminMapper.updateClassSetTutor(paramMap); // class update
		adminMapper.updateClassLogSetTutor(paramMap); // class_log update
	}

	// 레벨테스트 강사배정
	@RequestMapping("/userDetail/setTutorLeveltest")
	public void setTutorLeveltest(@RequestParam Map<String, Object> paramMap) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@");
		System.out.println(paramMap);
		adminMapper.updateLeveltestSetTutor(paramMap);
	}

	@RequestMapping("/userDetail/searchTeacherLeveltest")
	public Map<String, Object> searchTeacherLeveltest(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<Map<String, Object>> able_times = new ArrayList<Map<String, Object>>();
		System.out.println(
				"==============================================================================================================================================");
		System.out.println(">>> 받아온 parameter");
		System.out.println(paramMap);

		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(paramMap.get("start_date").toString());
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
			
			if(paramMap.containsKey("flag")) {
				if(paramMap.get("flag").toString().equals("class")) {
					Map<String, Object> classInfo = adminMapper.selectClassOne(paramMap);
					Map<String, Object> product = adminMapper.selectProductOne(classInfo);
					paramMap.put("product_language", product.get("product_language"));
					paramMap.put("product_running_time", product.get("product_running_time"));
				}
			}

			List<Map<String, Object>> holiday = adminMapper.selectHolidayByLang(paramMap);
			List<Map<String, Object>> t_holiday = adminMapper.getTeacherHoliday(paramMap);
			ArrayList<Integer> removeIndex = new ArrayList<Integer>();
			for (String w : weeks) {
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

			study_weeks = study_weeks.substring(0, study_weeks.length() - 1);
			System.out.println(">>> 가능한 요일");
			System.out.println(study_weeks);
			
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
//						System.out.println(">> "+at + "_" + s_time+"\t"+isTime);
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
						able_times.add(tmpMap);
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
			
			List<Map<String, Object>> list_times = new ArrayList<Map<String,Object>>();
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
				list_times.add(tmp_map);
			}
			
			System.out.println();
			System.out.println("최종 가능한 시간대");
			System.out.println(list_times);
			
			resultMap.put("able_times", list_times);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}

	// 수업 취소
	@RequestMapping("/userDetail/changeCancelClass")
	public void changeCancelClass(@RequestParam(value="selectedArr[]") List<Integer> classScoreNo) {
		for(int csn : classScoreNo) {
			adminMapper.cancelClassScore(csn);
		}
	}

	// 레벨테스트 취소
	@RequestMapping("/userDetail/changeCancelLeveltest")
	public void changeCancelLeveltest(@RequestParam(value="selectedArr[]") List<Integer> leveltestNo) {
		for(int ln : leveltestNo) {
			adminMapper.cancelLeveltest(ln);
		}
	}
	
	// 보충수업
	@RequestMapping("/userDetail/makeUp")
	public void makeUp(@RequestParam Map<String, Object> paramMap) {
		// modify 하는 class_score 를 취소처리
		adminMapper.cancelClassScore(adminMapper.getClassScoreNo(paramMap));
		
		paramMap.put("class_log_no", adminMapper.selectClassLogPK());
		paramMap.put("class_student_no", adminMapper.getClassStudentNo(paramMap));
		
		adminMapper.makeupClassLog(paramMap);
		adminMapper.makeupClassScore(paramMap);
	}

	@RequestMapping("/userDetail/textbookSelect")
	public List<Map<String, Object>> textbookSelect(@RequestParam Map<String, Object> paramMap) {
		return adminMapper.selectTextbookTopic(paramMap);
	}

	@RequestMapping("/userDetail/progressChange")
	public void progressChange(@RequestParam Map<String, Object> paramMap) {
		adminMapper.changeProgress(paramMap);
		adminMapper.updateClassPhone(paramMap);
	}
	
	@RequestMapping("/userDetail/modifyLevel")
	public void modifyLevel(@RequestParam Map<String, Object> paramMap) {
		adminMapper.updateLeveltestSetTutor(paramMap);
	}
	
	@RequestMapping("/userDetail/deleteClassScore")
	public void deleteClassScore(@RequestParam int class_score_no) {
		adminMapper.deleteClassScore(class_score_no);
	}
	
	@RequestMapping("/userDetail/search_student")
	public List<Map<String, Object>> search_student(@RequestParam Map<String, Object> paramMap){
		return adminMapper.searchUserStudent(paramMap);
	}

	private String addZero(int num) {
		return (num < 10) ? "0" + num : "" + num;
	}
	
	// 강사그룹관리 해당 티칭센터 강사 리스트 
   	@PostMapping(value = "/admin/tutorGroup/getTeacherByCenterNo")   
   	public List<Map<String, Object>> tutorGroup_getTeacherByCenterNo(@RequestParam Map<String, Object> paramMap) throws Exception {
   		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
   		list = adminMapper.selectTeacherByCenterNo(paramMap);   
   		return list;
   	}

   	// 강사그룹관리 해당 강사그룹 강사 리스트 
   	@PostMapping(value = "/admin/tutorGroup/getTeacherByGroupNo")   
   	public List<Map<String, Object>> tutorGroup_getTeacherByGroupNo(@RequestParam Map<String, Object> paramMap) throws Exception {
   		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
   		list = adminMapper.selectTeacherByGroupNo(paramMap);   
   		return list;
   	}

   	// 강사그룹관리 해당 강사그룹 강사추가
   	@PostMapping(value = "/admin/tutorGroup/insertTeacherGroupMember")   
   	public Map<String, Object> tutorGroup_insertTeacherGroupMember(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) throws Exception {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		
   		String[] selectedTeacherArr = null;
   		String[] selectedGroupArr = null;
   		
   		selectedTeacherArr = req.getParameterValues("selectedTeacherArr[]");
   		selectedGroupArr = req.getParameterValues("selectedGroupArr[]");
   		
   		Map<String, Object> param = new HashMap<String, Object>();
   		
   		// 체크된 강사그룹 숫자 만큼 반복
   		for(int i=0; i<selectedGroupArr.length; i++) {
   			// 체크된 티칭센터 강사 숫자만큼 반복
   			for(int k=0; k<selectedTeacherArr.length; k++) {
   				param = new HashMap<String, Object>();
   				param.put("teacher_group_no", selectedGroupArr[i]);
   				param.put("user_teacher_no", selectedTeacherArr[k]);
   				
   				// 중복되는 자료 있는지 확인
   				int duplCnt = adminMapper.getDuplTeacherGroupMember(param);   				
   				if(duplCnt == 0) {	// 중복 아닐 시 insert
   					adminMapper.insertTeacherGroupMember(param);
   				}
   	   		}   			
   		}
   			
   		return resultMap;
   	}
   	
   	// 강사그룹관리 강사그룹 강사 삭제
   	@PostMapping(value = "/admin/tutorGroup/deleteTeacherGroupMember")   
   	public void tutorGroup_deleteTeacherGroupMember(@RequestParam Map<String, Object> paramMap) throws Exception {
   		adminMapper.deleteTeacherGroupMember(paramMap);
   	}
   	
   	// 강사그룹관리 신규 강사그룹 insert
   	@PostMapping(value = "/admin/tutorGroup/insertTeacherGroup")   
   	public Map<String, Object> tutorGroup_insertTeacherGroup(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) throws Exception {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		
   		// 강사 그룹명이 중복되는지 확인
   		int duplCnt = adminMapper.getDuplTeacherGroupName(paramMap);
   		if(duplCnt == 0) {	// 그룹명 중복 아닐 시 insert
   			resultMap.put("result", true);
   			adminMapper.insertTeacherGroup(paramMap);
   		} else {
   			resultMap.put("result", false);
   		}	
   		return resultMap;
   	}   	
   	
   	// 강사그룹관리 그룹명 수정 
   	@PostMapping(value = "/admin/tutorGroup/updateTeacherGroup")   
   	public Map<String, Object> tutorGroup_updateTeacherGroup(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) throws Exception {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		resultMap.put("result", true);
   		int duplCnt = 0;
   		
   		Map<String, Object> teacherGroup = adminMapper.selectTeacherGroupOne(paramMap);
   		// 강사그룹명 변경 시 강사 그룹명이 중복되는지 확인
   		if(!paramMap.get("teacher_group_name").equals(teacherGroup.get("teacher_group_name").toString())) {
   			duplCnt = adminMapper.getDuplTeacherGroupName(paramMap);
   			
   			if(duplCnt == 0) {   	   			
   	   			adminMapper.updateTeacherGroup(paramMap);
   	   		} else {
   	   			resultMap.put("result", false);
   	   		}
   		}
   		 
   		return resultMap;
   	}
   	
   	// 강사그룹관리 그룹 삭제
   	@PostMapping(value = "/admin/tutorGroup/deleteTeacherGroup")   
   	public Map<String, Object> tutorGroup_deleteTeacherGroup(@RequestParam Map<String, Object> paramMap) throws Exception {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		resultMap.put("result", true);
   		
   		// 사이트에 선택되어있는 강사그룹: 삭제 안됨
   		List<Map<String, Object>> usedSiteList = adminMapper.selectUsedTeacherGroupBySite(paramMap);
   		String usedSiteName = "";
   		
   		// 사이트에 연결되어있지 않다면 삭제
   		if(usedSiteList.size() == 0) {   			
   			adminMapper.deleteTeacherGroup(paramMap);
   		} else {
   			for(int i=0; i<usedSiteList.size(); i++) {
   				if(i != usedSiteList.size()-1) {
   					usedSiteName += usedSiteList.get(i).get("site_name")+ ", ";
   				} else {
   					usedSiteName += usedSiteList.get(i).get("site_name");
   				}
   			}
   			
   			resultMap.put("result", false);
   			resultMap.put("resultMsg", "삭제 실패.\n해당 강사그룹이 사용되고있는 사이트가 있습니다.\n( "+usedSiteName + " )");
   		}
   		
   		return resultMap;
   	}
   	
   	@PostMapping(value = "/reportPopup/getTopicPdf")   
   	public Map<String, Object> reportPopup_getTopicPdf(@RequestParam Map<String, Object> paramMap) throws Exception {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		resultMap.put("result", true);
   		
   		System.out.println(paramMap);
   		
   		Map<String, Object> topic = adminMapper.selectTextbookTopicOne(paramMap);
   		if(topic.containsKey("pdf_file")) {
   			resultMap.put("pdf_file", topic.get("pdf_file"));
   		} else {
   			resultMap.put("pdf_file", "");
   		}

   		
   		
   		
   		   			
   			
   		//resultMap.put("result", false);
   		//resultMap.put("resultMsg", "삭제 실패.\n해당 강사그룹이 사용되고있는 사이트가 있습니다.\n( "+usedSiteName + " )");
   		
   		
   		
   		//
   		
   		return resultMap;
   	}
   	
   	// 사이트 정보조회 - 사이트명 중복여부
   	@RequestMapping("/admin/site/duplSiteChk")
	public Map<String, Object> site_duplChk(@RequestParam Map<String, Object> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("duplCnt", adminMapper.getDuplSiteName(paramMap));
		return result;
	}
   	
   	// 회원상세보기팝업 - 비밀번호 초기화
   	@RequestMapping("/userInfo/userPwdClear")
	public void userPwdClear(@RequestParam Map<String, Object> paramMap) {
   		String pwd = paramMap.get("id").toString();
		String encrypted = BCrypt.hashpw(pwd, BCrypt.gensalt());
		paramMap.put("pwd", encrypted);
   		
   		adminMapper.userPwdClear(paramMap);
	}
   	
   	// 회원상세보기팝업 - 특이사항 상세보기 팝업 - etc 수정 
   	@RequestMapping("/userInfo/updateEtc")
	public void updateEtc(@RequestParam Map<String, Object> paramMap) {
   		adminMapper.updateEtc(paramMap);
	}
   	
   	// 수업기본정보 교육과정 검색 셀렉트박스 (언어 선택별 where)
   	@PostMapping(value = "/admin/classInfo/getStudyComboByLanguage")
	public List<Map<String, Object>> admin_classInfo_getStudyComboByLanguage(@RequestParam Map<String, Object> paramMap)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.getStudyComboByLanguage(paramMap);
		return list;
	}
   	
   	// 수업기본정보 수강상품 검색 셀렉트박스 (언어 선택별 where)
   	@PostMapping(value = "/admin/classInfo/getProductComboByLanguage")
	public List<Map<String, Object>> admin_classInfo_getProductComboByLanguage(@RequestParam Map<String, Object> paramMap)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.getProductCombo(paramMap);
		return list;
	}
   	
   	// 수업기본정보 학습교재 검색 셀렉트박스 (교육과정 선택별 where)
   	@PostMapping(value = "/admin/classInfo/getTextbookComboByStudy")
	public List<Map<String, Object>> admin_classInfo_getTextbookComboByStudy(@RequestParam Map<String, Object> paramMap)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.getTextbookCombo(paramMap);
		return list;
	}   	
   	
   	// 수업기본정보 강사 검색 셀렉트박스 (언어별 where)
	@PostMapping(value = "/admin/classInfo/getTeacherComboByLanguage")
	public List<Map<String, Object>> admin_classInfo_getTeacherComboByLanguage(@RequestParam Map<String, Object> paramMap)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.getTeacherComboByLanguage(paramMap);
		return list;
	}
	
	// 수업날짜 일괄변경 관련 강사 셀렉트박스
	@PostMapping(value = "/admin/modifyClassDate/getTeacherCombo")
	public List<Map<String, Object>> admin_modifyClassDate_getTeacherCombo(@RequestParam Map<String, Object> paramMap) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = adminMapper.getTeacherCombo(paramMap);
		return list;
	}
	
	// 수업날짜 일괄변경 changing 버튼 기능
	@Transactional(rollbackFor = Exception.class)
	@PostMapping(value = "/admin/modifyClassDate/changing")
	public Map<String, Object> admin_modifyClassDate_changing(@RequestParam Map<String, Object> paramMap, HttpServletResponse res, HttpServletRequest req) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String choosedDate = paramMap.get("choosedDate").toString();
		// System.out.println("choosedDate: " + paramMap.get("choosedDate"));
		String result = "0";		// 성공여부
		String resultMsg = "";
		
		String[] classLogNoArr = null;		
		classLogNoArr = req.getParameterValues("classLogNoArr[]");		
		
		List<String> classLogListOrigin = new ArrayList<String>();
		List<String> classLogList = new ArrayList<String>();		// 중복 제거 후 classLog list
		
		// 변경 하려는 class_log		
		for (int i = 0; i < classLogNoArr.length; i++) {
			if(!classLogNoArr[i].toString().equals("0")) classLogListOrigin.add(classLogNoArr[i].toString());
		}
		
		// 0 제외(leveltest 내용)한 classlog
		if(classLogListOrigin.size() > 0) {
			// HashSet 중복 제거
			HashSet<String> arr = new HashSet<String>(classLogListOrigin);
			classLogList = new ArrayList<String>(arr); // 중복 제거 후 user_no List
		}		
				
		
		Map<String, Object> classLog = new HashMap<String, Object>();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("choosedDate", paramMap.get("choosedDate"));
		
		// class_log_no 0 제거, 중복 제거 후 변경 할 classLogList
		if(classLogList.size() > 0) {		
			try {
				for(String classLogNo : classLogList) {
					classLog = adminMapper.getClassLogInfo(classLogNo); 		// {user_teacher_no=1, language=English, class_log_no=4, class_no=1, start_time, product_name}
					param.put("language", classLog.get("language"));
					param.put("user_teacher_no", classLog.get("user_teacher_no"));
					param.put("class_log_no", classLogNo);
					
					// 1) 변경하려는 날짜가 휴강일(언어별)인지 체크 
					int holidayCnt = adminMapper.getHolidayCnt(param);
					if(holidayCnt > 0) {
						resultMsg += "변경하려는 날짜에 휴강일(공통)이 겹치는 수업이 존재합니다.\n( course: "+ classLog.get("product_name") + " )";
						throw new Exception();
					}
										
					// 2) 변경하려는 날짜가 해당 수업 강사 휴강일 인지 체크				
					int teacherHolidayCnt = adminMapper.getTeacherHolidayCnt(param);
					if(teacherHolidayCnt > 0) {
						resultMsg += "변경하려는 날짜에 강사 휴강일이 겹치는 수업이 존재합니다.\n( course: "+ classLog.get("product_name") + " )";
						throw new Exception();
					}			
										
					// 3) 변경하려는 날짜가 해당수업 강사 다른 수업일과 겹친다면 강사 수업시간 겹치는지 체크					
					List<Map<String, Object>> lessonList = adminMapper.selectLessonList(param);
					int duplTimeCnt = 0;
					
					double classLogStartMin = Double.parseDouble(classLog.get("startMin").toString());
					double classLogEndMin = Double.parseDouble(classLog.get("endMin").toString());
					
					double lessonStartMin = 0.0;
					double lessonEndMin = 0.0;
					
					
					if(lessonList.size() > 0) {
						for(Map<String, Object> lesson : lessonList) {
							lessonStartMin = Double.parseDouble(lesson.get("startMin").toString());
							lessonEndMin = Double.parseDouble(lesson.get("endMin").toString());
							
							// 수업시간이 겹친다면
							if ((classLogStartMin <= lessonStartMin && classLogEndMin > lessonStartMin) || (classLogStartMin < lessonEndMin && classLogEndMin >= lessonEndMin)) {
								duplTimeCnt++;
							}
						}
					}
					
					if(duplTimeCnt > 0) {						
						resultMsg += "변경하려는 날짜에 강사 수업시간이 겹치는 수업이 존재합니다.\n( course: "+ classLog.get("product_name") + " )";
						throw new Exception();
					}
					
					
					// 4) 변경하려는 날짜에 해당 수업을 듣는 학생의 수업일과 겹친다면 학생 수업시간 겹치는지 체크
					List<Map<String, Object>> studentList = adminMapper.selectStudentList(param);
					int studentDuplTimeCnt = 0;
					
					if(studentList.size() > 0) {
						for(Map<String, Object> student : studentList) {
							param.put("user_student_no", student.get("user_student_no"));
							
							// 해당 학생 기존 수업들 체크
							List<Map<String, Object>> studentLessonList = adminMapper.selectStudentLessonList(param);
							
							for(Map<String, Object> lesson : studentLessonList) {
								lessonStartMin = Double.parseDouble(lesson.get("startMin").toString());
								lessonEndMin = Double.parseDouble(lesson.get("endMin").toString());
								
								// 수업시간이 겹친다면
								if ((classLogStartMin <= lessonStartMin && classLogEndMin > lessonStartMin) || (classLogStartMin < lessonEndMin && classLogEndMin >= lessonEndMin)) {
									studentDuplTimeCnt++;
								}
							}
							
						}						
					}
					
					if(studentDuplTimeCnt > 0) {						
						resultMsg += "변경하려는 날짜에 학생 수업시간이 겹치는 수업이 존재합니다.\n( course: "+ classLog.get("product_name") + " )";
						throw new Exception();
					}
					
					// 5) 1~4 겹치는 조건이 없다면 class_log update 
					adminMapper.updateClassLogByClassDate(param);
				}
				
			} catch(Exception e) {				
				System.out.println("classLog changing Exception!");				
				//e.printStackTrace();	
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();	// 롤백
				
				resultMap.put("result", "0");
				resultMap.put("resultMsg", resultMsg);
				return resultMap;
			}
		}
		
		
		
		// 레벨테스트 검증		
		String[] leveltestNoArr = null;
		leveltestNoArr = req.getParameterValues("leveltestNoArr[]");
		
		List<String> leveltestListOrigin = new ArrayList<String>();
		List<String> leveltestList = new ArrayList<String>();		// 중복 제거 후 classLog list
		
		for (int i = 0; i < leveltestNoArr.length; i++) {
			if(!leveltestNoArr[i].toString().equals("0")) leveltestListOrigin.add(leveltestNoArr[i].toString());
		}
		
		// 0 제외(classLog 내용)한 leveltest
		if(leveltestListOrigin.size() > 0) {
			// HashSet 중복 제거
			HashSet<String> arr2 = new HashSet<String>(leveltestListOrigin);
			leveltestList = new ArrayList<String>(arr2); // 중복 제거 후 user_no List
		}	
		
				
		Map<String, Object> leveltest = new HashMap<String, Object>();
		param = new HashMap<String, Object>();
		param.put("choosedDate", paramMap.get("choosedDate"));
		
		// leveltest_no 0 제거, 중복 제거 후 변경 할 leveltestList
		if(leveltestList.size() > 0) {
			try {
				for(String leveltestNo : leveltestList) {
					leveltest = adminMapper.getLeveltestInfo(leveltestNo); 		// {user_teacher_no=1, language=English, class_log_no=4, class_no=1, start_time, product_name}
					param.put("language", leveltest.get("language"));
					param.put("user_teacher_no", leveltest.get("user_teacher_no"));
					param.put("user_student_no", leveltest.get("user_student_no"));
					param.put("leveltest_no", leveltestNo);
					
					// 1) 변경하려는 날짜가 휴강일(언어별)인지 체크
					int holidayCnt = adminMapper.getHolidayCnt(param);
					if(holidayCnt > 0) {
						resultMsg += "변경하려는 날짜에 휴강일(공통)이 겹치는 레벨테스트가 존재합니다.\n( course: "+ leveltest.get("product_name") 
						            + ", " + leveltest.get("start_time") + " )";									
						throw new Exception();
					}
					
					// 2) 변경하려는 날짜가 해당 수업 강사 휴강일 인지 체크				
					int teacherHolidayCnt = adminMapper.getTeacherHolidayCnt(param);
					if(teacherHolidayCnt > 0) {
						resultMsg += "변경하려는 날짜에 강사 휴강일이 겹치는 레벨테스트가 존재합니다.\n( course: "+ leveltest.get("product_name")
								  + ", " + leveltest.get("start_time") + " )";
						throw new Exception();
					}
					
					
					// 3) 변경하려는 날짜가 해당수업 강사 다른 수업일과 겹친다면 강사 수업시간 겹치는지 체크					
					List<Map<String, Object>> lessonList = adminMapper.selectLessonList(param);
					int duplTimeCnt = 0;
					
					double leveltestStartMin = Double.parseDouble(leveltest.get("startMin").toString());
					double leveltestEndMin = Double.parseDouble(leveltest.get("endMin").toString());
					
					double lessonStartMin = 0.0;
					double lessonEndMin = 0.0;
					
					
					if(lessonList.size() > 0) {
						for(Map<String, Object> lesson : lessonList) {
							lessonStartMin = Double.parseDouble(lesson.get("startMin").toString());
							lessonEndMin = Double.parseDouble(lesson.get("endMin").toString());
							
							// 수업시간이 겹친다면
							if((leveltestStartMin >= lessonStartMin && leveltestStartMin < lessonEndMin) || (leveltestEndMin >= lessonStartMin && leveltestEndMin < lessonEndMin)) {
								duplTimeCnt++;
							}
						}
					}
					
					if(duplTimeCnt > 0) {						
						resultMsg += "변경하려는 날짜에 강사 수업시간이 겹치는 수업이 존재합니다.\n( course: "+ leveltest.get("product_name")
								     + ", " + leveltest.get("start_time") + " )";
						throw new Exception();
					}
					
					
					// 4) 변경하려는 날짜에 해당 수업을 듣는 학생의 수업일과 겹친다면 학생 수업시간 겹치는지 체크
					int studentDuplTimeCnt = 0;
					// 해당 학생 기존 수업들 체크
					List<Map<String, Object>> studentLessonList = adminMapper.selectStudentLessonList(param);
					
					for(Map<String, Object> lesson : studentLessonList) {
						lessonStartMin = Double.parseDouble(lesson.get("startMin").toString());
						lessonEndMin = Double.parseDouble(lesson.get("endMin").toString());
						
						// 수업시간이 겹친다면
						if((leveltestStartMin >= lessonStartMin && leveltestStartMin < lessonEndMin) || (leveltestEndMin >= lessonStartMin && leveltestEndMin < lessonEndMin)) {
							studentDuplTimeCnt++;
						}
					}
					
					if(studentDuplTimeCnt > 0) {						
						resultMsg += "변경하려는 날짜에 학생 수업시간이 겹치는 수업이 존재합니다.\n( course: "+ leveltest.get("product_name")
					     			+ ", " + leveltest.get("start_time") + " )";
						throw new Exception();
					}
										
					// 5) 1~4 겹치는 조건이 없다면 leveltest update 
					adminMapper.updateLeveltestByClassDate(param);
					
				}
				
				
			} catch (Exception e) {
				System.out.println("leveltest changing Exception!");				
				//e.printStackTrace();	
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();	// 롤백
				
				resultMap.put("result", "0");
				resultMap.put("resultMsg", resultMsg);
				return resultMap;				
			}
			
		}
		
		
		
		// 에러발생 없이 등록 완료
		resultMap.put("result", "1");
		resultMap.put("resultMsg", "변경되었습니다.");
		return resultMap;
		
		
	}
	
	
	
	
	// 수업날짜 일괄변경 cancel 버튼 기능	
	@PostMapping(value = "/admin/modifyClassDate/cancel")
	public Map<String, Object> admin_modifyClassDate_cancel(@RequestParam Map<String, Object> paramMap, HttpServletResponse res, HttpServletRequest req) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String[] classLogNoArr = null;
		classLogNoArr = req.getParameterValues("classLogNoArr[]");
		
		List<String> classLogListOrigin = new ArrayList<String>();
		List<String> classLogList = new ArrayList<String>();		// 중복 제거 후 classLog list
		
		// 변경 하려는 class_log		
		for (int i = 0; i < classLogNoArr.length; i++) {
			if(!classLogNoArr[i].toString().equals("0")) classLogListOrigin.add(classLogNoArr[i].toString());
		}
		
		// 0 제외(leveltest 내용)한 classlog
		if(classLogListOrigin.size() > 0) {
			// HashSet 중복 제거
			HashSet<String> arr = new HashSet<String>(classLogListOrigin);
			classLogList = new ArrayList<String>(arr); // 중복 제거 후 user_no List
		}
		
		Map<String, Object> param = new HashMap<String, Object>();
		
		// class_log_no 0 제거, 중복 제거 후 변경 할 classLogList
		if(classLogList.size() > 0) {		
			for(String classLogNo : classLogList) {
				param.put("class_log_no", classLogNo);		
				adminMapper.cancelClassLog(param);	// 취소처리
			}
		}
		
		
				
		// 레벨테스트 cancel
		param = new HashMap<String, Object>();
		
		String[] leveltestNoArr = null;
		leveltestNoArr = req.getParameterValues("leveltestNoArr[]");
		
		List<String> leveltestListOrigin = new ArrayList<String>();
		List<String> leveltestList = new ArrayList<String>();		// 중복 제거 후 classLog list
		
		for (int i = 0; i < leveltestNoArr.length; i++) {
			if(!leveltestNoArr[i].toString().equals("0")) leveltestListOrigin.add(leveltestNoArr[i].toString());
		}
		
		// 0 제외(classLog 내용)한 leveltest
		if(leveltestListOrigin.size() > 0) {
			// HashSet 중복 제거
			HashSet<String> arr2 = new HashSet<String>(leveltestListOrigin);
			leveltestList = new ArrayList<String>(arr2); // 중복 제거 후 user_no List
		}	
		
		if(leveltestList.size() > 0) {
			for(String leveltestNo : leveltestList) {
				param.put("leveltest_no", leveltestNo);		
				adminMapper.cancelLeveltestByClassDate(param);	// 취소처리				
			}
		}
		
		
		resultMap.put("result", "1");
		resultMap.put("resultMsg", "cancel 처리되었습니다.");
		
		return resultMap;
	}
	
	
	
	// 체크선택한 회원 SMS 보내기 - SureM API 문자발송 테스트 
	@RequestMapping("/sms/checkedSend")
	public void checked_smsSendSureM(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) throws Exception {
		System.out.println(paramMap);
		// {selectedArr=15, type=sms, text=테스트입니다, reserve_dt=2021.08.23, reserve_hour=, reserve_min=, send_number=070-4082-4063, reserve_flag=0}
				
		String[] userNoArr = paramMap.get("selectedArr").toString().split(",");

		List<String> listArr = new ArrayList<String>();

		for (int i = 0; i < userNoArr.length; i++) {
			listArr.add(userNoArr[i]);
		}

		// SMS 팝업 open시 체크된 user 중복 제거
		// HashSet 중복 제거
		HashSet<String> arr2 = new HashSet<String>(listArr);
		List<String> userStudentNoArr = new ArrayList<String>(arr2); // 중복 제거 후 user_no List		

		Map<String, Object> smsMap = new HashMap<String, Object>();

		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat time_format = new SimpleDateFormat("HH:mm:ss");

		Date today = new Date();

		String send_date = date_format.format(today); // SMS 내역 날짜 저장용
		String send_time = time_format.format(today); // SMS 내역 시간 저장용

		// SMS 전송
		if ("sms".equals(paramMap.get("type").toString())) {
			for (int i = 0; i < userStudentNoArr.size(); i++) {
				System.out.println("API 테스트중 ★★★★★★★★★★   밑에 paramMap");
				System.out.println(paramMap);
				
				Map<String, Object> user = adminMapper.getUserByUserStudentNo(userStudentNoArr.get(i)); // log 테이블 저장할 때 사용할 user Map

				smsMap.put("text", paramMap.get("text")); // 내용
				smsMap.put("send_number", paramMap.get("send_number")); // 발신번호
				smsMap.put("receipt_number", user.get("student_phone")); // 수신자번호

				// sms_log 테이블 저장 정보
				smsMap.put("user_student_no", user.get("user_student_no"));
				smsMap.put("send_date", send_date);
				smsMap.put("send_time", send_time);

				smsMap.put("type", "SMS"); // 전송 방식

				smsMap.put("reserve_yn", paramMap.get("reserve_flag")); // 예약발송 사용여부 '1','0'
				smsMap.put("reserve_dt", paramMap.get("reserve_dt").toString().replace(".", "").replace("-", ""));
				smsMap.put("reserve_hour", paramMap.get("reserve_hour"));
				smsMap.put("reserve_min", paramMap.get("reserve_min"));
				
				
				System.out.println("API 테스트중 ★★★★★★★★★★   밑에 smsMap");
				System.out.println(smsMap);
				// {user_student_no=7, receipt_number=01064556867, send_time=19:29:23, reserve_dt=20210823, send_date=2021-08-23, reserve_hour=, text=테스트 입니다., send_number=070-4082-4063, reserve_min=, type=SMS, reserve_yn=0}
				
				String usercode = "saehaens1";
				String deptcode = "K7--RF-84";
				//String userpass = "5031986";	// 사용안함
				
				Map<String, Object> sureParam = new HashMap<String, Object>();
				sureParam.put("usercode", usercode);				
				sureParam.put("deptcode", deptcode);
				
				List<Map<String, Object>> msgs = new ArrayList<Map<String, Object>>();
				Map<String, Object> msg = new HashMap<String, Object>();
				msg.put("to", smsMap.get("receipt_number"));
				
				msgs.add(msg);
				
				
				sureParam.put("messages", msgs);
				sureParam.put("text", smsMap.get("text"));
				sureParam.put("from", smsMap.get("send_number").toString().replace("-", ""));		// 18993430
				
				// 예약발송 +
				if ("1".equals(smsMap.get("reserve_yn").toString())) {	// 예약발송 체크상태라면
					String reserve_time = paramMap.get("reserve_dt").toString().replace(".", "").replace("-", "") + paramMap.get("reserve_hour").toString() + paramMap.get("reserve_min").toString();							
					sureParam.put("reserved_time", reserve_time);	// 예약전송시 날짜, 시간 설정
				}
				
				ObjectMapper mapper = new ObjectMapper();
				String jsonValue = mapper.writeValueAsString(sureParam);								
				
				System.out.println("jsonValue : ");
				System.out.println(jsonValue);		
				
				/*
				{ "deptcode":"K7--RF-84"
				 ,"messages":[{"to":"01064556867"}]
				 ,"from":"18993430"
				 ,"text":"테스트입니다"
				 ,"usercode":"saehaens1"
				 }  
				*/
							
				
				String inputLine = null; 
				StringBuffer outResult = new StringBuffer();
				try{ URL url = new URL("https://rest.surem.com/sms/v1/json");			// SMS,LMS에 따라 url    /sms/ /lms/ 다름 
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:221.0) Gecko/20100101 Firefox/31.0"); // add this line to your code
					conn.setDoOutput(true);
					conn.setRequestMethod("POST");					
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("Accept-Charset", "UTF-8");
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(10000);
					
					OutputStream os = conn.getOutputStream();
					os.write(jsonValue.getBytes("UTF-8"));
					os.flush(); 
					os.close();
					
					
					// 리턴된 결과 읽기 					
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					while ((inputLine = in.readLine()) != null) {
						outResult.append(inputLine); 
					}
					
					//conn.disconnect(); 
					in.close();
					
					System.out.println("리턴된 결과 ############");
					System.out.println(outResult.toString()); 
					
					// 리턴 json응답 맵으로 변환
					HashMap<String,Object> returnMap = new ObjectMapper().readValue(outResult.toString(), HashMap.class);
					//System.out.println("리턴 json 응답 맵으로 변환 ############");
					//System.out.println(returnMap);					
					
					Map<String, Object> logMap = new HashMap<String, Object>();
					logMap.put("user_student_no", userStudentNoArr.get(i));
					logMap.put("content", smsMap.get("text"));
					logMap.put("send_date", smsMap.get("send_date"));					// 발송일
					logMap.put("send_time", smsMap.get("send_time"));					// 발송시간
					
					logMap.put("send_number", smsMap.get("send_number"));			// 발신번호	
					logMap.put("receipt_number", smsMap.get("receipt_number"));		// 수신자번호
					logMap.put("type", smsMap.get("type"));							// 전송방식 SMS,LMS
					
					logMap.put("reserve_yn", smsMap.get("reserve_yn").toString());	// 예약 발송 체크 여부
					logMap.put("reserve_time", sureParam.get("reserved_time"));		// 예약 발송 시간
					
					// 전송 성공이라면..
					if("200".equals(returnMap.get("code").toString())) {
						logMap.put("status", "1");
						logMap.put("result_msg", "성공");
					} else {
						logMap.put("status", "0");
						logMap.put("result_msg", returnMap.get("message"));
					}
					
					//System.out.println("문자내역 logMap : #########################################"); 
					//System.out.println(logMap);
					adminMapper.insertSmslog(logMap);
					
					System.out.println("REST API End"); 
				}catch(Exception e){
					e.printStackTrace(); 
				}		
				
			}

		} else {	// LMS 전송

			for (int i = 0; i < userStudentNoArr.size(); i++) {
				System.out.println("API 테스트중 ★★★★★★★★★★   밑에 paramMap");
				System.out.println(paramMap);
				
				Map<String, Object> user = adminMapper.getUserByUserStudentNo(userStudentNoArr.get(i)); // log 테이블 저장할 때 사용할 user Map

				smsMap.put("text", paramMap.get("text")); // 내용
				smsMap.put("send_number", paramMap.get("send_number")); // 발신번호
				smsMap.put("receipt_number", user.get("student_phone")); // 수신자번호

				// sms_log 테이블 저장 정보
				smsMap.put("user_student_no", user.get("user_student_no"));
				smsMap.put("send_date", send_date);
				smsMap.put("send_time", send_time);

				smsMap.put("type", "LMS"); // 전송 방식

				smsMap.put("reserve_yn", paramMap.get("reserve_flag")); // 예약발송 사용여부 '1','0'
				smsMap.put("reserve_dt", paramMap.get("reserve_dt").toString().replace(".", "").replace("-", ""));
				smsMap.put("reserve_hour", paramMap.get("reserve_hour"));
				smsMap.put("reserve_min", paramMap.get("reserve_min"));				
				
				System.out.println("API 테스트중 ★★★★★★★★★★   밑에 smsMap");
				System.out.println(smsMap);
				
				String usercode = "saehaens1";
				String deptcode = "K7--RF-84";				
				
				Map<String, Object> sureParam = new HashMap<String, Object>();
				sureParam.put("usercode", usercode);				
				sureParam.put("deptcode", deptcode);
				
				List<Map<String, Object>> msgs = new ArrayList<Map<String, Object>>();
				Map<String, Object> msg = new HashMap<String, Object>();
				msg.put("to", smsMap.get("receipt_number"));
				
				msgs.add(msg);				
				
				sureParam.put("messages", msgs);
				sureParam.put("text", smsMap.get("text"));
				sureParam.put("from", smsMap.get("send_number").toString().replace("-", ""));		// 18993430
				
				// 예약발송 +
				if ("1".equals(smsMap.get("reserve_yn").toString())) {	// 예약발송 체크상태라면
					String reserve_time = paramMap.get("reserve_dt").toString().replace(".", "").replace("-", "") + paramMap.get("reserve_hour").toString() + paramMap.get("reserve_min").toString();							
					sureParam.put("reserved_time", reserve_time);	// 예약전송시 날짜, 시간 설정
				}
				
				ObjectMapper mapper = new ObjectMapper();
				String jsonValue = mapper.writeValueAsString(sureParam);								
				
				System.out.println("jsonValue : ");
				System.out.println(jsonValue);		
				
				
				String inputLine = null; 
				StringBuffer outResult = new StringBuffer();
				try{ URL url = new URL("https://rest.surem.com/lms/v1/json");			// SMS,LMS에 따라 url    /sms/ /lms/ 다름 
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:221.0) Gecko/20100101 Firefox/31.0"); // add this line to your code
					conn.setDoOutput(true);
					conn.setRequestMethod("POST");					
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("Accept-Charset", "UTF-8");
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(10000);
					
					OutputStream os = conn.getOutputStream();
					os.write(jsonValue.getBytes("UTF-8"));
					os.flush(); 
					os.close();
					
					
					// 리턴된 결과 읽기 					
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					while ((inputLine = in.readLine()) != null) {
						outResult.append(inputLine); 
					}
					 
					in.close();
					
					System.out.println("리턴된 결과 ############");
					System.out.println(outResult.toString()); 
					
					// 리턴 json응답 맵으로 변환
					HashMap<String,Object> returnMap = new ObjectMapper().readValue(outResult.toString(), HashMap.class);
					
					Map<String, Object> logMap = new HashMap<String, Object>();
					logMap.put("user_student_no", userStudentNoArr.get(i));
					logMap.put("content", smsMap.get("text"));
					logMap.put("send_date", smsMap.get("send_date"));					// 발송일
					logMap.put("send_time", smsMap.get("send_time"));					// 발송시간
					
					logMap.put("send_number", smsMap.get("send_number"));			// 발신번호	
					logMap.put("receipt_number", smsMap.get("receipt_number"));		// 수신자번호
					logMap.put("type", smsMap.get("type"));							// 전송방식 SMS,LMS
					
					logMap.put("reserve_yn", smsMap.get("reserve_yn").toString());	// 예약 발송 체크 여부
					logMap.put("reserve_time", sureParam.get("reserved_time"));		// 예약 발송 시간
					
					// 전송 성공이라면..
					if("200".equals(returnMap.get("code").toString())) {
						logMap.put("status", "1");
						logMap.put("result_msg", "성공");
					} else {
						logMap.put("status", "0");
						logMap.put("result_msg", returnMap.get("message"));
					}
					
					adminMapper.insertSmslog(logMap);
					
					System.out.println("REST API End"); 
				}catch(Exception e){
					e.printStackTrace(); 
				}		
				
			}
		}
		
		
	}
	
	
	
}
