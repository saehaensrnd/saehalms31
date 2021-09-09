package com.edutem.lms.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper {
	
	// 마이노트 학습현황 - 수강중인 과정 리스트 (콤보박스용)
	public List<Map<String, Object>> selectClassComboByStudent(Map<String, Object> paramMap);
	public Map<String, Object> getClassInfo(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectClassLog(Map<String, Object> paramMap);	
	public void insertClassVideoStudentLog(Map<String, Object> paramMap);
	public void updateClassVideoStudentLog(Map<String, Object> paramMap);
	
	public int selectLastAIClassVideoStudentLog();
	public Map<String, Object> getClassLogInfo(Map<String, Object> paramMap);
	
	// 마이노트 무료레벨테스트
	public List<Map<String, Object>> selectLevelTestAll(Map<String, Object> paramMap);
	public int countLevelTestAll(Map<String, Object> paramMap);
	public Map<String, Object> getLevelInfo(Map<String, Object> paramMap);
	
	
	// 수강신청 페이지
	public Map<String, Object> selectStudentOne(Map<String, Object> paramMap);
	public Map<String, Object> selectSiteOne(Map<String, Object> paramMap);
	public Map<String, Object> selectScheduleClassSite(Map<String, Object> paramMap);
	public int countStudyNotUsedSite(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStudyLanguage(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectStudyLanguageLIMIT(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectSiteProduct(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectTeacherLangAll(Map<String, Object> paramMap);
	public Map<String, Object> selectTeacherOne(Map<String, Object> paramMap);
	public int selectTextbookPriceOne(Map<String, Object> paramMap);
	public void insertPayment(Map<String, Object> paramMap);
	
	// 회원가입
	public Map<String, Object> selectSiteOneByCode(Map<String, Object> paramMap);
	public void insertUserStudent(Map<String, Object> paramMap);
	
	// 마이노트 휴강 및 보강 신청
	public List<Map<String, Object>> selectPostponeByStudent(Map<String, Object> paramMap);
	public int countPostponeByStudent(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getHolidayComboByClass(Map<String, Object> paramMap);
	public void insertPostpone(Map<String, Object> paramMap);
	public void updatePostpone(Map<String, Object> paramMap);
	public Map<String, Object> getPostpone(Map<String, Object> paramMap);
	
	// 회원정보 - 내 정보 수정
	public Map<String, Object> getStudentInfo(Map<String, Object> paramMap);
	public void updateUserByInfoModify(Map<String, Object> paramMap);
	public void updateUserStudentByInfoModify(Map<String, Object> paramMap);
	
	// 무료레벨테스트신청
	public int countLevelAppAbleTerm(Map<String, Object> paramMap);
	public List<Map<String, Object>> getTeacherComboByLanguageType(Map<String, Object> paramMap);
	public Map<String, Object> getSiteGroupNCenter(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectSiteTeachers(Map<String, Object> paramMap);
	public void insertLeveltestByStudent(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectLevelAppDays(Map<String, Object> paramMap);
	
	
}
