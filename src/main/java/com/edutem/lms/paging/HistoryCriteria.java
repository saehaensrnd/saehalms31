package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class HistoryCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	// 업체정보 검색 조건
	private String searchStudyDate = "";		// 수업일자
	private String class_student_no = "";

	public HistoryCriteria() {
		// 최초의 기본 값
		this.page = 1;
		this.perPageNum = 5;
	}
	
	
	
	public String getSearchStudyDate() {
		return searchStudyDate;
	}



	public void setSearchStudyDate(String searchStudyDate) {
		this.searchStudyDate = searchStudyDate;
	}

	

	public String getClass_student_no() {
		return class_student_no;
	}



	public void setClass_student_no(String class_student_no) {
		this.class_student_no = class_student_no;
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
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum + ", searchStudyDate=" + searchStudyDate+"]";
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