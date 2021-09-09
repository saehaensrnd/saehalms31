package com.edutem.lms.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeacherMapper {
	public List<Map<String, Object>> selectTodaySchedule(Map<String, Object> paramMap);
	public int countTodaySchedule(Map<String, Object> paramMap);
	
	public Map<String, Object> getVideoClassInfo(Map<String, Object> paramMap);
	public Map<String, Object> getVideoClassLevelInfo(Map<String, Object> paramMap);
	public void insertClassVideoTeacherLog(Map<String, Object> paramMap);
	public void updateClassVideoTeacherLog(Map<String, Object> paramMap);
	// 레벨테스트 로그 insert
	public void insertClassVideoLevelLog(Map<String, Object> paramMap);
	public void updateClassVideoLevelLog(Map<String, Object> paramMap);
	
	public int selectLastAIClassVideoTeacherLog();
	public int selectLastAIClassVideoLevelLog();
	
	public List<Map<String, Object>> selectClassVideoLogList(Map<String, Object> paramMap);
	public List<Map<String, Object>> selectLevelVideoLogList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> selectTodayScheduleExcel(Map<String, Object> paramMap);
	
	// 강사 - 회원상세보기팝업
	public void updateEngNameByTeacherUserDetail(Map<String, Object> paramMap);
		
	// 화상API 입장 시 교재 PDF 연결 관련 - 가장 최근 진도 토픽[Unit]단위 PDF를 불러온다. 최초 수업이라 최근 진도가 없다면 unit 1번 교재를 가져옴
	public Map<String, Object> getTopic(Map<String, Object> paramMap);
	public Map<String, Object> getTextbookTopic(Map<String, Object> paramMap);

}
