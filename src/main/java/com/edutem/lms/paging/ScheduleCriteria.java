package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class ScheduleCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	// 업체정보 검색 조건
	private String searchCenter = "";		// 티칭센터 검색 셀렉트 콤보
	private String searchTeacher = "";		// 강사 검색 셀렉트 콤보
	private String searchStudent = "";		// 학생 검색 셀렉트 콤보	// 강사 today, weekly 스케줄
	
	
	private String thisDay = "";		// 검색 기준일
					

	public ScheduleCriteria() {
		// 최초의 기본 값
		this.page = 1;
		this.perPageNum = 10;
	}

	
	
	
	
	public String getSearchStudent() {
		return searchStudent;
	}

	public void setSearchStudent(String searchStudent) {
		this.searchStudent = searchStudent;
	}

	public String getThisDay() {
		return thisDay;
	}

	public void setThisDay(String thisDay) {
		this.thisDay = thisDay;
	}

	public String getSearchTeacher() {
		return searchTeacher;
	}
	public void setSearchTeacher(String searchTeacher) {
		this.searchTeacher = searchTeacher;
	}
	public String getSearchCenter() {
		return searchCenter;
	}
	
	public void setSearchCenter(String searchCenter) {
		this.searchCenter = searchCenter;
	}


	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPerPageNum() {
		return perPageNum;
	}

	public void setPerPageNum(int perPageNum) {
		this.perPageNum = perPageNum;
	}
	
	public int getPageStart() {
		return (this.page - 1) * perPageNum;
	}

	@Override
	public String toString() {
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum + ", searchTeacher=" + searchTeacher+ ", searchCenter=" + searchCenter +"]";
	}
	
	
	public String encoding(String keyword) {
		if(keyword == null || keyword.trim().length() == 0) { 
			return "";
		}		 
		try {
			return URLEncoder.encode(keyword, "UTF-8");
		} catch(UnsupportedEncodingException e) { 
			return ""; 
		}
	} 
	
}