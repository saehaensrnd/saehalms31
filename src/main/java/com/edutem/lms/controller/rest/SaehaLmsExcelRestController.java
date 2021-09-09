package com.edutem.lms.controller.rest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.MapUtils;

import com.edutem.lms.component.Common;
import com.edutem.lms.component.S3Wrapper;
import com.edutem.lms.mapper.AdminMapper;
import com.edutem.lms.mapper.TeacherMapper;

@RestController
public class SaehaLmsExcelRestController {

	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private TeacherMapper teacherMapper;
	

	@Autowired
	private S3Wrapper s3Wrapper;
  	
  	// Make user excel
  	@Transactional(rollbackFor = Exception.class)
  	@ResponseBody
  	@RequestMapping("/admin/user/downAllUser")
  	public String downAllUser(@RequestParam Map<String, Object> paramMap){
  		String downloadURL = "";
  		
  		String[] mode = {"NO", "site", "kname", "land line", "ID", "cell phone", "parent", "parent No", "post", "address1", "address2", "register date", "condition", "이메일수신여부", "SMS수신여부"};
  		String[] column = {"no", "site_name", "student_name", "student_tel", "id", "student_phone", "student_parent_name", "student_parent_phone", "post", "address1", "address2", "student_regdate", "student_status", "student_email_status", "student_phone_status"};

  		XSSFWorkbook wb = new XSSFWorkbook();
  		XSSFRow row = null;
  		int rowNo = 0;

  		XSSFSheet sheet = wb.createSheet("회원목록");
  		row = sheet.createRow(rowNo);
  		for(int i=0; i<mode.length; i++) {
  			row.createCell(i).setCellValue(mode[i]);
  		}
  		
  		rowNo = 1;
  		List<Map<String, Object>> list = adminMapper.selectUserStudentExcel(paramMap);
  		for(Map<String, Object> map: list) {
  			row = sheet.createRow(rowNo++);
  			String[] address = map.get("student_address").toString().split("/");
  			for(int i=0; i<mode.length; i++) {
  				if(column[i].equals("no")) {
  					row.createCell(i).setCellValue(rowNo-1);
  				}
  				else if(column[i].equals("post")) {
  					if(address.length > 0) {
  						row.createCell(i).setCellValue(address[0]);
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else if(column[i].equals("address1")) {
  					if(address.length > 0) {
  						row.createCell(i).setCellValue(address[1]);
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else if(column[i].equals("address2")) {
  					if(address.length > 0) {
  						row.createCell(i).setCellValue(address[2]);
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else if(column[i].equals("student_status")) {
  					switch(Integer.parseInt(map.get(column[i]).toString())) {
  					case 0: row.createCell(i).setCellValue("탈퇴"); break;
  					case 1: row.createCell(i).setCellValue("정상"); break;
  					case 2: row.createCell(i).setCellValue("대기"); break;
  					}
  				}
  				else if(column[i].equals("student_email_status")) {
  					if(map.containsKey(column[i])) {
  	  					switch(Integer.parseInt(map.get(column[i]).toString())) {
  	  					case 0: row.createCell(i).setCellValue("N"); break;
  	  					case 1: row.createCell(i).setCellValue("Y"); break;
  	  					}
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else if(column[i].equals("student_phone_status")) {
  					if(map.containsKey(column[i])) {
  	  					switch(Integer.parseInt(map.get(column[i]).toString())) {
  	  					case 0: row.createCell(i).setCellValue("N"); break;
  	  					case 1: row.createCell(i).setCellValue("Y"); break;
  	  					}
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else {
  					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
  				}
  			}
  		}
  		
  		Date date = new Date();
  		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
  		
  		ByteArrayOutputStream bo = new ByteArrayOutputStream();
  		try {
			wb.write(bo);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bo != null) {
					bo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		
  		ByteArrayInputStream bi = null;
  		try {
  			bi = new ByteArrayInputStream(bo.toByteArray());
  			Long contentLength = Long.valueOf(bo.toByteArray().length);
			s3Wrapper.uploadTest(contentLength, bi, "excel/user_student_excel", "student_"+format.format(date)+".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bi != null) {
					bi.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		
  		downloadURL = "https://saeha.s3.ap-northeast-2.amazonaws.com/excel/user_student_excel/student_"+format.format(date)+".xlsx";
  		
  		return downloadURL;
  	}
  	
  	// Make classStudent excel
  	@Transactional(rollbackFor = Exception.class)
  	@ResponseBody
  	@RequestMapping("/admin/user/downClassStudent")
  	public String downClassStudent(@RequestParam Map<String, Object> paramMap){
  		String downloadURL = "";
  		
  		String[] mode = {"site", "수업명", "수업구분", "수업방식", "수업횟수", "apply term", "name", "ID", "land line", "cell phone", "post", "address1", "address2", "원어민강사", "시작일", "종료일", "condition", "보호자", "보호자 전화번호", "교재", "이메일수신여부", "SMS수신여부"};
  		String[] column = {"site_name", "product_name", "regular", "product_type", "product_week", "product_running_time", "student_name", "id", "student_tel", "student_phone", "post", "address1", "address2", "teacher_name", "start_date", "end_date", "status", "student_parent_name", "student_parent_phone", "textbook_name", "student_email_status", "student_phone_status"};

  		XSSFWorkbook wb = new XSSFWorkbook();
  		XSSFRow row = null;
  		int rowNo = 0;

  		XSSFSheet sheet = wb.createSheet("수강자정보");
  		row = sheet.createRow(rowNo);
  		for(int i=0; i<mode.length; i++) {
  			row.createCell(i).setCellValue(mode[i]);
  		}
  		
  		rowNo = 1;
  		List<Map<String, Object>> list = adminMapper.selectClassStudentExcel(paramMap);
  		for(Map<String, Object> map: list) {
  			row = sheet.createRow(rowNo++);
  			String[] address = map.get("student_address").toString().split("/");
  			for(int i=0; i<mode.length; i++) {
  				if(column[i].equals("regular")) {
  					row.createCell(i).setCellValue("regular class");
  				}
  				else if(column[i].equals("product_week")) {
  					String[] week = map.get("product_week").toString().split("/");
  					row.createCell(i).setCellValue("주 "+week.length+"회");
  				}
  				else if(column[i].equals("post")) {
  					if(address.length > 0) {
  						row.createCell(i).setCellValue(address[0]);
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else if(column[i].equals("address1")) {
  					if(address.length > 0) {
  						row.createCell(i).setCellValue(address[1]);
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else if(column[i].equals("address2")) {
  					if(address.length > 0) {
  						row.createCell(i).setCellValue(address[2]);
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else if(column[i].equals("student_email_status")) {
  					if(map.containsKey(column[i])) {
  	  					switch(Integer.parseInt(map.get(column[i]).toString())) {
  	  					case 0: row.createCell(i).setCellValue("N"); break;
  	  					case 1: row.createCell(i).setCellValue("Y"); break;
  	  					}
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else if(column[i].equals("student_phone_status")) {
  					if(map.containsKey(column[i])) {
  	  					switch(Integer.parseInt(map.get(column[i]).toString())) {
  	  					case 0: row.createCell(i).setCellValue("N"); break;
  	  					case 1: row.createCell(i).setCellValue("Y"); break;
  	  					}
  					}
  					else {
  						row.createCell(i).setCellValue("");
  					}
  				}
  				else {
  					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
  				}
  			}
  		}
  		
  		Date date = new Date();
  		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
  		
  		ByteArrayOutputStream bo = new ByteArrayOutputStream();
  		try {
			wb.write(bo);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bo != null) {
					bo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		
  		ByteArrayInputStream bi = null;
  		try {
  			bi = new ByteArrayInputStream(bo.toByteArray());
  			Long contentLength = Long.valueOf(bo.toByteArray().length);
			s3Wrapper.uploadTest(contentLength, bi, "excel/classStudent_excel", "classStudent_"+format.format(date)+".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bi != null) {
					bi.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		
  		downloadURL = "https://saeha.s3.ap-northeast-2.amazonaws.com/excel/classStudent_excel/classStudent_"+format.format(date)+".xlsx";
  		
  		return downloadURL;
  	}
  	
  	// Make leveltest excel
  	@Transactional(rollbackFor = Exception.class)
  	@ResponseBody
  	@RequestMapping("/admin/user/downAllLeveltest")
  	public String downAllLeveltest(@RequestParam Map<String, Object> paramMap){
  		String downloadURL = "";
  		
  		String[] mode = {"NO", "사이트", "회원명", "로그인아이디", "코스명", "등록일", "수업일", "상태"};
  		String[] column = {"no", "site_name", "student_name", "id", "leveltest_language", "leveltest_regdate", "study_date", "status"};

  		XSSFWorkbook wb = new XSSFWorkbook();
  		XSSFRow row = null;
  		int rowNo = 0;

  		XSSFSheet sheet = wb.createSheet("수강자정보");
  		row = sheet.createRow(rowNo);
  		for(int i=0; i<mode.length; i++) {
  			row.createCell(i).setCellValue(mode[i]);
  		}
  		
  		rowNo = 1;
  		List<Map<String, Object>> list = adminMapper.selectLevelTestExcel(paramMap);
  		for(Map<String, Object> map: list) {
  			row = sheet.createRow(rowNo++);
  			for(int i=0; i<mode.length; i++) {
  				if(column[i].equals("no")) {
  					row.createCell(i).setCellValue(i+1);
  				}
  				else if(column[i].equals("leveltest_language")) {
  					row.createCell(i).setCellValue(String.valueOf(map.get(column[i]))+" 레벨 테스트");
  				}
  				else if(column[i].equals("study_date")) {
  					if(map.containsKey("study_date")) {
  						System.out.println("true");
  						row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
  					}
  					else {
  						System.out.println("false");
  						row.createCell(i).setCellValue("미정");
  					}
  				}
  				else {
  					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
  				}
  			}
  		}
  		
  		Date date = new Date();
  		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
  		
  		ByteArrayOutputStream bo = new ByteArrayOutputStream();
  		try {
			wb.write(bo);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bo != null) {
					bo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		
  		ByteArrayInputStream bi = null;
  		try {
  			bi = new ByteArrayInputStream(bo.toByteArray());
  			Long contentLength = Long.valueOf(bo.toByteArray().length);
			s3Wrapper.uploadTest(contentLength, bi, "excel/leveltest_excel", "leveltest_"+format.format(date)+".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bi != null) {
					bi.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		
  		downloadURL = "https://saeha.s3.ap-northeast-2.amazonaws.com/excel/leveltest_excel/leveltest_"+format.format(date)+".xlsx";
  		
  		return downloadURL;
  	}
  	
  	// Make leveltest excel
  	@Transactional(rollbackFor = Exception.class)
  	@ResponseBody
  	@RequestMapping("/admin/user/downAllPostpone")
  	public String downAllPostpone(){
  		String downloadURL = "";
  		
  		String[] mode = {"NO", "사이트", "회원명", "로그인아이디", "과정", "휴강일", "보강일", "신청일", "진행과정"};
  		String[] column = {"no", "site_name", "student_name", "id", "product_name", "holiday_date", "supplement_date", "postpone_regdate", "postpone_status"};

  		XSSFWorkbook wb = new XSSFWorkbook();
  		XSSFRow row = null;
  		int rowNo = 0;

  		XSSFSheet sheet = wb.createSheet("수강자정보");
  		row = sheet.createRow(rowNo);
  		for(int i=0; i<mode.length; i++) {
  			row.createCell(i).setCellValue(mode[i]);
  		}
  		
  		rowNo = 1;
  		List<Map<String, Object>> list = adminMapper.selectPostponeExcel();
  		for(Map<String, Object> map: list) {
  			row = sheet.createRow(rowNo++);
  			for(int i=0; i<mode.length; i++) {
  				if(column[i].equals("no")) {
  					row.createCell(i).setCellValue(i+1);
  				}
  				else if(column[i].equals("postpone_status")) {
  					if(Integer.parseInt(map.get("postpone_status").toString()) == 0) {
  						row.createCell(i).setCellValue("요청");
  					}
  					else {
  						row.createCell(i).setCellValue("확인");
  					}
  				}
  				else {
  					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
  				}
  			}
  		}
  		
  		Date date = new Date();
  		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
  		
  		ByteArrayOutputStream bo = new ByteArrayOutputStream();
  		try {
			wb.write(bo);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bo != null) {
					bo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		
  		ByteArrayInputStream bi = null;
  		try {
  			bi = new ByteArrayInputStream(bo.toByteArray());
  			Long contentLength = Long.valueOf(bo.toByteArray().length);
			s3Wrapper.uploadTest(contentLength, bi, "excel/leveltest_excel", "leveltest_"+format.format(date)+".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bi != null) {
					bi.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  		
  		downloadURL = "https://saeha.s3.ap-northeast-2.amazonaws.com/excel/leveltest_excel/leveltest_"+format.format(date)+".xlsx";
  		
  		return downloadURL;
  	}
   	
   	
  	// Make classInfo excel - 수업기본정보 엑셀 다운로드
   	@Transactional(rollbackFor = Exception.class)   	
   	@RequestMapping("/admin/classInfo/excelDownload")
   	public String downAllclassInfo(@RequestParam Map<String, Object> paramMap){
   				   		
   		String downloadURL = "";
   		
   		// 엑셀에 표시될 컬럼 제목	
   		String[] mode = {"NO", "수업번호", "사이트", "교육과정", "구분", "과정명", "시작일", "종료일", "수업방식/주횟수/인원/배정인원", "시작시간", "선생님", "학습교재", "로그인아이디", "회원정보 전화번호", "회원정보 휴대전화", "회원정보 보호자휴대전화", "회원명", "회원영문명", "수강자상태"};
   		String[] column = {"rownum", "room_no", "site_name", "study_name", "classType", "product_name", "start_date", "end_date", "class_info", "time", "teacher_name", "textbook_name", "id", "student_tel", "student_phone", "student_parent_phone", "student_name", "student_eng_name", "status"};

   		XSSFWorkbook wb = new XSSFWorkbook();
   		XSSFRow row = null;
   		int rowNo = 0;

   		XSSFSheet sheet = wb.createSheet("수업정보");
   		row = sheet.createRow(rowNo);				// 시트 생성
   		for(int i=0; i<mode.length; i++) {
   			row.createCell(i).setCellValue(mode[i]);		// 첫 row 컬럼 제목 Setting
   		}
   		
   		rowNo = 1;
   		adminMapper.setRownum();
   		List<Map<String, Object>> list = adminMapper.selectClassInfoExcel(paramMap);	// 수업기본정보 엑셀 다운로드 내용 조회
   		for(Map<String, Object> map: list) {	// 조회된 리스트 숫자만큼 반복
   			
   			row = sheet.createRow(rowNo++);		// row 생성
   			for(int i=0; i<mode.length; i++) {	// 컬럼 숫자만큼 반복하며 cell 값 Setting
   				if(column[i].equals("rownum")) {
   					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
   				}   				
   				else if(column[i].equals("class_info")) {
   					String class_info = "";
   					
   					String product_type = map.get("product_type").toString();
   					
   					String product_week = "";
   					if(!"".equals(map.get("product_week").toString())) {
   						String[] week = map.get("product_week").toString().split("/");
   	   					product_week = "주"+week.length+"회";
   					} 
   					
   					String product_personnel = map.get("product_personnel").toString();
   					String class_student_cnt = map.get("class_student_cnt").toString() + "명";
   					
   					
   					class_info = product_type + " / " + product_week + " / " + product_personnel + " / " + class_student_cnt;
   					
   					row.createCell(i).setCellValue(class_info);
   				}
   				else if(column[i].equals("time")) {
   					String time = map.get("class_time").toString() + "/" +map.get("product_running_time").toString() + "분";
   					
   					row.createCell(i).setCellValue(time);
   				}
   				else {
   					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
   				}
   			}
   		}
   		
   		Date date = new Date();
   		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
   		
   		ByteArrayOutputStream bo = new ByteArrayOutputStream();
   		try {
 			wb.write(bo);
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if(bo != null) {
 					bo.close();
 				}
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}
   		
   		ByteArrayInputStream bi = null;
   		try {
   			bi = new ByteArrayInputStream(bo.toByteArray());
   			Long contentLength = Long.valueOf(bo.toByteArray().length);   			
 			s3Wrapper.uploadTest(contentLength, bi, "excel/classList_excel", "ClassList_"+format.format(date)+".xlsx");
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if(bi != null) {
 					bi.close();
 				}
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}
   		
   		downloadURL = "https://saeha.s3.ap-northeast-2.amazonaws.com/excel/classList_excel/ClassList_"+format.format(date)+".xlsx";
   		
   		return downloadURL;
   	}
   	
   	
   	
   	
   	// Make timeClass excel - 시간별 수업현황 excel 다운로드
   	@Transactional(rollbackFor = Exception.class)   	
   	@RequestMapping("/admin/timeClass/excelDownload")
   	public String downAllTimeClass(@RequestParam Map<String, Object> paramMap){   		
   		System.out.println(paramMap);
   				   		
   		String downloadURL = "";
   		
   			
   		String[] mode = {"NO", "사이트", "Room No.", "로그인아이디", "회원명", "영문이름", "수업구분", "Course", "c_start_date", "c_end_date", "Course Date", "Class Phone", "Phone", "Mobile", "parent_mobile", "Step", "Class Time", "End Time"
   				, "Type", "Status", "Attend", "학생", "강사", "결과레벨", "WebcamClass", "실제수업전화", "Teacher", "선생님티칭센터", "선생님아이디"};
   		String[] column = {"rownum", "site_name", "room_no", "student_id", "student_name", "student_eng_name", "classType", "course", "start_date", "end_date", "course_date", "class_phone", "student_tel", "student_phone", "student_parent_phone", "textbook_name", "class_time", "class_endTime"
   				, "type", "class_status", "present", "student_status", "teacher_status", "result_level", "product_type", "real_class_phone", "teacher_name", "center_name", "teacher_id"};

   		XSSFWorkbook wb = new XSSFWorkbook();
   		XSSFRow row = null;
   		int rowNo = 0;

   		XSSFSheet sheet = wb.createSheet("수업정보");
   		row = sheet.createRow(rowNo);
   		for(int i=0; i<mode.length; i++) {
   			row.createCell(i).setCellValue(mode[i]);
   		}
   		
   		
   		String class_time = "";
		int running_time = 0;		
		int class_hour, class_min;
		String[] classTimeSplit = null;		
		int end_hour, end_min;
		String class_endTime = "";
		
   		
   		rowNo = 1;
   		adminMapper.setRownum();
   		List<Map<String, Object>> list = adminMapper.selectTimeClassExcel(paramMap);		// 엑셀에 뿌려줄 데이터 조회
   		for(Map<String, Object> map: list) {
   			
   			row = sheet.createRow(rowNo++);
   			for(int i=0; i<mode.length; i++) {
   				if(column[i].equals("rownum")) {
   					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
   				}   				
   				else if(column[i].equals("class_endTime")) {
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
   					
   					row.createCell(i).setCellValue(class_endTime);
   				}   				
   				else {
   					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
   				}
   			}
   		}
   		
   		Date date = new Date();
   		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
   		
   		ByteArrayOutputStream bo = new ByteArrayOutputStream();
   		try {
 			wb.write(bo);
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if(bo != null) {
 					bo.close();
 				}
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}
   		
   		ByteArrayInputStream bi = null;
   		try {
   			bi = new ByteArrayInputStream(bo.toByteArray());
   			Long contentLength = Long.valueOf(bo.toByteArray().length);   			
 			s3Wrapper.uploadTest(contentLength, bi, "excel/timeClass_excel", "TimeClass_"+format.format(date)+".xlsx");
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if(bi != null) {
 					bi.close();
 				}
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}
   		
   		downloadURL = "https://saeha.s3.ap-northeast-2.amazonaws.com/excel/timeClass_excel/TimeClass_"+format.format(date)+".xlsx";
   		
   		
   		return downloadURL;
   	}
   	
   	
   	// Make TodaySchedule excel	(Teacher, Admin)
   	@Transactional(rollbackFor = Exception.class)   	
   	@RequestMapping("/todaySchedule/excelDownload")
   	public String downAlltodaySchedule(@RequestParam Map<String, Object> paramMap, HttpSession session){   		
   		System.out.println(paramMap);	// {searchType=all, keyword=, searchDate=2021-07-13, searchStartHour=06, searchEndHour=24, flag=teacher}
   		String flag = paramMap.get("flag").toString();	
   		
   		String downloadURL = "";
   		
   			
   		String[] mode = {"NO", "업체명", "Room No.", "회원명", "로그인아이디", "수업구분", "Class Type", "Course", "c_start_date", "c_end_date", "Course Date", "Class Phone", "Phone", "Mobile", "parent mobile",                                                 "Step", "Class Time", "End Time", "Type", "Status", "Attend", "결과레벨", "WebcamClass", "실제수업전화", "Teacher", "선생님티칭센터", "선생님아이디"};
   		String[] column = {"rownum", "site_name", "room_no", "student_name", "student_id", "class_kind", "product_type", "product_name", "start_date", "end_date", "study_date", "class_phone", "student_tel", "student_phone", "student_parent_phone", "textbook_name", "class_time", "class_endTime", "day_type", "log_status", "present", "result_level", "web_classType", "real_class_phone", "teacher_name", "center_name", "teacher_id"};

   		XSSFWorkbook wb = new XSSFWorkbook();
   		XSSFRow row = null;
   		int rowNo = 0;

   		XSSFSheet sheet = wb.createSheet("수업정보");
   		row = sheet.createRow(rowNo);
   		for(int i=0; i<mode.length; i++) {
   			row.createCell(i).setCellValue(mode[i]);
   		}
   		
   		rowNo = 1;
   		adminMapper.setRownum();
   		
   		
   		
   		
   		List<Map<String, Object>> classList = new ArrayList<Map<String, Object>>();
   		if("teacher".equals(flag)) {
   			// 로그인 user 정보 - user_teahcher_no 가져오기
   	   		String user_no = session.getAttribute("login_no").toString();
   	   		Map<String, Object> user = adminMapper.getUserByLoginNo(user_no);
   	   		paramMap.put("user_teacher_no", user.get("user_teacher_no"));
   			//classList = teacherMapper.selectTodayScheduleExcel(paramMap);
   		} else {   			
   			//classList = teacherMapper.selectTodayScheduleExcelByAdmin(paramMap);
   		}   		
   		classList = teacherMapper.selectTodayScheduleExcel(paramMap);
   		
   		String class_time = "";
		int running_time = 0;
		
		int class_start_hour = 0;
		int class_start_min = 0;
		String[] classTimeSplit = null;
		
		int class_end_hour = 0;
		int class_end_min = 0;
		String class_endTime = "";
		
		int searchStartHour = Integer.parseInt(paramMap.get("searchStartHour").toString());
		int searchEndHour = Integer.parseInt(paramMap.get("searchEndHour").toString());
		
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
								
				// 검색조건 Time 시작시간~종료시간 관련 if~else
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
   		
   		
   		for(Map<String, Object> map: classList) {
   			
   			row = sheet.createRow(rowNo++);
   			for(int i=0; i<mode.length; i++) {
   				if(column[i].equals("rownum")) {
   					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
   				}   				
   				else if(column[i].equals("class_kind")) {
   					String class_kind = "";
   					
   					if("class".equals(map.get("flag").toString())) {
   						class_kind = "regular class";
   					} else {
   						class_kind = "leveltest";
   					}   					
   					
   					row.createCell(i).setCellValue(class_kind);
   				}   				
   				else {
   					row.createCell(i).setCellValue(String.valueOf(map.get(column[i])));
   				}
   			}
   		}
   		
   		Date date = new Date();
   		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
   		
   		ByteArrayOutputStream bo = new ByteArrayOutputStream();
   		try {
 			wb.write(bo);
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if(bo != null) {
 					bo.close();
 				}
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}
   		
   		ByteArrayInputStream bi = null;
   		try {
   			bi = new ByteArrayInputStream(bo.toByteArray());
   			Long contentLength = Long.valueOf(bo.toByteArray().length);   			
 			s3Wrapper.uploadTest(contentLength, bi, "excel/todaySchedule_excel", "DailyClass_"+format.format(date)+".xlsx");
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if(bi != null) {
 					bi.close();
 				}
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}
   		
   		downloadURL = "https://saeha.s3.ap-northeast-2.amazonaws.com/excel/todaySchedule_excel/DailyClass_"+format.format(date)+".xlsx";
   		
   		return downloadURL;
   	}
   	
   	
}
