package com.edutem.lms.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.thymeleaf.util.MapUtils;

import com.edutem.lms.mapper.AdminMapper;
import com.edutem.lms.component.Common;
import com.edutem.lms.component.S3Wrapper;

@RestController
public class SaehaLmsRestController {
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private S3Wrapper s3Wrapper;
	
	
	
	// 강사 - 레벨테스트 수정  
   	@PostMapping(value = "/teacher/lesson/levelTestLog/update")   
   	public void teacher_levelTestLog_update(@RequestParam Map<String, Object> paramMap) throws Exception {  		
   		adminMapper.updateLevelTestLogByTeacher(paramMap);		
   	}
   	
   	// 공지사항 insert	
  	@RequestMapping("/admin/notice/insertNotice")
  	public void noticeInsert(@RequestParam Map<String, Object> paramMap, HttpSession session) {		
  		//System.out.println(paramMap); 		 	
  		paramMap.put("reg_company", session.getAttribute("company_code"));
  		adminMapper.insertNotice(paramMap);  		
  	}
  	
  	// 공지사항 update	
   	@RequestMapping("/admin/notice/updateNotice")
   	public void noticeUpdate(@RequestParam Map<String, Object> paramMap) {		
   		//System.out.println(paramMap); 		 		
   		adminMapper.updateNotice(paramMap);  		
   	}
   	
   	// 공지사항 delete
   	@RequestMapping("/admin/notice/deleteNotice")
   	public void noticeDelete(@RequestParam Map<String, Object> paramMap) {   		 		 		
   		adminMapper.deleteNotice(paramMap);  		
   	}
   	

   	
   	// 강사관리자 @@@@@@@@@@@
   	// 강사등록 insert 
   	@RequestMapping("/center/teacher/insert")
   	public void teacherInsert(@RequestParam Map<String, Object> paramMap, MultipartFile mfile, HttpSession session) {
   		String company_code = session.getAttribute("company_code").toString();   		
   		int userNo = adminMapper.selectLastAIUser();	// 생성될 유저번호
   		
   		String fileName = "PR_"+company_code+"_"+ userNo + ".jpg"; 		// 이미지 파일 이름 채번 : PR_COMPANYCODE_userno.jpg
		
		String awsUrl = "https://edutemlms.s3.ap-northeast-2.amazonaws.com/profile/";
		String img_url = "";
		
		
		paramMap.put("profile_img", ""); 
		
   		
   		// 파일찾기로 가져온 파일이 있다면
   		if (!mfile.isEmpty()) {
   			img_url = awsUrl + fileName;		// db에 저장할 이미지 url
   			paramMap.put("profile_img", img_url);
   			
   			try {
   				adminMapper.insertTeacher(paramMap); 
   				s3Wrapper.upload(mfile, "profile", fileName);
   			} catch (Exception e) {
   				e.printStackTrace();
   			}	
   			
   		} else {
   			paramMap.put("profile_img", img_url);
   			adminMapper.insertTeacher(paramMap);
   		}
   		
   		
   		
   		  		
   	}	
   	
   	
   	// 강사 - 수업현황 - 유저 영어이름 등록/수정
   	@RequestMapping("/teacher/lesson/engName/update")
   	public void teacher_student_engNameUpdate(@RequestParam Map<String, Object> paramMap) {
   		adminMapper.updateUserEnglishName(paramMap);  	// param: no(user_no), nickname	
   	}
   	
   	// 무료 레벨테스트 강사배정 insert	- level_test_log 테이블 insert
   	@RequestMapping("/center/levelTestAssign/insert")
   	public void levelTestAssignInsert(@RequestParam Map<String, Object> paramMap) {   		 		 		
   		adminMapper.insertLevelTestAssign(paramMap);  		
   	}
   	
