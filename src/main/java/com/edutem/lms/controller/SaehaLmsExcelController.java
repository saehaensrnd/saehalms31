package com.edutem.lms.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.MapUtils;

import com.edutem.lms.component.S3Wrapper;
import com.edutem.lms.mapper.AdminMapper;


@Controller
public class SaehaLmsExcelController {
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired 
	private S3Wrapper s3Wrapper;
	

	/************************ 유료수강신청 엑셀업로드 등록 ************************/ 
	@Transactional(rollbackFor=Exception.class)
	@RequestMapping("/classApplicationExcel")
	public ModelAndView classApplicationExcel(MultipartFile mfile, HttpServletResponse res, HttpSession session, HttpServletRequest req) throws IOException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/company/classApplication");		
		
		DataFormatter formatter = new DataFormatter();		
		
		// 등록 실패 이름, 연락처...
		StringBuffer ErrInfo = new StringBuffer();
		String returnPage = req.getParameter("returnPage");
		
		if(!mfile.isEmpty()) {
			try {
				Workbook wb = WorkbookFactory.create(mfile.getInputStream());
				wb.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);	
				
				// 유료신청 정보
				String[] classAppKey = {"flag", "reg_dt", "name", "tel", "email", "type", "level", "title", "period_moths", "class_week", "class_time", "start_dt", "pick_time1", "pick_time2", "process", "kind", "com_name", "req_content","eng_name"};
				Map<String, Object> classAppMap;
				
				// 첫번째 시트	행 숫자만큼 반복 			
				for(Row row: wb.getSheetAt(0)) {	
					ErrInfo.setLength(0);	// 에러 메시지 초기화
					int duplCnt = 0;		// 중복 내역 초기화
					Map<String, Object> class_category = new HashMap<String, Object>();
					
					// 0번째 row 제외 -> 제목 부분 
					if(row.getRowNum() != 0) {
						classAppMap = new HashMap<String, Object>();
						
						for(int cn = 0; cn<classAppKey.length; cn++) {
							Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);						
							
							if(!formatter.formatCellValue(row.getCell(0)).trim().equals("")) {
								
								if(classAppKey[cn].equals("flag")) {									
									String flag = formatter.formatCellValue(cell).trim();
									//String user_companyCode = "";
									String subject = "";
									
									//System.out.println(user_company_code);
									if ("오피아".equals(flag)){
										//user_companyCode = "CP_OPIA";
										subject = "English";										
									} else if("보라구영어".equals(flag)) {
										//user_companyCode = "CP_BORAGU";
										subject = "English";
									} else if("보라구중국어".equals(flag)) {
										//user_companyCode = "CP_BORAGU_CN";
										subject = "Chinese";
									} 
									classAppMap.put("subject", subject);
																		
								}
								
								if(classAppKey[cn].equals("reg_dt")) {
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("name")) {
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());
									//ErrInfo.append("등록실패.. 빈칸이거나, 등록된 연락처인지 확인해주세요.  [ 이름: " + formatter.formatCellValue(cell));
								}
								
								if(classAppKey[cn].equals("tel")) {
									String tel = formatter.formatCellValue(cell).trim();
									//classAppMap.put("tel", tel);		
									classAppMap.put("tel", tel.replace(" ", "").replace("-", ""));
									
									//ErrInfo.append(", 연락처: " + tel + " ]"); 
									
									// 코드 생성 (연락처)
									classAppMap.put("code", tel.replace(" ", "").replace("-", ""));	
									
									if (tel.replace(" ", "").replace("-", "").equals("")) { 
										// 비어있다면 예외 발생
										ErrInfo.append("등록실패. 연락처가 비어있습니다.");
										throw new Exception();
									}										
								}
								
								if(classAppKey[cn].equals("email")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("type")) {	// 유형(과정): 일반회화, 비즈니스, ...																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());
									ErrInfo.append("등록실패. ( 유형: " + formatter.formatCellValue(cell).trim());
								}
								
								if(classAppKey[cn].equals("level")) {	// 레벨: Starter, Beginner, ...																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());
									ErrInfo.append(", 레벨: " + formatter.formatCellValue(cell).trim());
								}
								
								if(classAppKey[cn].equals("title")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());
									ErrInfo.append(", 과정명: " + formatter.formatCellValue(cell).trim() + " ) 을 확인해주세요.");
								}
								
								if(classAppKey[cn].equals("period_moths")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("class_week")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("class_time")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("start_dt")) {						
									String start_dt = formatter.formatCellValue(cell).trim();									
									start_dt = start_dt.substring(0, 10);									
									
									classAppMap.put(classAppKey[cn], start_dt);										
								}
								
								if(classAppKey[cn].equals("pick_time1")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("pick_time2")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("process")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("kind")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(classAppKey[cn].equals("com_name")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								if(classAppKey[cn].equals("req_content")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								if(classAppKey[cn].equals("eng_name")) {																
									classAppMap.put(classAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								classAppMap.put("role", "3");	// 학생 role						
								classAppMap.put("company_code", session.getAttribute("company_code")); 
								
							} // if(!formatter.formatCellValue(row.getCell(1)).equals("")) {
							
							
						} // for	
						
						
						
						if (classAppMap.containsKey("tel")) {
							// 1) 해당 연락처(코드) 유저 있는지 확인 후 없으면 insert					
							Map<String, Object> user = adminMapper.getUserByCode(classAppMap);	
							int user_no = 0;
							
							if(!MapUtils.isEmpty(user)) {	// 해당 코드(연락처) 유저가 있다면 
								// 2) user_no 가져오기
								user_no = Integer.parseInt(user.get("no").toString());
							} else {	// 신청서 - 해당 코드(연락처) 유저가 없다면 
								// 2) user 생성 
								adminMapper.inserUserByApplication(classAppMap);							
								user_no = adminMapper.selectLastAIUser() - 1;		// user_no (FK) 가져오기							
							}
							
							
							
							
							
							// 3) 신청서 (유형,레벨,과정명)에 따른 class_category No 가져오기   (로그인 고객사 티칭센터 company_code로 등록된 교재중에서..)
							class_category = adminMapper.getClassCategoryByApp(classAppMap);						
							
							if(!MapUtils.isEmpty(class_category)) {	// 해당 과정명이 있다면						
								classAppMap.put("class_category", class_category.get("class_category_no"));							
							} else {	 
								// 등록되어있는 과정(교재)이 없다면 예외처리 후 alert							
								throw new Exception();							
							}	
							
							// 3.5) 중복 신청내역 있는지 확인	
							classAppMap.put("user_no", user_no);
							duplCnt = adminMapper.getDuplClassAppCnt(classAppMap);					
							
							if (duplCnt > 0) {	// 중복 신청데이터 존재
								String name = classAppMap.get("name").toString();	
								String regDt = classAppMap.get("reg_dt").toString();
								String title = classAppMap.get("title").toString();
								String startDt = classAppMap.get("start_dt").toString();
								String classWeek = classAppMap.get("class_week").toString();
								
								
								ErrInfo.setLength(0);	// 에러 메시지 초기화
								ErrInfo.append("중복 유료수강신청 내역이 있습니다. ( 이름:" + name + "  신청날짜: " + regDt + "  과정명: " + title + "  시작일: " 
								              + startDt + "  수업주기: " + classWeek + " 데이터를 확인하세요.) ");
								throw new Exception();
							}
						
													
							// 4) 유료수강 신청서 insert @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@					
							adminMapper.insertClassAppication(classAppMap);
						}
						
						
						/*
						res.setContentType("text/html; charset=UTF-8");
						PrintWriter out = res.getWriter();						
						out.println("<script>alert('등록되었습니다.'); window.location.href='/company/classApplication'; </script>");
						out.flush();
						*/
						
					}	// 0번째 row 제외 -> 제목 부분					
					
				}	// 첫번째 시트	행 숫자만큼 반복
				
				
				
				
				// 두번째 시트 (무료 신청)
				// 무료레벨테스트 신청 정보
				String[] levelTestAppKey = {"flag", "reg_dt", "name", "tel", "email", "type", "level", "title", "pick_date", "pick_time1", "pick_time2", "process", "eng_name"};
				Map<String, Object> levelTestAppMap;
				
				// 두번째 시트	행 숫자만큼 반복 			
				for(Row row: wb.getSheetAt(1)) {	
					ErrInfo.setLength(0);	// 에러 메시지 초기화		
					int duplCnt = 0;
					Map<String, Object> class_category = new HashMap<String, Object>(); 
					
					// 0번째 row 제외 -> 제목 부분 
					if(row.getRowNum() != 0) {
						levelTestAppMap = new HashMap<String, Object>();
						
						for(int cn = 0; cn<levelTestAppKey.length; cn++) {
							Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);						
							
							if(!formatter.formatCellValue(row.getCell(0)).trim().equals("")) {							
								
								if(levelTestAppKey[cn].equals("flag")) {									
									String flag = formatter.formatCellValue(cell).trim();
									//String user_companyCode = "";
									String subject = "";
									
									//System.out.println(user_company_code);
									if ("오피아".equals(flag)){
										//user_companyCode = "CP_OPIA";
										subject = "English";										
									} else if("보라구영어".equals(flag)) {
										//user_companyCode = "CP_BORAGU";
										subject = "English";
									} else if("보라구중국어".equals(flag)) {
										//user_companyCode = "CP_BORAGU_CN";
										subject = "Chinese";
									} 
									levelTestAppMap.put("subject", subject);
																		
								}
								
								if(levelTestAppKey[cn].equals("reg_dt")) {
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(levelTestAppKey[cn].equals("name")) {
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());									
								}
								
								if(levelTestAppKey[cn].equals("tel")) {
									String tel = formatter.formatCellValue(cell).trim();
									//levelTestAppMap.put("tel", tel);
									levelTestAppMap.put("tel", tel.replace(" ", "").replace("-", ""));
									
									// 코드 생성 (연락처)
									levelTestAppMap.put("code", tel.replace(" ", "").replace("-", ""));	
									
									if (tel.replace(" ", "").replace("-", "").equals("")) { 
										// 비어있다면 예외 발생
										ErrInfo.append("등록실패. 연락처가 비어있습니다.");
										throw new Exception();
									}										
								}
								
								if(levelTestAppKey[cn].equals("email")) {																
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(levelTestAppKey[cn].equals("type")) {	// 유형(과정): 일반회화, 비즈니스, ...																
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());
									ErrInfo.append("등록실패. ( 유형: " + formatter.formatCellValue(cell).trim());
								}
								
								if(levelTestAppKey[cn].equals("level")) {	// 레벨: Starter, Beginner, ...																
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());
									ErrInfo.append(", 레벨: " + formatter.formatCellValue(cell).trim());
								}
								
								if(levelTestAppKey[cn].equals("title")) {																
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());
									ErrInfo.append(", 과정명: " + formatter.formatCellValue(cell).trim() + " ) 을 확인해주세요.");
								}
								
								if(levelTestAppKey[cn].equals("pick_date")) {						
									String start_dt = formatter.formatCellValue(cell).trim();
									start_dt = start_dt.substring(0, 10);										
									
									System.out.println(start_dt);
									levelTestAppMap.put(levelTestAppKey[cn], start_dt);										
								}
								
								if(levelTestAppKey[cn].equals("pick_time1")) {																
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(levelTestAppKey[cn].equals("pick_time2")) {																
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								
								if(levelTestAppKey[cn].equals("process")) {																
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
								if(levelTestAppKey[cn].equals("eng_name")) {																
									levelTestAppMap.put(levelTestAppKey[cn], formatter.formatCellValue(cell).trim());										
								}
											
								
								levelTestAppMap.put("role", "3");	// 학생 role						
								levelTestAppMap.put("company_code", session.getAttribute("company_code")); 
							
								
							} // if(!formatter.formatCellValue(row.getCell(1)).equals("")) {
							
							
						} // for
						
						
						
						if(levelTestAppMap.containsKey("tel")) {
							// 1) 해당 연락처(코드) 유저 있는지 확인 후 없으면 insert					
							Map<String, Object> user = adminMapper.getUserByCode(levelTestAppMap);	
							int user_no = 0;
							
							if(!MapUtils.isEmpty(user)) {	// 해당 코드(연락처) 유저가 있다면 
								// 2) user_no 가져오기
								user_no = Integer.parseInt(user.get("no").toString());
							} else {	// 신청서 - 해당 코드(연락처) 유저가 없다면 
								// 2) user 생성 
								adminMapper.inserUserByApplication(levelTestAppMap);							
								user_no = adminMapper.selectLastAIUser() - 1;		// user_no (FK) 가져오기							
							}
							
							levelTestAppMap.put("user_no", user_no);
							
							
							// 3) 신청서 (유형,레벨,과정명)에 따른 class_category No 가져오기   (로그인 고객사 티칭센터 company_code로 등록된 교재중에서..)
							class_category = adminMapper.getClassCategoryByApp(levelTestAppMap);	
							
							
							if(!MapUtils.isEmpty(class_category)) {	// 해당 과정명이 있다면						
								levelTestAppMap.put("class_category", class_category.get("class_category_no"));							
							} else {	 
								// 등록되어있는 과정(교재)이 없다면 예외처리 후 alert							
								throw new Exception();							
							}	
							
							System.out.println("2222222222222222222222222222222222");
													
							// 3.5) 중복 신청내역 있는지 확인
							duplCnt = adminMapper.getDuplLevelTestAppCnt(levelTestAppMap);					
							
							if (duplCnt > 0) {	// 중복 신청데이터 존재
								String name = levelTestAppMap.get("name").toString();	
								String regDt = levelTestAppMap.get("reg_dt").toString();
								String title = levelTestAppMap.get("title").toString();
								String pickDt = levelTestAppMap.get("pick_date").toString();							
								
								
								ErrInfo.setLength(0);	// 에러 메시지 초기화
								ErrInfo.append("중복 무료레벨테스트 신청 내역이 있습니다. ( 이름:" + name + "  신청날짜: " + regDt + "  과정명: " + title + "  희망일: " 
								              + pickDt + " 데이터를 확인하세요.) ");
								throw new Exception();
							}
										
							System.out.println(levelTestAppMap);
													
							// 4) 무료레벨테스트 신청서 insert @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@			
							adminMapper.insertLevelTestAppication(levelTestAppMap);							
						}
												
						
						
						
						
						
						
					}	// 0번째 row 제외 -> 제목 부분					
					
				}	// 두번째 시트	행 숫자만큼 반복
				
				
				// 유료신청, 무료신청 예외처리없이 다 등록되었다면	
				res.setContentType("text/html; charset=UTF-8");
				PrintWriter out = res.getWriter();					
				out.println("<script>alert('등록되었습니다.'); window.location.href='/company/"+returnPage+"'; </script>");
				out.flush();
				
				
			} catch (Exception e) {			
				e.printStackTrace();
				// 롤백				
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();			
				// alert 처리				
				res.setContentType("text/html; charset=UTF-8");
				PrintWriter out = res.getWriter();	
				//out.println("<script>alert('등록 실패. 연락처가 기존에 등록되어있거나, 칸이 비어있는지 확인해주세요.'); history.back(); </script>");
				out.println("<script>alert('"+ErrInfo+"'); history.back(); </script>");
				out.flush();
			}	
			
			
		} else {
			System.out.println("파일이 없습니다.");
		}				
		
		return mav;
	}
	
	
	
	
	
	
	
	
	
	
	/************************ 유료수강신청 엑셀 다운로드 ************************/
	@Transactional(rollbackFor=Exception.class)
	@ResponseBody
	@RequestMapping(value = "/classApplicationExcelDownload", method = RequestMethod.POST)
	public Map<String, Object> classApplicationExcel(@RequestParam Map<String, Object> paramMap, HttpServletResponse resp,  HttpServletRequest req){	
		Map<String, Object> excelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		Map<String, Object> tmpMap = null;
		int tot = 0;
		
		XSSFRow row = null;
		int rowNo = 0;
			
		
		String[][] mode = {			
			{"구분","신청날짜","이름","전화번호","E-mail","과정","레벨","수강 과정","수업기간","수업주기","수업시간","희망 시작일","시작시간 1지망","시작시간 2지망","수업방식","결제방식","수요기업명, 대표자명", "기타 요청사항", "영어 이름"},
			{"구분","신청날짜","이름","전화번호","E-mail","과정","레벨","수강 과정","희망일","시작시간 1지망","시작시간 2지망","수업방식","영어 이름"},
		};
		String[][] column = {			
			{"flag","reg_dt","name","tel","email","type","level","title","period_moths","class_week","class_time","start_dt","pick_time1","pick_time2","process","kind","com_name","req_content", "eng_name"},
			{"flag","reg_dt","name","tel","email","type","level","title","pick_date","pick_time1","pick_time2","process","eng_name"},
		};
		
		XSSFWorkbook wb = new XSSFWorkbook();
	
		
		XSSFSheet[] sheets = {			
			wb.createSheet("유료 신청"),
			wb.createSheet("무료 신청"),
		};
		
		for(int i=0; i<2; i++) {
			row = sheets[i].createRow(rowNo);
			for(int j=0; j<mode[i].length; j++) {
				row.createCell(j).setCellValue(mode[i][j]);
			}
		}
		
		
		
		//String tmp = "";	
		String[] selectedArr = null;
		Map<String, Object> selectMap = new HashMap<String, Object>();
		
		selectedArr = req.getParameterValues("selectedArr[]");	
		
		
		try {
			
			for(int i=0; i<2; i++) {
				rowNo = 1;						
				
				if(i==0) {
					excelMap.put("selectedArr", selectedArr);					
					list = new ArrayList<Map<String,Object>>();					
					list = adminMapper.classApplicationExcelDownload(excelMap);
				} else {
					list = new ArrayList<Map<String,Object>>();
				}
				
				
				//System.out.println(list);					
							
				
				for(Map<String, Object> map: list) {
					row = sheets[i].createRow(rowNo++);
					for(int j=0; j<mode[i].length; j++) {
						if(column[i][j].equals("flag")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("reg_dt")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("name")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("tel")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("email")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("type")) {							
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}
						else if(column[i][j].equals("level")) {							
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}
						else if(column[i][j].equals("title")) {							
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}
						else if(column[i][j].equals("period_moths")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("class_week")) {
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}
						}
						else if(column[i][j].equals("class_time")) {													
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}
						else if(column[i][j].equals("com_name")) {													
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}	
						else if(column[i][j].equals("req_content")) {													
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}
						else if(column[i][j].equals("eng_name")) {													
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}
						else {
							//System.out.println("JINJIN: "+map.get(column[i][j]));
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
						}
					}
				}
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BufferedOutputStream bos = null;
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		Map<String, Object> resultMap = new HashMap<>();		
		
		
		//////////////////////////////////////////
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
			s3Wrapper.uploadTest(contentLength, bi, "face_excel", "face_study_"+format.format(date)+".xlsx");
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
		///////////////////////////////////////////		

		
		
		String downloadURL = "https://edutemlms.s3.ap-northeast-2.amazonaws.com/face_excel/face_study_"+format.format(date)+".xlsx";	
			
		resultMap.put("resultCode", 1);
		resultMap.put("downloadURL", downloadURL);	
		
		return resultMap;	
	}
	
	
	
	
	
	/************************ 무료레벨테스트 신청 엑셀 다운로드 ************************/
	@Transactional(rollbackFor=Exception.class)
	@ResponseBody
	@RequestMapping(value = "/levelTestApplicationExcelDownload", method = RequestMethod.POST)
	public Map<String, Object> levelTestApplicationExcelDownload(@RequestParam Map<String, Object> paramMap, HttpServletResponse resp,  HttpServletRequest req){	
		Map<String, Object> excelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		Map<String, Object> tmpMap = null;
		int tot = 0;
		
		XSSFRow row = null;
		int rowNo = 0;
			
		
		String[][] mode = {			
			{"구분","신청날짜","이름","전화번호","E-mail","과정","레벨","수강 과정","수업기간","수업주기","수업시간","희망 시작일","시작시간 1지망","시작시간 2지망","수업방식","결제방식","수요기업명, 대표자명", "기타 요청사항", "영어 이름"},
			{"구분","신청날짜","이름","전화번호","E-mail","과정","레벨","수강 과정","희망일","시작시간 1지망","시작시간 2지망","수업방식", "영어 이름"},
		};
		String[][] column = {			
			{"flag","reg_dt","name","tel","email","type","level","title","period_moths","class_week","class_time","start_dt","pick_time1","pick_time2","process","kind","com_name","req_content", "eng_name"},
			{"flag","reg_dt","name","tel","email","type","level","title","pick_date","pick_time1","pick_time2","process", "eng_name"},
		};
		
		XSSFWorkbook wb = new XSSFWorkbook();
	
		
		XSSFSheet[] sheets = {			
			wb.createSheet("유료 신청"),
			wb.createSheet("무료 신청"),
		};
		
		for(int i=0; i<2; i++) {
			row = sheets[i].createRow(rowNo);
			for(int j=0; j<mode[i].length; j++) {
				row.createCell(j).setCellValue(mode[i][j]);
			}
		}
		
		
		
		//String tmp = "";	
		String[] selectedArr = null;
		Map<String, Object> selectMap = new HashMap<String, Object>();
		
		selectedArr = req.getParameterValues("selectedArr[]");	
		
		
		try {
			
			for(int i=0; i<2; i++) {
				rowNo = 1;						
				
				if(i==0) {										
					list = new ArrayList<Map<String,Object>>();					
					
				} else {
					list = new ArrayList<Map<String,Object>>();
					excelMap.put("selectedArr", selectedArr);
					list = adminMapper.levelTestApplicationExcelDownload(excelMap);
				}
				
				
				//System.out.println(list);					
							
				
				for(Map<String, Object> map: list) {
					row = sheets[i].createRow(rowNo++);
					for(int j=0; j<mode[i].length; j++) {
						if(column[i][j].equals("flag")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("reg_dt")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("name")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("tel")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("email")) {							
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));							
						}
						else if(column[i][j].equals("type")) {							
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}
						}
						else if(column[i][j].equals("level")) {							
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}
						else if(column[i][j].equals("title")) {							
							if(String.valueOf(map.get(column[i][j])).equals("null")) {
								row.createCell(j).setCellValue("");
							} else {
								row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
							}							
						}
						else if(column[i][j].equals("pick_date")) {
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
						}												
						else {
							//System.out.println("JINJIN: "+map.get(column[i][j]));
							row.createCell(j).setCellValue(String.valueOf(map.get(column[i][j])));
						}
					}
				}
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BufferedOutputStream bos = null;
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		Map<String, Object> resultMap = new HashMap<>();		
		
		
		//////////////////////////////////////////
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
			s3Wrapper.uploadTest(contentLength, bi, "face_excel", "face_study_"+format.format(date)+".xlsx");
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
		///////////////////////////////////////////		

		
		
		String downloadURL = "https://edutemlms.s3.ap-northeast-2.amazonaws.com/face_excel/face_study_"+format.format(date)+".xlsx";	
			
		resultMap.put("resultCode", 1);
		resultMap.put("downloadURL", downloadURL);	
		
		return resultMap;	
	}
	
	
	
	
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ saeha LMS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	/************************ 휴강일 엑셀업로드 등록 ************************/ 
	@Transactional(rollbackFor=Exception.class)
	@RequestMapping("/holidayExcel")
	public ModelAndView holidayExcel(MultipartFile mfile, HttpServletResponse res, @RequestParam Map<String, Object> paramMap) throws IOException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/admin/holiday/detail");	
		
