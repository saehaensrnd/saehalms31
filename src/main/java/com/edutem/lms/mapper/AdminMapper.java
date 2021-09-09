package com.edutem.lms.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
	// security
	//public Map<String, Object> selectAdminById(String admin_id);
	//public String selectRoleByNo(Map<String, Object> paramMap);	
	
	
	// user 정보
	public Map<String, Object> selectUserInfo(int user_no);
	
		
	// code 로그인 (학생 로그인)(사용안함)
	public Map<String, Object> selectUserByCode(Map<String, Object> paramMap);
	
	
	
	// id, pw 로그인	( 로그인페이지 같은 경우 ) 
	public Map<String, Object> selectUserById(Map<String, Object> paramMap);	
	
	public Map<String, Object> selectUserByAdmin(Map<String, Object> paramMap);			// 관리자 로그인페이지에서 사용
	public Map<String, Object> selectUserByStudent(Map<String, Object> paramMap);		// 학생 로그인페이지에서 사용
	
	
	// 중복 아이디 체크
	public int idCheck(String user_id);
	
	
	
	
	public void setRownum();
	
	/************************ 학생 로그인 *****************************/	
	// 무료 레벨 테스트 정보 가져오기
	public Map<String, Object> getLevelTestLogByStudent(Map<String, Object> paramMap);
	
	
	
	
	
	/************************ 강사 로그인 *****************************/
	// 강사 수업현황
	public int countClass(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClass(Map<String, Object> paramMap);
	
	// 수업일정 - 해당학생 수업일정 : 중간에 강사가 바뀔수도있어서 class_log에 teacher_no 까지 걸어줘야함
	public List<Map<String, Object>> selectClassLogByTeacher(Map<String, Object> paramMap);
	
		

	// 담당 학생 리스트 (콤보박스)
	public List<Map<String, Object>> selectStudentComboByTeacher(Map<String, Object> paramMap);	
	
	// @무료 레벨테스트
	// 레벨테스트 신청자 조회 - 무료 레벨테스트 강사 배정받은 화면 조회	
	public int countLevelTest(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectLevelTest(Map<String, Object> paramMap);
	
	
	
	
	// 강사 - 해당 학생 무료 레벨 테스트 정보 가져오기
	public Map<String, Object> getLevelTestLog(Map<String, Object> paramMap);
	
	public void insertLevelTestLogByTeacher(Map<String, Object> paramMap);	// 무료 레벨테스트 결과 insert
	public void updateLevelTestLogByTeacher(Map<String, Object> paramMap);	// 무료 레벨테스트 결과 update
	
	
	// 수업피드백 등록/수정
	public Map<String, Object> getClassLog(Map<String, Object> paramMap);	
	public void updateClassLogByTeacher(Map<String, Object> paramMap);		// 일일 피드백 결과 update
	
	// 학생 영어이름(수업용이름) update
	public void updateUserEnglishName(Map<String, Object> paramMap);	
	
	
	// 금주 날짜 정보 가져오기
	public Map<String, Object> getWeeklyDays(Map<String, Object> paramMap);
	public Map<String, Object> selectClassLogExist(Map<String, Object> paramMap);
	
	// Weekly Schedule
	public List<Map<String, Object>> selectWeeklySchedule(Map<String, Object> paramMap);
	public int countWeeklySchedule(Map<String, Object> paramMap);
	
	public Map<String, Object> selectClassLogExistToday(Map<String, Object> paramMap);
	
	
	
	
	
	/************************ 티칭센터 관리자 로그인 *****************************/
	// Today, Weekly Schedule 수강내역들
	public List<Map<String, Object>> selectWeeklyScheduleByCenter(Map<String, Object> paramMap);
	public int countWeeklyScheduleByCenter(Map<String, Object> paramMap);	
	
	public List<Map<String, Object>> selectStudentComboByCenter(Map<String, Object> paramMap);
	
	
	// 강사등록
	public int countTeacher(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTeacher(Map<String, Object> paramMap);
	
	public void updateTeacher(Map<String, Object> paramMap);
	public void deleteTeacher(Map<String, Object> paramMap);
	
	
	// 휴무일등록
	public int countHoliday(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectHoliday(Map<String, Object> paramMap);	
	public Map<String, Object> holidayCheck(Map<String, Object> paramMap);			// 휴무일 중복여부		
	
	
	public int getDuplHolidayCnt(Map<String, Object> paramMap);						// 휴무일 엑셀 등록 (중복확인)	
	
	public List<Map<String, Object>> selectHolidayByCenter(Map<String, Object> paramMap);		// 강사 배정 전 해당 티칭센터 휴무일 리스트 가져오기
	
	
	
	// 유료수강자 강사 배정	
	public int countClassApp(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassApp(Map<String, Object> paramMap);
	
	// 유료수강자 강사 배정 Detail
	public Map<String, Object> getClassAppInfo(Map<String, Object> paramMap);		// class 생성 전 - 신청서 정보 가져오기
	public Map<String, Object> getClassAssignInfo(Map<String, Object> paramMap);	// class 생성 후 - 과정 정보 가져오기
	
	public Map<String, Object> getClassCategoryNo(Map<String, Object> paramMap);	// class 생성 전 - 선택된 셀렉트박스 과정명으로 class_category NO 가져오기
	
	// 유료수강자 강사배정 - 회원정보 상세보기 - 수정
	public void updateUserByCenter(Map<String, Object> paramMap);					// 회원정보 수정
	
	
	
	
	public void insertClass(Map<String, Object> paramMap);							// 유료수강자 강사배정 - class insert
	public void tempSaveClass(Map<String, Object> paramMap);						// 유료수강자 강사배정 - class tempSave 임시저장
	public void tempUpdateClass(Map<String, Object> paramMap);						// 유료수강자 강사배정 - class tempUpdate 임시저장 - update문
	
	public void updateClass(Map<String, Object> paramMap);						// 유료수강자 강사배정 - class tempUpdate 임시저장 - update문
	
	public int selectLastAIClass();
	
	public void updateClassByAssign(Map<String, Object> paramMap);					// 유료수강자 강사배정 변경 - class: teacher_no, pick_time1, pick_time2 변경
	public void updateClassLogByAssign(Map<String, Object> paramMap);				// 유료수강자 강사배정 변경 - class_log: teacher_no, class_time 변경 (status 0인 log들만 -> 출석완료 말고 수업예정일들만 변경 가능)
	
	// 유료수강 강사배정변경 - 요일, 수강일 변경 관련
	public void deleteClassLogByAssign(Map<String, Object> paramMap);	
	
	public Map<String, Object> getClassLogTest(Map<String, Object> paramMap);
	public void updateEndDt(Map<String, Object> paramMap);
	public void deleteClassLogByStartDt(Map<String, Object> paramMap);
	public void deleteClassLogByEndDt(Map<String, Object> paramMap);
	
	// 레벨테스트 신청자 강사 배정	
	public int countLevelTestApp(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectLevelTestApp(Map<String, Object> paramMap);
	

	// 레벨테스트 신청자 강사 배정 Detail
	public Map<String, Object> getLevelTestAppInfo(Map<String, Object> paramMap);		// level_test_log 생성 전 - 신청서 정보 가져오기
	public Map<String, Object> getLevelTestLogInfo(Map<String, Object> paramMap);		// level_test_log 생성 후 - log 정보 가져오기
	
	public void insertLevelTestAssign(Map<String, Object> paramMap);					// level_test_log 레벨테스트 강사배정 insert
	public void updateLevelTestAssign(Map<String, Object> paramMap);					// level_test_log 레벨테스트 강사배정 updtae
	
	
	public List<Map<String, Object>> selectPayTeacherComboByCenter(Map<String, Object> paramMap);	//  해당 티칭센터의 유료수업 강사 param: company_code	
	public List<Map<String, Object>> selectTeacherComboByClassAssign(Map<String, Object> paramMap);	//  해당 티칭센터의 유료수업, (화상 or 전화) 강사 param: company_code, process
	
	public List<Map<String, Object>> selectTeacherComboByCenter(Map<String, Object> paramMap);		// 해당 티칭센터 강사 리스트 (콤보박스) param: company_code
	public List<Map<String, Object>> selectClassCategoryCombo();									// 과정 리스트 (콤보박스용)
	
	public List<Map<String, Object>> selectCategoryComboByCenter(Map<String, Object> paramMap);		// 해당 company_code로 등록되어있는 구분 리스트 (오피아,보라구영어 / 보라구중국어 ... )
	public List<Map<String, Object>> selectTypeComboByCenter(Map<String, Object> paramMap);			// 해당 company_code, category로 등록되어있는 유형 리스트 (일반회화, 토론, 비즈니스, 뉴스, ...)
	public List<Map<String, Object>> selectLevelComboByCenter(Map<String, Object> paramMap);		// 해당 company_code, category, type으로 등록되어있는 레벨 리스트 (Starter, Beginner, Intermediate, ...)
	public List<Map<String, Object>> selectTitleComboByCenter(Map<String, Object> paramMap);		// 해당 company_code, category, type으로 등록되어있는 레벨 리스트 (Starter, Beginner, Intermediate, ...)
	
	
	// 교재등록
	public List<Map<String, Object>> selectTextbook(Map<String, Object> paramMap);
	public int countTextbook(Map<String, Object> paramMap);
	public int selectAItextbook();
	public void addTextbook(Map<String, Object> paramMap);
	
	// 교재 사용 고객사 구분 리스트 (콤보박스용)	// param: company_code
	public List<Map<String, Object>> selectTextbookCategoryCombo(Map<String, Object> paramMap);

	public List<Map<String, Object>> selectTextbookByStudent(Map<String, Object> paramMap);
	
	// 교재 상세보기 정보
	public Map<String, Object> getTextbook(Map<String, Object> paramMap);	
	
	// 강사별 수업현황
	public int countClassByCenter(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassByCenter(Map<String, Object> paramMap);	
	
	// Weekly Schedule
	public Map<String, Object> getWeeklyDaysFromSun(Map<String, Object> paramMap);			// 일요일시작 이번주 날짜 가져오기   param: thisDay (기준일)
	
	
	
	
	// Weekly Schedule 상세보기	
	public void updateUserByClassDetail(Map<String, Object> paramMap);	// 회원기본정보	수정
	public int countClassByUserNo(Map<String, Object> paramMap);		 
	public List<Map<String, Object>> selectClassByUserNo(Map<String, Object> paramMap);	// 해당 회원 수강목록	
	public Map<String, Object> getClassDetailInfo(Map<String, Object> paramMap);
	public Map<String, Object> getLevelLogInfo(Map<String, Object> paramMap);
	public Map<String, Object> getLevelDetailInfo(Map<String, Object> paramMap);
	
	
	
	

	
	/************************ 공지사항 *****************************/
	// 학생공지사항 추가
	public List<Map<String, Object>> selectNoticeAllByStudent(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectShowNoticeAllByStudent(Map<String, Object> paramMap);
	public int countShowNoticeByStudent(Map<String, Object> paramMap);
	public int countNoticeByStudent(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectNoticeSpecialByStudent(Map<String, Object> paramMap);
	
	
	public List<Map<String, Object>> selectNoticeSpecial(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectNoticeAll(Map<String, Object> paramMap);
	public int countNotice(Map<String, Object> paramMap);
	public Map<String, Object> selectNoticeOne(Map<String, Object> paramMap);
	public void insertNotice(Map<String, Object> paramMap);
	public void updateNotice(Map<String, Object> paramMap);								// 공지사항 update
	public void deleteNotice(Map<String, Object> paramMap);								// 공지사항 delete
	
	
	// 슈퍼관리자 공지사항
	public List<Map<String, Object>> selectNoticeSpecialByAdmin();
	

	/************************ 1:1문의 *****************************/
	public void addInquiry(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectInquiryAll(Map<String, Object> paramMap);
	public int countInquiry(Map<String, Object> paramMap);
	public Map<String, Object> selectInquiryOne(Map<String, Object> paramMap);
	public void updateInquiry(Map<String, Object> paramMap);
	
	
	
	/************************ 고객사 관리자(OPIA) 로그인 *****************************/
	
	public List<Map<String, Object>> selectCategoryComboByCompany(Map<String, Object> paramMap);	// 해당 고객사의 티칭센터 company_cde로 등록되어있는 구분 리스트 (오피아,보라구영어 / 보라구중국어 ... )
	public Map<String, Object> getCenterCodeByCategory(Map<String, Object> paramMap);
	
	public Map<String, Object> getClassCategoryNoByApplication(Map<String, Object> paramMap);
	public Map<String, Object> getClassApplication(Map<String, Object> paramMap);					// 유료수강신청 정보 가져오기 (수정)
	
	public Map<String, Object> getLevelTestApplication(Map<String, Object> paramMap);				// 레벨테스트신청 정보 가져오기 (수정)
	
	// 회원현황
	public int countUser(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectUser(Map<String, Object> paramMap);	
	public Map<String, Object> selectUserOne(Map<String, Object> paramMap);		// 회원 상세보기	
	public void deleteUserByCompany(Map<String, Object> paramMap);				// 회원삭제
	
	
	public Map<String, Object> getTcLoginInfo(Map<String, Object> paramMap);	// 티칭센터 로그인정보
	
	// 유료수강신청	
	public int countClassAppByCompany(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassAppByCompany(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> selectCenterComboByCompany(Map<String, Object> paramMap);	// 그룹되어있는 티칭센터 리스트 (콤보박스용)

	// 신청서 엑셀등록 	
	public Map<String, Object> getUserByCode(Map<String, Object> paramMap);			// 신청서 코드(연락처)로 생성된 유저 있는지 확인용
	public void inserUserByApplication(Map<String, Object> paramMap);				// 신청서 - 해당 코드(연락처) 유저가 없다면 user insert
	
	// 신청서 (유형,레벨,과정명)에 따른 class_category No 가져오기   (로그인 고객사 티칭센터 company_code로 등록된 교재중에서..)
	public Map<String, Object> getClassCategoryByApp(Map<String, Object> paramMap);	
	public void insertClassAppication(Map<String, Object> paramMap);				// 유료수강 신청서 insert
	public void updateClassAppication(Map<String, Object> paramMap);				// 유료수강 신청서 update
	public void deleteClassAppication(Map<String, Object> paramMap);				// 유료수강 신청서 delete
	
	public Map<String, Object> getClassExist(Map<String, Object> paramMap);
	
	// 유료수강신청 엑셀 다운로드
	public List<Map<String, Object>> classApplicationExcelDownload(Map<String, Object> paramMap);
	// 유료수강신청 - 회원정보 조회 및 수정
	public Map<String, Object> getUserByUserNo(Map<String, Object> paramMap);	
	public void updateUserByCompany(Map<String, Object> paramMap);				// 회원정보 수정
	
	public void updateTcCodeByCompany(Map<String, Object> paramMap);			// 고객사 관리자 - 유료수강신청 - 티칭센터 매칭 수정
	public void updateLevelAppTcCodeByCompany(Map<String, Object> paramMap);	// 고객사 관리자 - 레벨테스트신청 - 티칭센터 매칭 수정
	
	// 엑셀 신청등록 시 중복 신청내역 확인용
	public int getDuplClassAppCnt(Map<String, Object> paramMap);			// 유료수강 신청 중복확인
	public int getDuplLevelTestAppCnt(Map<String, Object> paramMap);		// 무료레벨테스트 신청 중복확인
	
	
	
	// 레벨테스트 신청
	public int countlevelTestAppByCompany(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectlevelTestAppByCompany(Map<String, Object> paramMap);
	public void insertLevelTestAppication(Map<String, Object> paramMap);			// 무료레벨테스트 신청서 insert
	public void updateLevelTestAppication(Map<String, Object> paramMap);			// 무료레벨테스트 신청서 update
	public void deleteLevelTestAppication(Map<String, Object> paramMap);			// 무료레벨테스트 신청서 delete
	
	public Map<String, Object> getLevelTestExist(Map<String, Object> paramMap);
	
	// 무료레벨테스트 신청 엑셀 다운로드
	public List<Map<String, Object>> levelTestApplicationExcelDownload(Map<String, Object> paramMap);
	
	
	
	// 공지사항 - 티칭센터와 그룹인 고객사 리스트 (동적 체크박스용)	// param: company_code (tc_code)
	public List<Map<String, Object>> selectCompanyCheckbox(Map<String, Object> paramMap);
	
	// 1:1문의
	public List<Map<String, Object>> selectInquiryAllByCompany(Map<String, Object> paramMap);
	public int countInquiryByCompany(Map<String, Object> paramMap);
	
	// 강사별 수업현황
	public int countClassByCompany(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassByCompany(Map<String, Object> paramMap);

	
	
	/************************ 슈퍼관리자(에듀템) 로그인 *****************************/
	public List<Map<String, Object>> selectNoticeAllByAdmin(Map<String, Object> paramMap);
	public int countNoticeByAdmin(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectCompanyCheckboxByAdmin(Map<String, Object> paramMap); 		//  고객사 리스트 (동적 체크박스용)
	
	// 유료수강신청
	public int countClassAppByAdmin(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassAppByAdmin(Map<String, Object> paramMap);
	
	// 레벨테스트 신청
	public int countlevelTestAppByAdmin(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectlevelTestAppByAdmin(Map<String, Object> paramMap);
	
	// 레벨테스트 신청현황 무료 레벨 테스트 정보 가져오기
	public Map<String, Object> getLevelTestLogByAdmin(Map<String, Object> paramMap);
	
	// 교재조회
	public List<Map<String, Object>> selectTextbookByAdmin(Map<String, Object> paramMap);
	public int countTextbookByAdmin(Map<String, Object> paramMap);
	
	// 강사현황
	public int countTeacherByAdmin(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTeacherByAdmin(Map<String, Object> paramMap);
	
	// 강사별 수업현황
	public int countClassByAdmin(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassByAdmin(Map<String, Object> paramMap);

	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@	
	public int selectLastAIUser();
	public Map<String, Object> getUserByLoginNo(String user_no);
	
	// user_student
	public List<Map<String, Object>> selectStudentAll(Map<String, Object> paramMap);
	public int countStudentAll(Map<String, Object> paramMap);
	public void deleteUser(Map<String, Object> paramMap);
	public void restoreUser(Map<String, Object> paramMap);
	public Map<String, Object> selectStudentOne(Map<String, Object> paramMap);
	public void insertUser(Map<String, Object> paramMap);
	public int selectUserPK();
	public void insertUserStudent(Map<String, Object> paramMap);
	public void updateUser(Map<String, Object> paramMap);
	public void updateUserStudent(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectUserStudentExcel(Map<String, Object> paramMap);
	public List<Integer> selectUserStatus1();
	
	// user
	public int userIDCheck(Map<String, Object> paramMap);
	
	// site
	public List<Map<String, Object>> selectSiteList();
	
	public List<Map<String, Object>> selectClassRoomAll(Map<String, Object> paramMap);
	public int countClassRoomAll(Map<String, Object> paramMap);
	
	// 강사정보조회
	public List<Map<String, Object>> selectTeacherAll(Map<String, Object> paramMap);
	public int countTeacherAll(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectCenterCombo();
	public void deleteTimeTable(Map<String, Object> paramMap);
	public void insertTimeTable(Map<String, Object> paramMap);
	public List<String> getTimeTable(Map<String, Object> paramMap);
	public void insertUserByTeacher(Map<String, Object> paramMap);
	public void insertTeacher(Map<String, Object> paramMap);
	public Map<String, Object> selectTeacherOne(Map<String, Object> paramMap);
	public void updateUserByTeacher(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTeacherCombo();
	public void deleteTeacherCheckedItem(int userNo);							// 체크선택 삭제
	public List<Map<String, Object>> selectTeacherComboByCenterNo(Map<String, Object> paramMap);
	public List<Map<String, Object>> getTeacherHoliday(Map<String, Object> paramMap);
	public void deleteTeacherHoliday(Map<String, Object> paramMap);
	public void insertTeacherHoliday(Map<String, Object> paramMap);
	public int getDuplTeacherHoliday(Map<String, Object> paramMap);	
	public Map<String, Object> getTeacherInfo(Map<String, Object> paramMap);
	
	// 티칭센터정보조회
	public List<Map<String, Object>> selectCenterAll(Map<String, Object> paramMap);
	public int countCenterAll(Map<String, Object> paramMap);
	public Map<String, Object> selectCenterOne(Map<String, Object> paramMap);	
	public Map<String, Object> getCenterCodeMaxNumber();
	public void insertCenter(Map<String, Object> paramMap);
	public void updateCenter(Map<String, Object> paramMap);
	
	// 사이트정보조회
	public List<Map<String, Object>> selectSiteAll(Map<String, Object> paramMap);
	public int countSiteAll(Map<String, Object> paramMap);
	public Map<String, Object> selectSiteOne(Map<String, Object> paramMap);	
	public Map<String, Object> getSiteCodeMaxNumber();
	public void insertSite(Map<String, Object> paramMap);
	public void updateSite(Map<String, Object> paramMap);
	public int selectLastAISite();
	public int selectSiteLeveltestLimit(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTeacherGroupCombo();
	public int getDuplSiteName(Map<String, Object> paramMap);
	
	// 수강과정
	public List<Map<String, Object>> selectStudyAll(Map<String, Object> paramMap);
	public int countStudyAll();
	public Map<String, Object> selectStudyOne(Map<String, Object> paramMap);
	public void insertStudy(Map<String, Object> paramMap);
	public int selectStudyPK();
	public void updateStudy(Map<String, Object> paramMap);
	public void deleteStudy(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStudyLang(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStudyHaveLevel();
	
	// 수강과정 레벨
	public List<Map<String, Object>> selectStudyLevelAll(Map<String, Object> paramMap);
	public int selectStudyLevelMax(Map<String, Object> paramMap);
	public void insertStudyLevel(Map<String, Object> paramMap);
	public void updateStudyLevel(Map<String, Object> paramMap);
	public void deleteStudyLevel(Map<String, Object> paramMap);

	// 수강과정 사용하지 않을 사이트
	public void insertStudyNotUsedSite(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStudyNotUsedSite(Map<String, Object> paramMap);
	public void deleteStudyNotUsedSite(Map<String, Object> paramMap);
	
	// 수업시작시간관리
	public List<Map<String, Object>> selectStartTimeAll(Map<String, Object> paramMap);
	public int countStartTimeAll(Map<String, Object> paramMap);
	public int getStartTimeDuplCnt(Map<String, Object> paramMap);
	public Map<String, Object> selectStartTimeOne(Map<String, Object> paramMap);
	public void insertStartTime(Map<String, Object> paramMap);
	public int selectLastAIStudyStartTime();
	public void insertStartTimeLog(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStartTimeLogAll(Map<String, Object> paramMap);
	public Map<String, Object> getStudyStartTime(Map<String, Object> paramMap);
	public void deleteStartTimeLog(Map<String, Object> paramMap);
	public void deleteStartTimeCheckedItem(int study_start_time_no);
	
	// 휴강일
	public List<String> selectHolidayAll(Map<String, Object> paramMap);
	public int countHolidayAll(Map<String, Object> paramMap);
	public Map<String, Object> selectHolidayOne(Map<String, Object> paramMap);
	public int getDuplHoliday(Map<String, Object> paramMap);
	public void insertHoliday(Map<String, Object> paramMap);
	public void updateHoliday(Map<String, Object> paramMap); 
	public void deleteHolidayCheckedItem(int holiday_no);
	
	// 수업일정
	public List<String> selectClassScheduleAll(Map<String, Object> paramMap);
	public int countClassScheduleAll(Map<String, Object> paramMap);
	public int getDuplClassSchedule(Map<String, Object> paramMap);
	public void insertClassSchedule(Map<String, Object> paramMap);
	public Map<String, Object> selectClassScheduleOne(Map<String, Object> paramMap);
	public void updateClassSchedule(Map<String, Object> paramMap);
	public void deleteClassSchedule(Map<String, Object> paramMap);
	// 수업시작일 수업종료일 관련 체크 추가 - 해당언어 다른 기수년월에 이미 등록하려는 수업시작일~수업종료일 사이 날짜를 포함하고있다면 등록 실패
	public int getContainClassSchedule(Map<String, Object> paramMap);
	public int getContainReAppCntClassSchedule(Map<String, Object> paramMap);
	public int getContainAppCntClassSchedule(Map<String, Object> paramMap);
	
	public int getContainClassScheduleByUpdate(Map<String, Object> paramMap);
	public int getContainReAppCntClassScheduleByUpdate(Map<String, Object> paramMap);
	public int getContainAppCntClassScheduleByUpdate(Map<String, Object> paramMap);
	
	// 사이트별 수강신청
	public List<String> selectSiteClassScheduleAll(Map<String, Object> paramMap);
	public int countSiteClassScheduleAll(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectSemesterYmComboByLanguage(Map<String, Object> paramMap);
	public int getDuplClassScheduleSite(Map<String, Object> paramMap);
	public void insertClassScheduleSite(Map<String, Object> paramMap);
	public Map<String, Object> selectClassScheduleSiteOne(Map<String, Object> paramMap);
	public void updateClassScheduleSite(Map<String, Object> paramMap);
	public void deleteClassScheduleSite(Map<String, Object> paramMap);	
	// 수업시작일 수업종료일 관련 체크 추가 - 해당언어 다른 기수년월에 이미 등록하려는 수업시작일~수업종료일 사이 날짜를 포함하고있다면 등록 실패
	public List<Map<String, Object>> selectScheduleClassSite(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectProductBySite(Map<String, Object> paramMap);   // 해당 사이트, 해당 언어로 등록된 수강상품이 하나도 없는지 체크
	public int getContainReAppCntClassScheduleBySite(Map<String, Object> paramMap);
	public int getContainAppCntClassScheduleBySite(Map<String, Object> paramMap);
	public int getContainReAppCntClassScheduleByUpdateSite(Map<String, Object> paramMap);
	public int getContainAppCntClassScheduleByUpdateSite(Map<String, Object> paramMap);
	
	
	
	// 수업기본정보
	public List<Map<String, Object>> selectClassInfoAll(Map<String, Object> paramMap);
	public int countClassInfoAll(Map<String, Object> paramMap);
	public Map<String, Object> selectClassInfoOne(Map<String, Object> paramMap);	
	public List<Map<String, Object>> selectStudentInfoAll(Map<String, Object> paramMap);
	public void deleteClass(Map<String, Object> paramMap);
	public List<Map<String, Object>> getClassSel(Map<String, Object> paramMap);
	public int selectClassPK();
	public void insertNewClass(Map<String, Object> paramMap);
	public void insertClassStudent(Map<String, Object> paramMap);
	public int selectClassStudentPK();
	public void updateClassDetail(Map<String, Object> paramMap);
	public void updateClassStudentDetail(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassInfoExcel(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassInfoStudentAll(Map<String, Object> paramMap);
	public Map<String, Object> selectLevelTestInfoOne(Map<String, Object> paramMap);
	public void deleteClassStudent(Map<String, Object> paramMap);
	public void deleteClassLogTermChange(Map<String, Object> paramMap);
	
	// SMS
	public Map<String, Object> getUserByUserStudentNo(String user_student_no);
	public int countSmsLogByStudentNo(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectSmsLogByStudentNo(Map<String, Object> paramMap);
	
	// 회원 상세보기 팝업
	public Map<String, Object> getUserByStudentNo(Map<String, Object> paramMap);
	public int countClassAndLevelByStudentNo(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassAndLevelByStudentNo(Map<String, Object> paramMap);		// 해당 회원 수강목록 + 레벨테스트 목록
	public void updateUserByUserDetail(Map<String, Object> paramMap);
	
	
	// 교재리스트
	public List<Map<String, Object>> selectTextbookAll(Map<String, Object> paramMap);
	public int countTextbookAll(Map<String, Object> paramMap);
	public void insertTextbook(Map<String, Object> paramMap);
	public int selectTextbookPK();
	public Map<String, Object> selectTextbookOne(Map<String, Object> paramMap);
	public void updateTextbook(Map<String, Object> paramMap);
	public void deleteTextbook(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTextbookByStudy(Map<String, Object> paramMap);
	
	// 교재 토픽리스트
	public List<Map<String, Object>> selectTextbookTopic(Map<String, Object> paramMap);
	public int selectTextbookTopicPK();
	public int selectTextbookTopicUnitMax(Map<String, Object> paramMap);
	public void insertTextbookTopic(Map<String, Object> paramMap);
	public void updateTextbookTopic(Map<String, Object> paramMap);
	public Map<String, Object> selectTextbookTopicOne(Map<String, Object> paramMap);
	public void deleteTextbookTopic(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTextbookTopicHavePage(Map<String, Object> paramMap);
	
	// 교재 페이지
	public int countTextbookPage(Map<String, Object> paramMap);
	public void insertSmslog(Map<String, Object> paramMap);
	
	// 선생님별 수업일정
	public List<Map<String, Object>> getClassStudentList(Map<String, Object> paramMap);
	public List<Map<String, Object>> getLevelTestStudentList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getClassInfoList(Map<String, Object> paramMap);		// teacher_no, 날짜, 시간으로 class_log 정보 가져온다	
	public List<Map<String, Object>> getLevelTestInfoList(Map<String, Object> paramMap);	// teacher_no, 날짜, 시간으로 level_test_log 정보 가져온다
	
	public List<Map<String, Object>> selectTextbookPage(Map<String, Object> paramMap);
	public int selectTextbookPagePK();
	public int selectTextbookPagePageMax(Map<String, Object> paramMap);
	public void insertTextbookPage(Map<String, Object> paramMap);
	public Map<String, Object> selectTextbookPageOne(Map<String, Object> paramMap);
	public void updateTextbookPage(Map<String, Object> paramMap);
	public void deleteTextbookPage(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTextbookPageLimit(Map<String, Object> paramMap);
	
	// 휴강 및 보강
	public List<Map<String, Object>> selectPostponeAll(Map<String, Object> paramMap);
	public int countPostponeAll(Map<String, Object> paramMap);
	public Map<String, Object> selectPostponeOne(Map<String, Object> paramMap);
	public void updatePostpone(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectPostponeExcel();
	
	// 수강자 정보
	public List<Map<String, Object>> selectClassStudentAll(Map<String, Object> paramMap);
	public int countClassStudentAll(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassStudentExcel(Map<String, Object> paramMap);
	
	// 시간별 수업현황
	public List<Map<String, Object>> selectTimeClassAll(Map<String, Object> paramMap);
	public int countTimeClassAll(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTimeClassExcel(Map<String, Object> paramMap);
	// report 팝업
	public Map<String, Object> getReportClassInfo(Map<String, Object> paramMap);
	public Map<String, Object> getLastBook(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTextbookCombo(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTextbookTopicCombo(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTextbookPageCombo(Map<String, Object> paramMap);
	public void updateClassLogPage(Map<String, Object> paramMap);	
	public List<Map<String, Object>> selectClassScoreCombo(Map<String, Object> paramMap);	// report 팝업 - report 탭메뉴
	public Map<String, Object> selectClassScoreOne(Map<String, Object> paramMap);
	public void updateReport(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassStudentCombo(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassHistoryAll(Map<String, Object> paramMap);
	public int countClassHistoryAll(Map<String, Object> paramMap);
	public Map<String, Object> getRegisterInfo(Map<String, Object> paramMap);
	public void completeReport(Map<String, Object> paramMap);
	public Map<String, Object> getRegisterLevelTestInfo(Map<String, Object> paramMap);
	public void completeLevelTestReport(Map<String, Object> paramMap);
	public void makeupReport(Map<String, Object> paramMap);
	public void cancelMakeupReport(Map<String, Object> paramMap);
	public Map<String, Object> getTodayBook(Map<String, Object> paramMap);
	
	// report 팝업 leveltest
	public Map<String, Object> selectLevelTestOne(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStudyComboByLanguage(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStudyLevelCombo(Map<String, Object> paramMap);
	public void updateReportLevelTest(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectLevelTestHistoryAll(Map<String, Object> paramMap);
	public int countLevelTestHistoryAll(Map<String, Object> paramMap);
	
	// 레벨테스트 신청
	public List<Map<String, Object>> selectLevelTestAll(Map<String, Object> paramMap);
	public int countLevelTestAll(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectLevelTestExcel(Map<String, Object> paramMap);
	public void insertLeveltest(Map<String, Object> paramMap);
	public int countUserLeveltest(Map<String, Object> paramMap);
	public void updateLeveltestDetail(Map<String, Object> paramMap);
	public void deleteLeveltest(Map<String, Object> paramMap);
	
	// 수강상품
	public List<Map<String, Object>> selectProductAll(Map<String, Object> paramMap);
	public int countProductAll(Map<String, Object> paramMap);
	public Map<String, Object> selectProductOne(Map<String, Object> paramMap);
	public void insertProduct(Map<String, Object> paramMap);
	public int selectProductPK();
	public void updateProduct(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectProductMakeClass(Map<String, Object> paramMap);
	
	// 신청내역
	public List<Map<String, Object>> selectPaymentAll(Map<String, Object> paramMap);
	public int countPaymentAll(Map<String, Object> paramMap);
	public void paymentStatus(Map<String, Object> paramMap);
	
	// ROOM_NO
	public int selectROOMPK();
	public void insertROOM();
	
	// 강사 배정 시간대 구하기
	public Map<String, Object> selectClassTutor(Map<String, Object> paramMap);
	public Map<String, Object> selectLeveltestTutor(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectCenterTutor();
	public List<Map<String, Object>> selectGroupTutor();
	public List<Map<String, Object>> selectTeacherCenterClass(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTeacherGroupClass(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTeacherCenterLeveltest(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTeacherGroupLeveltest(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStudyTutor(String language);
	public List<Map<String, Object>> selectTextbookStudy(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectHolidayByLang(Map<String, Object> paramMap);
	public int countTeacherTimetable(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClasslogTeacher(Map<String, Object> paramMap);
	public Map<String, Object> selectSetTutorInfoClass(Map<String, Object> paramMap);
	public Map<String, Object> selectSetTutorInfoGroup(Map<String, Object> paramMap);
	public void insertClassLogFirst(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassLogClass(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassLogSeachDate(Map<String, Object> paramMap);
	public void insertClassScore(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectLeveltestLogTeacher(Map<String, Object> paramMap);
	
	// 강사 배정
	public Map<String, Object> selectClassOne(Map<String, Object> paramMap);
	public void updateClassSetTutor(Map<String, Object> paramMap);
	public void updateClassLogSetTutor(Map<String, Object> paramMap);
	public void updateLeveltestSetTutor(Map<String, Object> paramMap);	

	// 강사그룹관리
	public List<Map<String, Object>> selectCenterByTeacherGroup();
	public List<Map<String, Object>> selectTeacherByCenterNo(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectGroupByTeacherGroup();
	public List<Map<String, Object>> selectTeacherByGroupNo(Map<String, Object> paramMap);
	public int getDuplTeacherGroupMember(Map<String, Object> paramMap);
	public void insertTeacherGroupMember(Map<String, Object> paramMap);
	public void deleteTeacherGroupMember(Map<String, Object> paramMap);
	public int getDuplTeacherGroupName(Map<String, Object> paramMap);
	public void insertTeacherGroup(Map<String, Object> paramMap);
	public void updateTeacherGroup(Map<String, Object> paramMap);
	public void deleteTeacherGroup(Map<String, Object> paramMap);
	public Map<String, Object> selectTeacherGroupOne(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectUsedTeacherGroupBySite(Map<String, Object> paramMap);	// 삭제 전 사이트에 사용되고있는 강사그룹인지 확인용
	
	// Teacher' Page (관리자 로그인)
	public List<Map<String, Object>> selectTodayScheduleByAdmin(Map<String, Object> paramMap);
	public int countTodayScheduleByAdmin(Map<String, Object> paramMap);

	// 학습현황
	public List<Map<String, Object>> selectClassLogStudent(Map<String, Object> paramMap);
	public Map<String, Object> selectLeveltestStudent(Map<String, Object> paramMap);
	
	// cancel
	public void cancelClassScore(int class_score_no);
	public void cancelLeveltest(int leveltest_no);
	// makeup
	public Map<String, Object> selectTextbookInfo(Map<String, Object> paramMap);
	public int selectClassLogPK();
	public void makeupClassLog(Map<String, Object> paramMap);
	public int getClassStudentNo(Map<String, Object> paramMap);
	public void makeupClassScore(Map<String, Object> paramMap);
	public List<Map<String, Object>> searchUserStudent(Map<String, Object> paramMap);
	public Map<String, Object> selectTeacherByNo(Map<String, Object> paramMap);
	// progress
	public Map<String, Object> getStudyByClass(Map<String, Object> paramMap);
	public void changeProgress(Map<String, Object> paramMap);
	public void updateClassPhone(Map<String, Object> paramMap);
	// modify
	public int getClassScoreNo(Map<String, Object> paramMap);
	// delete
	public void deleteClassScore(int class_score_no);
		
	// 회원상세보기팝업
	public void userPwdClear(Map<String, Object> paramMap); // 비밀번호 초기화
	public Map<String, Object> getUserByEtcPopup(Map<String, Object> paramMap); // 특이사항 상세팝업 유저정보
	public void updateEtc(Map<String, Object> paramMap);
	public List<Map<String, Object>> getSiteCombo();
	public List<Map<String, Object>> getCenterCombo();
	public List<Map<String, Object>> getStudyCombo(Map<String, Object> paramMap);
	public List<Map<String, Object>> getProductCombo(Map<String, Object> paramMap);
	public List<Map<String, Object>> getTextbookCombo(Map<String, Object> paramMap);
	public List<Map<String, Object>> getStudyComboByLanguage(Map<String, Object> paramMap);
	public List<Map<String, Object>> getTeacherComboByLanguage(Map<String, Object> paramMap);
	
	// 수업날짜 일괄변경
	public List<Map<String, Object>> getTeacherCombo(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassDateAll(Map<String, Object> paramMap);
	public int countClassDateAll(Map<String, Object> paramMap);
	public int getHolidayCnt(Map<String, Object> paramMap);	// 변경하려는 날짜가 공통 휴강일(언어)인지 체크
	public int getTeacherHolidayCnt(Map<String, Object> paramMap);	// 변경하려는 날짜가 강사 휴강일 인지 체크
	public Map<String, Object> getClassLogInfo(String class_log_no);
	public List<Map<String, Object>> selectLessonList(Map<String, Object> paramMap);		// 변경하려는 날짜가 해당수업 강사 다른 수업일과 겹치는지 확인용
	public List<Map<String, Object>> selectStudentList(Map<String, Object> paramMap);		// 변경하려는 날짜가 해당수업 학생들 다른 수업일, 수업시간과 겹치는지 확인용 1
	public List<Map<String, Object>> selectStudentLessonList(Map<String, Object> paramMap);		// 변경하려는 날짜가 해당수업 학생들 다른 수업일, 수업시간과 겹치는지 확인용 2
	public void updateClassLogByClassDate(Map<String, Object> paramMap);	// classLog update
	// 수업날짜 일괄변경 레벨테스트 검증
	public Map<String, Object> getLeveltestInfo(String class_log_no);
	public void updateLeveltestByClassDate(Map<String, Object> paramMap);	// leveltest update
	// 수업날짜 일괄변경 cancel
	public void cancelClassLog(Map<String, Object> paramMap);	// classScore update	해당 class_log_no인 class_score 학생들 cancel
	public void cancelLeveltestByClassDate(Map<String, Object> paramMap);	// leveltest update - cancel 처리
	
	
}