   	// 무료 레벨테스트 강사배정 update
   	@RequestMapping("/center/levelTestAssign/update")
   	public void levelTestAssignUpdate(@RequestParam Map<String, Object> paramMap) {
   		adminMapper.updateLevelTestAssign(paramMap);  		
   	}
   	
   	
   	// 유료수강자 강사배정 전 과정셀렉트박스 과정명으로 class_category No 가져오기.. 해당 과정명으로 db에 데이터 없다면 alert 
   	@RequestMapping("/center/classAssign/getClassCategoryNo")
   	public Map<String, Object> classAssign_getClassCategoryNo(@RequestParam Map<String, Object> paramMap, HttpSession session) {
   		//System.out.println(paramMap);   		
   		paramMap.put("company_code", session.getAttribute("company_code").toString());
   		   		
   		Map<String, Object> classCategory = adminMapper.getClassCategoryNo(paramMap);
   		
   		if (MapUtils.isEmpty(classCategory)) {
   			classCategory = new HashMap<String, Object>();
   			classCategory.put("no", 0);
   		} 		
   		
   		
   		return classCategory;
   	}
   	
   	
   	// 유료수강자 강사배정 insert	- class, class_log 테이블
   	@RequestMapping("/center/classAssign/insert")
   	public void classAssignInsert(@RequestParam Map<String, Object> paramMap) {
   		//System.out.println(paramMap);
   		
   		// class 테이블 insert
   		adminMapper.insertClass(paramMap);
   		
   		
   		// class_log 테이블 insert 파라미터   		
   		Map<String, Object> logParam = new HashMap<String, Object>();
   		
   		int class_no = adminMapper.selectLastAIClass() - 1;		// class_no (FK) 가져오기
   		String teacher_no = paramMap.get("teacher_no").toString();
   		String class_time = paramMap.get("pick_time1").toString();
   		
   		logParam.put("class_no", class_no);
   		logParam.put("teacher_no", teacher_no);
   		logParam.put("class_time", class_time);	
   		
   		
   		
   		
   		// class_log 테이블 생성할 class_date Array
   		String[] logDateArr = paramMap.get("logDateArr").toString().split(",");   		
   		
   		for(int i=0; i<logDateArr.length; i++) {  			
   			//System.out.println(logDateArr[i]);   			
   			logParam.put("class_date", logDateArr[i]);
   			
   			// class_log 테이블 insert
//   	   		adminMapper.insertClassLog(logParam);   			
   		}  		
   		  		
   	}
   	
   	
	// 유료수강자 강사배정 insert	- class, class_log 테이블    
   	@RequestMapping("/center/classAssign/insertByTempSaveAfter")
   	public void classAssignInsertByTempSaveAfter(@RequestParam Map<String, Object> paramMap) {
   		System.out.println(paramMap);   		
   	
   		// class 테이블 update		// status도 1로 변경   		
   		adminMapper.updateClass(paramMap);   		
   		
   		// class_log 테이블 insert 파라미터   		
   		Map<String, Object> logParam = new HashMap<String, Object>();
   		
   		int class_no = Integer.parseInt(paramMap.get("class_no").toString());
   		String teacher_no = paramMap.get("teacher_no").toString();
   		String class_time = paramMap.get("pick_time1").toString();
   		
   		logParam.put("class_no", class_no);
   		logParam.put("teacher_no", teacher_no);
   		logParam.put("class_time", class_time);	
   		
   		// class_log 테이블 생성할 class_date Array
   		String[] logDateArr = paramMap.get("logDateArr").toString().split(",");   		
   		
   		for(int i=0; i<logDateArr.length; i++) {  			
   			//System.out.println(logDateArr[i]);   			
   			logParam.put("class_date", logDateArr[i]);
   			
   			// class_log 테이블 insert
//   	   		adminMapper.insertClassLog(logParam);   			
   		}  		
   			
   	}
   	
   	
   	// 유료수강자 강사배정 임시저장
   	@RequestMapping("/center/classAssign/tempSave")
   	public void classAssignTempSave(@RequestParam Map<String, Object> paramMap) {
   		System.out.println(paramMap);
   		
   		// 신규저장
   		if ("-1".equals(paramMap.get("saveStat"))) {
   			adminMapper.tempSaveClass(paramMap);   			
   			
   		} else {	// 0 임시 저장상태에서 한번 더 임시저장 : update   			
   			adminMapper.tempUpdateClass(paramMap);
   		} 		
   	}
   	
   	
   	// 유료수강자 강사배정 변경 update	- 수강기간 변경.................
   	@RequestMapping("/center/classAssign/updateClassDate")
   	public void classAssignUpdateClassDate(@RequestParam Map<String, Object> paramMap) {
   		//System.out.println(paramMap);
   		
   		if(paramMap.get("startDeleteFlag").toString().equals("1")) {	 // 시작일이 뒤로 미뤄졌을 때
   			adminMapper.deleteClassLogByStartDt(paramMap);
   		}
   		
   		if(paramMap.get("endDeleteFlag").toString().equals("1")) {		 // 종료일이 앞당겨졌을 때
   			adminMapper.deleteClassLogByEndDt(paramMap);
   		}
   		
   		// 수강기간 변경에 따른 시작일,종료일 update
   		adminMapper.updateEndDt(paramMap);
   		
   		
   		
   		Map<String, Object> logParam = new HashMap<String, Object>();
   		
   		int class_no = Integer.parseInt(paramMap.get("class_no").toString());
   		String teacher_no = paramMap.get("teacher_no").toString();
   		String class_time = paramMap.get("pick_time1").toString();
   		
   		logParam.put("class_no", class_no);
   		logParam.put("teacher_no", teacher_no);
   		logParam.put("class_time", class_time);
   		
   		String[] totalAddDateArr = paramMap.get("totalAddDateArr").toString().split(",");
   		
   		if(totalAddDateArr.length > 0) {
   			for(int i=0; i<totalAddDateArr.length; i++) {
   				
   				if (!totalAddDateArr[i].equals("")) {		// totalAddDateArr= paramMap 은 이런식으로 가져와도 length는 1로 보기때문에.. 한번 더 체크
   					
   	   	   			logParam.put("class_date", totalAddDateArr[i]);   	   			
   	   	   			//System.out.println(logParam);
   	   	   			
   	   	   			Map<String, Object> log = adminMapper.getClassLogTest(logParam);
   	   	   			
   	   	   			if(MapUtils.isEmpty(log)) {	// 중복데이터 없을때만
   	   	   				// class_log 테이블 insert   	    	   	   				
//   	   	   				adminMapper.insertClassLog(logParam);
   	   	   			}
   				}
   	   			   			
   	   		}  
   		}  		
   		
   	
   	}
   	
   	
   	// 유료수강자 강사배정 변경 update	- class, class_log 테이블
   	@RequestMapping("/center/classAssign/update")
   	public void classAssignUpdate(@RequestParam Map<String, Object> paramMap) {   		
   		// paramMap: class_no, teacher_no, pick_time1, pick_time2   		
   		//System.out.println(paramMap);
   		
   		
   		// class: teacher_no, pick_time1, pick_time2, class_week, end_dt 변경
   		// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   		adminMapper.updateClassByAssign(paramMap);	
   		
   		// 요일 변경이 이뤄졌다면
   		if("1".equals(paramMap.get("dateChangeFlag").toString())) {
   			// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   			adminMapper.deleteClassLogByAssign(paramMap);
   			
   			   			
   			// class_log 테이블 insert 파라미터   		
   	   		Map<String, Object> logParam = new HashMap<String, Object>();
   	   		
   	   		int class_no = Integer.parseInt(paramMap.get("class_no").toString());
   	   		String teacher_no = paramMap.get("teacher_no").toString();
   	   		String class_time = paramMap.get("pick_time1").toString();
   	   		
   	   		logParam.put("class_no", class_no);
   	   		logParam.put("teacher_no", teacher_no);
   	   		logParam.put("class_time", class_time);
   	   		
   			// class_log 테이블 생성할 class_date Array
   	   		String[] logDateArr = paramMap.get("logDateArr").toString().split(",");   		
   	   		
   	   		for(int i=0; i<logDateArr.length; i++) {  			
   	   			//System.out.println(logDateArr[i]);   			
   	   			logParam.put("class_date", logDateArr[i]);
   	   			
   	   			//System.out.println(logParam);
   	   			Map<String, Object> log = adminMapper.getClassLogTest(logParam);
  	   			
  	   			if(MapUtils.isEmpty(log)) {	// 중복데이터 없을때만
  	   				// class_log 테이블 insert   	    	   	   				
//  	   				adminMapper.insertClassLog(logParam);
  	   			}
   	   			
   	   						
   	   		}  	
   			
   		}
   		
   		
   		// class_log: teacher_no, class_time 변경 (status 0인 log들만 -> 출석완료 말고 수업예정일들만 변경 가능)
   		// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   		adminMapper.updateClassLogByAssign(paramMap);
   		
   	}
   	
   	
   	
   	
   	
   	
   	
   	
   	// 강사 - 데일리 피드백 등록/수정
   	@PostMapping(value = "/teacher/lesson/classLog/update")   
   	public void teacher_classLog_update(@RequestParam Map<String, Object> paramMap) throws Exception {  		
   		//System.out.println(paramMap);
   		adminMapper.updateClassLogByTeacher(paramMap);		
   	}
   	
   	
   	