		DataFormatter formatter = new DataFormatter();		
		
		// 등록 실패 이름, 연락처...
		StringBuffer ErrInfo = new StringBuffer();		
		
		if(!mfile.isEmpty()) {
			try {
				Workbook wb = WorkbookFactory.create(mfile.getInputStream());
				wb.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);	
				
				// 휴무일 정보
				String[] holidayKey = {"holiday_date", "holiday_name"};
				Map<String, Object> holidayMap;
				
				// 첫번째 시트	행 숫자만큼 반복 			
				for(Row row: wb.getSheetAt(0)) {	
					ErrInfo.setLength(0);	// 에러 메시지 초기화
					int duplCnt = 0;		// 중복 내역 초기화
					Map<String, Object> holiday = new HashMap<String, Object>();
					
					// 0번째 row 제외 -> 제목 부분 
					if(row.getRowNum() != 0) {
						holidayMap = new HashMap<String, Object>();
						
						for(int cn = 0; cn<holidayKey.length; cn++) {
							Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);						
							
							if(!formatter.formatCellValue(row.getCell(0)).trim().equals("")) {
								
								if(holidayKey[cn].equals("holiday_date")) {									
									holidayMap.put(holidayKey[cn], formatter.formatCellValue(cell).trim());
									ErrInfo.append("등록실패. ( 휴강일: " + formatter.formatCellValue(cell).trim() + " ) 을 확인해주세요. 이미 등록되어있는 휴강일입니다.");
								}
								
								if(holidayKey[cn].equals("holiday_name")) {									
									holidayMap.put(holidayKey[cn], formatter.formatCellValue(cell).trim());
									//ErrInfo.append(", 휴무일 명: " + formatter.formatCellValue(cell).trim() + " ) 을 확인해주세요. 이미 등록되어있는 휴무일입니다.");
								}
														
								holidayMap.put("language_type", paramMap.get("language_type")); 
								
							} // if(!formatter.formatCellValue(row.getCell(1)).equals("")) {
							
							
						} // for	
						
						
						
						if (holidayMap.containsKey("holiday_date")) {
							// 1) 중복되는 휴강일 날짜 있는지 확인 후 없으면 insert
							duplCnt = adminMapper.getDuplHoliday(holidayMap);
							if (duplCnt > 0) {	// 중복 신청데이터 존재								
								throw new Exception();
							}					
							
							// 2) 휴강일 insert					
							adminMapper.insertHoliday(holidayMap);
						}
						
					}	// 0번째 row 제외 -> 제목 부분					
					
				}	// 첫번째 시트	행 숫자만큼 반복
				
				
				
				// 예외처리없이 다 등록되었다면	
				res.setContentType("text/html; charset=UTF-8");
				PrintWriter out = res.getWriter();					
				out.println("<script>alert('등록되었습니다.'); window.location.href='/admin/holiday'; </script>");
				out.flush();
				
				
			} catch (Exception e) {			
				//e.printStackTrace();
				// 롤백				
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();			
				// alert 처리				
				res.setContentType("text/html; charset=UTF-8");
				PrintWriter out = res.getWriter();	
				//out.println("<script>alert('등록 실패. 연락처가 기존에 등록되어있거나, 칸이 비어있는지 확인해주세요.'); history.back(); </script>");
				out.println("<script>alert('"+ErrInfo+"'); history.back(); </script>");
				out.flush();
			}	
			
			
		} else {
			//System.out.println("파일이 없습니다.");			
			res.setContentType("text/html; charset=UTF-8");
			PrintWriter out = res.getWriter();					
			out.println("<script>alert('선택된 파일이 없습니다.'); history.back(); </script>");
			out.flush();
			
		}				
		
		return mav;
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
