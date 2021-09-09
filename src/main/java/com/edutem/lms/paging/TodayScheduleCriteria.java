package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class TodayScheduleCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	private String searchType = "";		// 검색 구분
	private String keyword = "";		// 검색 키워드
	
	private String searchDate = "";			// 검색 기준일	
	private String searchStartHour = "";			
	private String searchEndHour = "";			
	
	// 업체정보 검색 조건
	private String searchCenter = "";		// 티칭센터 검색 셀렉트 콤보
	private String searchTeacher = "";		// 강사 검색 셀렉트 콤보
	
	
	
	
	
					

	public TodayScheduleCriteria() {
		// 최초의 기본 값
		this.page = 1;
		this.perPageNum = 10;
	}

	
	
	
	
	public String getSearchType() {
		return searchType;
	}





	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}





	public String getKeyword() {
		return keyword;
	}





	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}





	public String getSearchDate() {
		return searchDate;
	}





	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}





	public String getSearchStartHour() {
		return searchStartHour;
	}





	public void setSearchStartHour(String searchStartHour) {
		this.searchStartHour = searchStartHour;
	}





	public String getSearchEndHour() {
		return searchEndHour;
	}





	public void setSearchEndHour(String searchEndHour) {
		this.searchEndHour = searchEndHour;
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
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum+ ", searchDate=" + searchDate + ", searchTeacher=" + searchTeacher+ ", searchCenter=" + searchCenter +"]";
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