   	// 교재 delete
   	@RequestMapping("/center/textbook/deleteTextbook")
   	public void textbookDelete(@RequestParam Map<String, Object> paramMap) {
   		adminMapper.deleteTextbook(paramMap);
   	}
   	
   	
   	
 	// 유료수강자 강사배정 - 구분 change 이벤트 발생 시 - 유형 콤보박스 셀렉트박스 Setting 
   	@PostMapping(value = "/center/classAssign/getCategoryCombo")   
   	public List<Map<String, Object>> center_getCategoryCombo(@RequestParam Map<String, Object> paramMap) throws Exception { 		
   		//paramMap: {company_code=NA, category=보라구중국어}   		   		
   		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
   		list = adminMapper.selectTypeComboByCenter(paramMap);   		
   		
   		return list;
   	}   	
   	
   	// 유료수강자 강사배정 - 유형 change 이벤트 발생 시 - 레벨 콤보박스 셀렉트박스 Setting
   	@PostMapping(value = "/center/classAssign/getLevelCombo")   
   	public List<Map<String, Object>> center_getLevelCombo(@RequestParam Map<String, Object> paramMap) throws Exception { 		
   		//paramMap: {company_code=NA, category=보라구중국어, type=회화}   		   		
   		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
   		list = adminMapper.selectLevelComboByCenter(paramMap);   		
   		
   		return list;
   	}
   	
