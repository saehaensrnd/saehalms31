package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class TeacherCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	// 검색 조건
	private String searchCenter = "";
	private String searchTeacherNo = "";
	private String searchNation = "";
	private String searchClassType = "";
	private String searchStatus = "";
	private String searchOpenStatus = "";
	private String searchTeacherName = "";
					

	public TeacherCriteria() {
		// 최초의 기본 값
		this.page = 1;
		this.perPageNum = 10;
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

	public String getSearchCenter() {
		return searchCenter;
	}

	public void setSearchCenter(String searchCenter) {
		this.searchCenter = searchCenter;
	}
	
	public String getSearchTeacherNo() {
		return searchTeacherNo;
	}
	
	public void setSearchTeacherNo(String searchTeacherNo) {
		this.searchTeacherNo = searchTeacherNo;
	}
	
	public String getSearchNation() {
		return searchNation;
	}
	
	public void setSearchNation(String searchNation) {
		this.searchNation = searchNation;
	}
	
	public String getSearchClassType() {
		return searchClassType;
	}

	public void setSearchClassType(String searchClassType) {
		this.searchClassType = searchClassType;
	}

	public String getSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}

	public String getSearchOpenStatus() {
		return searchOpenStatus;
	}

	public void setSearchOpenStatus(String searchOpenStatus) {
		this.searchOpenStatus = searchOpenStatus;
	}

	public String getSearchTeacherName() {
		return searchTeacherName;
	}

	public void setSearchTeacherName(String searchTeacherName) {
		this.searchTeacherName = searchTeacherName;
	}



	@Override
	public String toString() {
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum + "]";
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