   	// 유료수강자 강사배정 - 레벨 change 이벤트 발생 시 - 과정 콤보박스 셀렉트박스 Setting
   	@PostMapping(value = "/center/classAssign/getTitleCombo")   
   	public List<Map<String, Object>> center_getTitleCombo(@RequestParam Map<String, Object> paramMap) throws Exception { 		
   		//paramMap: {company_code=NA, category=보라구영어, type=일반회화, level=Starter}   		   		
   		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
   		list = adminMapper.selectTitleComboByCenter(paramMap);   		
   		
   		return list;
   	}
   	
   	
   	// 유료수강자 강사배정 - 방식(전화/화상) change 이벤트 발생 시 - 강사 콤보박스 셀렉트박스 Setting 
   	@PostMapping(value = "/center/classAssign/getTeacherCombo")   
   	public List<Map<String, Object>> center_getTeacherCombo(@RequestParam Map<String, Object> paramMap, HttpSession session) throws Exception {		
   		// paramMap: process   		
   		paramMap.put("company_code", session.getAttribute("company_code"));
   		
   		System.out.println(paramMap);
   		
   		paramMap.put("process", (paramMap.get("process").equals("화상") ? "화상" : "전화" ));
   		
   		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
   		list = adminMapper.selectTeacherComboByClassAssign(paramMap);   		
   		
   		System.out.println(list);
   		
   		return list;
   	}   
   	
   	
   	
   	// 티칭센터 관리자 - 유료수강자 강사배정 - 회원정보 조회/수정
   	@PostMapping(value = "/center/detailUser/update")   
   	public void center_userUpdate(@RequestParam Map<String, Object> paramMap) throws Exception {  		
   		// paramMap: {no=1, etc=22}
   		adminMapper.updateUserByCenter(paramMap);		
   	}
   	
   	
   	
   	// 고객사 관리자 - 회원현황 - 회원정보 수정
   	@PostMapping(value = "/company/user/update")   
   	public void company_user_update(@RequestParam Map<String, Object> paramMap) throws Exception {  		
   		// paramMap: {no=1, name=테스트학생22, code=0000, tel=01012341111222, email=test@mail.com2, etc=22}
   		adminMapper.updateUserByCompany(paramMap);		
   	}
   	
   	// 고객사 관리자 - 회원현황 - 회원삭제
   	@PostMapping(value = "/company/user/delete")   
   	public void company_userDelete(@RequestParam Map<String, Object> paramMap) throws Exception {
   		adminMapper.deleteUserByCompany(paramMap);		
   	}
   	
   	
	// 고객사 관리자 - 유료수강신청 - 회원정보 수정
   	@PostMapping(value = "/company/detailUser/update")   
   	public void company_userUpdate(@RequestParam Map<String, Object> paramMap) throws Exception {  		
   		// paramMap: {no=1, name=테스트학생22, code=0000, tel=01012341111222, email=test@mail.com2, etc=22}
   		adminMapper.updateUserByCompany(paramMap);		
   	}
   	
   	
   	// 고객사 관리자 - 유료수강신청 - 티칭센터 매칭 수정
   	@PostMapping(value = "/company/detailTeaching/update")   
   	public void company_centerUpdate(@RequestParam Map<String, Object> paramMap) throws Exception {  		
   		adminMapper.updateTcCodeByCompany(paramMap);		
   	}
   	
   	// 고객사 관리자 - 레벨테스트신청 - 티칭센터 매칭 수정
   	@PostMapping(value = "/company/levelApp/detailTeaching/update")   
   	public void company_levelApp_centerUpdate(@RequestParam Map<String, Object> paramMap) throws Exception {  		
   		adminMapper.updateLevelAppTcCodeByCompany(paramMap);		
   	}
   	
   	
   	// 유료수강신청 - 선택한 신청자 SMS 보내기  
   	@RequestMapping("/company/classApplication/send")   
   	public void classApplication_smsSend(@RequestParam Map<String, Object> paramMap, HttpServletRequest req) throws Exception {   		
   		String[] selectedArr = null;
   		selectedArr = req.getParameterValues("selectedArr[]");
   		
   		Map<String, Object> smsMap = new HashMap<String, Object>();
   		
   		for(int i=0; i<selectedArr.length; i++) {
   			//System.out.println(selectedArr[i]);
   			
   			/*
   			smsMap.put("text", "[보라구영어]\n'전화영어'신청이 접수되었습니다.\n수강등록이 완료되면 개별 문자로 안내됩니다.");
   			smsMap.put("receipt_number", selectedArr[i]);
   			Common.coolSMSSend(smsMap, "sms");
   			*/   			
   		}
   	}
   	
   	
   	
   	// 티칭센터 - 교재등록 상세보기(수정)
   	@Transactional(rollbackFor=Exception.class)
   	@PostMapping(value = "/center/textbook/detail/update")   
   	public void center_textbookUpdate(@RequestParam Map<String, Object> paramMap, MultipartFile textbook) throws Exception {
   		
   		if (!textbook.isEmpty()) {	// 교재 파일이 있을 시
			try {
				String file_url = "textbook_"+paramMap.get("no").toString()+".pdf";
				paramMap.put("file_url", file_url);				
								
				adminMapper.updateTextbook(paramMap);
				s3Wrapper.upload(textbook, "textbook", file_url);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
   		} else {
   			// 기존에 교재파일이 있었는지 확인
   			Map<String, Object> info = adminMapper.getTextbook(paramMap);
   			
   			if(info.containsKey("file_url")) {
   				paramMap.put("file_url", info.get("file_url"));
   	   			adminMapper.updateTextbook(paramMap);
   				
   			} else { // 기존에 파일이 없었다면
   				paramMap.put("file_url", "");
   	   			adminMapper.updateTextbook(paramMap);
   			}
   			
   		}
   		
   		
   		//adminMapper.updateUserByCompany(paramMap);		
   	}
   	
   	
   	
   	// 티칭센터 휴무일등록 - 중복체크
 	@RequestMapping(value = "/holidayCheck", method = { RequestMethod.POST }) 	
 	public String idCheck(@RequestParam Map<String, Object> paramMap) throws Exception {
 		// paramMap {holiday_dt=2021.05.05, company_code=NA}
 		
 		Map<String, Object> holiday = adminMapper.holidayCheck(paramMap);  
 		
 		if(!MapUtils.isEmpty(holiday)) {
 			return "exist";		// 중복
 		} else {
 			return "ok";
 		}
 	}
   	
 	// 티칭센터 휴무일등록 - insert
 	@PostMapping(value = "/center/holiday/insert")   
   	public void center_holidayInsert(@RequestParam Map<String, Object> paramMap) throws Exception {  		
   		// paramMap: company_code=NA, holiday_dt=2021.05.06, holiday_name=TEST, type=한국공휴일		
 		adminMapper.insertHoliday(paramMap);		
   	}
 	
 	// 티칭센터 휴무일등록 - update
  	@PostMapping(value = "/center/holiday/update")   
	public void center_holidayUpdate(@RequestParam Map<String, Object> paramMap) throws Exception {  		
		// paramMap: no=2, old_holiday_dt=2021-05-10, holiday_dt=2021.05.10, holiday_name=TEST2
		adminMapper.updateHoliday(paramMap);		
	}
 	
  	
  	// 티칭센터 휴무일등록 - 체크 선택된 holiday 삭제
 	@RequestMapping(value = "/center/holiday/checkedItemDelete", method = RequestMethod.POST) 	
 	public int deleteHolidayCheckedItem(@RequestParam(value = "selectedArr[]") List<String> selectedArr) throws Exception {
 		int result = 1;

 		try {			
 			int cnt = selectedArr.size();

 			for (int i = 0; i < cnt; i++) {
 				int holidayNo = Integer.parseInt(selectedArr.get(i));
 				adminMapper.deleteHolidayCheckedItem(holidayNo);	
 			}

 		} catch (Exception e) {
 			System.out.println(e.getMessage());
 			result = 0;
 		}

 		return result;
 	}
  	
	
   
   	@PostMapping(value = "/center/weeklySchedule/classDetail/userUpdate")   
   	public void center_classDetail_userUpdate(@RequestParam Map<String, Object> paramMap) throws Exception {
   		//System.out.println(paramMap);
   		adminMapper.updateUserByClassDetail(paramMap);		
   	}
   	
   	
   	@PostMapping(value = "/center/weeklySchedule/classDetail/getLogInfo")   
   	public Map<String, Object> center_classDetail_getLogInfo(@RequestParam Map<String, Object> paramMap) throws ParseException {
   		System.out.println(paramMap);	// no, flag    ( class_no    level_test_log_no )
   		   		
   		Map<String, Object> info = new HashMap<String, Object>(); 
   		
   		// 수업상태 - 수업전, 수업중, 종료 (수업기간)
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());
		Date date = format.parse(today); // ParseException throw

		Date start_dt, end_dt;
		int startCompare, endCompare;
   		
   		
   		if ("class".equals(paramMap.get("flag").toString())) {
   			info = adminMapper.getClassDetailInfo(paramMap);
   			
   			start_dt = format.parse(info.get("start_dt").toString());
			end_dt = format.parse(info.get("end_dt").toString());

			startCompare = date.compareTo(start_dt);
			endCompare = date.compareTo(end_dt);
			
			if (startCompare < 0) { // 오늘날짜가 수업시작일보다 적다면
				// 수업 전
				info.put("statusText", "수업예정");
			} else {
				if (endCompare > 0) { // 오늘날짜가 종료일보다 크다면
					info.put("statusText", "수업종료");
				} else {
					info.put("statusText", "수업중");
				}
			}
   			
   			
		} else {
			info = adminMapper.getLevelLogInfo(paramMap);			
		}
   		
   		//System.out.println(info);
   		
   		return info;   		
   	}
   	
   	
   	// 고객사 유료수강신청 - 티칭센터 로그인정보 가져오기
   	@PostMapping(value = "/company/classApplication/getTeachingLoginInfo")   
   	public Map<String, Object> company_getLoginInfo(@RequestParam Map<String, Object> paramMap) throws ParseException {
   		Map<String, Object> info = adminMapper.getTcLoginInfo(paramMap);
   		return info;   		
   	}
   	
   	
   	
   	// 티칭센터 TodaySchedule - 강사콤보 change 이벤트 발생 시 - 학생 콤보박스 셀렉트박스 Setting 
   	@PostMapping(value = "/center/todaySchedule/getStudentCombo")   
   	public List<Map<String, Object>> center_getStudentCombo(@RequestParam Map<String, Object> paramMap) throws Exception { 		
   		//paramMap: {teacher_no=2}   		   		
   		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
   		list = adminMapper.selectStudentComboByTeacher(paramMap);   		
   		
   		return list;
   	}
   	
   	
   	// 해당 category로 티칭센터 company_code 가져오기
   	@PostMapping(value = "/company/classApplication/getCenterCodeByCategory")   
   	public Map<String, Object> company_getCenterCodeByCategory(@RequestParam Map<String, Object> paramMap) throws ParseException {
   		Map<String, Object> info = adminMapper.getCenterCodeByCategory(paramMap);   		
   		
   		return info;   		
   	}

   	
   	// class_category No 가져오기.. 해당 과정명으로 db에 데이터 없다면 alert 
   	@RequestMapping("/company/classApplication/getClassCategoryNo")
   	public Map<String, Object> classApplication_getClassCategoryNo(@RequestParam Map<String, Object> paramMap, HttpSession session) {   		
   		// param: category, type, level, title
   		Map<String, Object> classCategory = adminMapper.getClassCategoryNoByApplication(paramMap);
   		
   		if (MapUtils.isEmpty(classCategory)) {
   			classCategory = new HashMap<String, Object>();
   			classCategory.put("no", 0);
   		} 		
   		
   		return classCategory;
   	}
   	
   	// 유료수강신청 등록
   	@RequestMapping("/company/classApplication/insert")
   	public void classApplication_insert(@RequestParam Map<String, Object> paramMap, HttpSession session) {
   		
   		// param: {company_code=OPIA, center_code=NA, pick_min2=유료, reg_dt=2021.05.26, name=홍길동, eng_name=, tel=01012341234, email=test@naver.com, process=화상, category=4, type=일반회화, level=Starter, title=[왕초보회화] English communication 1, period_moths=1, class_time=20, class_week=Mon/Tue/Wed/Thu/Fri, pick_hour1=18, pick_min1=10, pick_hour2=, com_name=, subject=English, req_content=, class_category_no=14, code=01012341234, role=null, user_no=0}
   		
   		paramMap.put("code", paramMap.get("tel"));
   		paramMap.put("role", "3");
   		paramMap.put("class_time", paramMap.get("class_time").toString() + "min");
		paramMap.put("period_moths", paramMap.get("period_moths").toString() + "개월");
   		
   		
   		// 1) 해당 연락처(코드) 유저 있는지 확인 후 없으면 insert					
		Map<String, Object> user = adminMapper.getUserByCode(paramMap);	
		int user_no = 0;		
		
		if(!MapUtils.isEmpty(user)) {	// 해당 코드(연락처) 유저가 있다면 
			// 2) user_no 가져오기
			user_no = Integer.parseInt(user.get("no").toString());
		} else {	// 신청서 - 해당 코드(연락처) 유저가 없다면 
			// 2) user 생성 @@@@@@@@@@@@@@@@@@@@@@
			adminMapper.inserUserByApplication(paramMap);							
			user_no = adminMapper.selectLastAIUser() - 1;		// user_no (FK) 가져오기							
		}
   			
		paramMap.put("user_no", user_no);
   		
		
		String pick_time1 = paramMap.get("pick_hour1").toString() + ":" + paramMap.get("pick_min1").toString();
		String pick_time2 = "";
		
		// pick_time2는 필수로 받는 값이 아님
		if ("".equals(paramMap.get("pick_hour2").toString()) || "".equals(paramMap.get("pick_min2").toString())) {
			pick_time2 = "";
		} else {
			pick_time2 = paramMap.get("pick_hour2").toString() + ":" + paramMap.get("pick_min2").toString();
		}
			
		paramMap.put("pick_time1", pick_time1);
		paramMap.put("pick_time2", pick_time2);
				
		
		//class_category
		
		// 유료수강 insert @@@@@@@@@@@@@@@@@@@@@@
		adminMapper.insertClassAppication(paramMap);
		
   	}
   	
   	
   	
   	// 유료수강신청 수정
   	@RequestMapping("/company/classApplication/update")
   	public void classApplication_update(@RequestParam Map<String, Object> paramMap, HttpSession session) {   	
   		paramMap.put("class_time", paramMap.get("class_time").toString() + "min");
		paramMap.put("period_moths", paramMap.get("period_moths").toString() + "개월");
		
		String pick_time1 = paramMap.get("pick_hour1").toString() + ":" + paramMap.get("pick_min1").toString();
		String pick_time2 = "";
		
		// pick_time2는 필수로 받는 값이 아님
		if ("".equals(paramMap.get("pick_hour2").toString()) || "".equals(paramMap.get("pick_min2").toString())) {
			pick_time2 = "";
		} else {
			pick_time2 = paramMap.get("pick_hour2").toString() + ":" + paramMap.get("pick_min2").toString();
		}
			
		paramMap.put("pick_time1", pick_time1);
		paramMap.put("pick_time2", pick_time2);		
		
		// 유료수강 update @@@@@@@@@@@@@@@@@@@@@@
		adminMapper.updateClassAppication(paramMap);		
   	}
	
   	
   	
   	// 유료수강신청 삭제
   	@RequestMapping("/company/classApplication/delete")
   	public Map<String, Object> classApplication_delete(@RequestParam Map<String, Object> paramMap, HttpSession session) {   	
   		Map<String, Object> result = new HashMap<String, Object>();
		
   		// 해당 신청서 내용으로 생성된 class 내용이 있는지 확인 - 배정된게 없거나 임시저장 까지만 된상태면 class_no = 0  / 수강중이면 class_no != 0 
   		Map<String, Object> classMap = adminMapper.getClassExist(paramMap);
   		
   		if("0".equals(classMap.get("class_no").toString())) {	// 배정 안된상태. (임시저장 또는 저장 전)
   			// 유료수강 delete @@@@@@@@@@@@@@@@@@@@@@
   			adminMapper.deleteClassAppication(paramMap);		// param : no
   			
   			result.put("result", "1");
   			result.put("msg", "삭제되었습니다.");
   			
   		} else {
   			result.put("result", "0");
   			result.put("msg", "삭제 실패. 해당 신청서 내용으로 수강중인 과정이 있습니다.");
   		}   		
		
   		return result;
   	}
   	
   	
   	
   	// 무료레벨테스트 신청 등록
   	@RequestMapping("/company/levelTestApplication/insert")
   	public void levelTestApplication_insert(@RequestParam Map<String, Object> paramMap, HttpSession session) {
   		paramMap.put("code", paramMap.get("tel"));
   		paramMap.put("role", "3");
   		   		
   		// 1) 해당 연락처(코드) 유저 있는지 확인 후 없으면 insert					
		Map<String, Object> user = adminMapper.getUserByCode(paramMap);	
		int user_no = 0;		
		
		if(!MapUtils.isEmpty(user)) {	// 해당 코드(연락처) 유저가 있다면 
			// 2) user_no 가져오기
			user_no = Integer.parseInt(user.get("no").toString());
		} else {	// 신청서 - 해당 코드(연락처) 유저가 없다면 
			// 2) user 생성 @@@@@@@@@@@@@@@@@@@@@@
			adminMapper.inserUserByApplication(paramMap);							
			user_no = adminMapper.selectLastAIUser() - 1;		// user_no (FK) 가져오기							
		}
   			
		paramMap.put("user_no", user_no);
   		
		
		String pick_time1 = paramMap.get("pick_hour1").toString() + ":" + paramMap.get("pick_min1").toString();
		String pick_time2 = "";
		
		// pick_time2는 필수로 받는 값이 아님
		if ("".equals(paramMap.get("pick_hour2").toString()) || "".equals(paramMap.get("pick_min2").toString())) {
			pick_time2 = "";
		} else {
			pick_time2 = paramMap.get("pick_hour2").toString() + ":" + paramMap.get("pick_min2").toString();
		}
			
		paramMap.put("pick_time1", pick_time1);
		paramMap.put("pick_time2", pick_time2);
		
		//System.out.println(paramMap);
		
		// 유료수강 insert @@@@@@@@@@@@@@@@@@@@@@
		adminMapper.insertLevelTestAppication(paramMap);
		
   	}
   	
   	
   	// 레벨테스트신청 수정
   	@RequestMapping("/company/levelTestApplication/update")
   	public void levelTestApplication_update(@RequestParam Map<String, Object> paramMap, HttpSession session) {
		String pick_time1 = paramMap.get("pick_hour1").toString() + ":" + paramMap.get("pick_min1").toString();
		String pick_time2 = "";
		
		// pick_time2는 필수로 받는 값이 아님
		if ("".equals(paramMap.get("pick_hour2").toString()) || "".equals(paramMap.get("pick_min2").toString())) {
			pick_time2 = "";
		} else {
			pick_time2 = paramMap.get("pick_hour2").toString() + ":" + paramMap.get("pick_min2").toString();
		}
			
		paramMap.put("pick_time1", pick_time1);
		paramMap.put("pick_time2", pick_time2);	
		
		// 레벨테스트 신청 update @@@@@@@@@@@@@@@@@@@@@@
		adminMapper.updateLevelTestAppication(paramMap);		
   	}
   	
   	
   	// 레벨테스트 신청 삭제
   	@RequestMapping("/company/levelTestApplication/delete")
   	public Map<String, Object> levelTestApplication_delete(@RequestParam Map<String, Object> paramMap, HttpSession session) {   	
   		Map<String, Object> result = new HashMap<String, Object>();
		
   		// 해당 신청서 내용으로 레벨테스트 배정된 내용 있는지 확인
   		Map<String, Object> levelTestMap = adminMapper.getLevelTestExist(paramMap);  		
   		
   		if("0".equals(levelTestMap.get("cnt").toString())) {	// 강사배정 안된상태.
   			// 레벨테스트 신청서 delete @@@@@@@@@@@@@@@@@@@@@@
   			adminMapper.deleteLevelTestAppication(paramMap);		// param : no
   			
   			result.put("result", "1");
   			result.put("msg", "삭제되었습니다.");
   			
   		} else {
   			result.put("result", "0");
   			result.put("msg", "삭제 실패. 해당 신청서 내용으로 테스트 배정된 내용이 있습니다.");
   		}   				
   		
   		return result;
   	}
   	
}
