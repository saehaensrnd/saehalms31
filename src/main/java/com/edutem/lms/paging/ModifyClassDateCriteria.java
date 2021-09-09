package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class ModifyClassDateCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	// 업체정보 검색 조건
	private String searchType = "";		// 검색 구분
	private String keyword = "";		// 검색 키워드
	
	private String searchSite = "";
	
	private String searchDate = "";				// 날자 (검색일)
	private String searchClassType = "";		// 수업타입 (Phone, Video..)
	private String searchClassCategory = "";	// 수업구분 (regular, leveltest)
	
	private String searchLanguage = "";		// 언어
	private String searchStudy = "";		// 교육과정
	private String searchTextBook = "";		// 학습교재
	
	private String searchCenter = "";
	private String searchTeacher = "";		
	
	private String searchTimeOrderBy = "";			// time 정렬
	

	public ModifyClassDateCriteria() {
		// 최초의 기본 값
		this.page = 1;
		this.perPageNum = 10;
	}
	
	
	
	
	
	
	
	public String getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}
	
	public String getSearchClassCategory() {
		return searchClassCategory;
	}
	
	public void setSearchClassCategory(String searchClassCategory) {
		this.searchClassCategory = searchClassCategory;
	}

	public String getSearchTeacher() {
		return searchTeacher;
	}

	public void setSearchTeacher(String searchTeacher) {
		this.searchTeacher = searchTeacher;
	}

	public String getSearchTimeOrderBy() {
		return searchTimeOrderBy;
	}

	public void setSearchTimeOrderBy(String searchTimeOrderBy) {
		this.searchTimeOrderBy = searchTimeOrderBy;
	}

	public String getSearchSite() {
		return searchSite;
	}

	public void setSearchSite(String searchSite) {
		this.searchSite = searchSite;
	}

	public String getSearchLanguage() {
		return searchLanguage;
	}

	public void setSearchLanguage(String searchLanguage) {
		this.searchLanguage = searchLanguage;
	}

	public String getSearchStudy() {
		return searchStudy;
	}

	public void setSearchStudy(String searchStudy) {
		this.searchStudy = searchStudy;
	}

	public String getSearchTextBook() {
		return searchTextBook;
	}

	public void setSearchTextBook(String searchTextBook) {
		this.searchTextBook = searchTextBook;
	}

	public String getSearchCenter() {
		return searchCenter;
	}

	public void setSearchCenter(String searchCenter) {
		this.searchCenter = searchCenter;
	}
	
	public String getSearchClassType() {
		return searchClassType;
	}

	public void setSearchClassType(String searchClassType) {
		this.searchClassType = searchClassType;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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

	
	public String getSearchType() {
		return searchType;
	}


	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}


	@Override
	public String toString() {
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum + ", searchType=" + searchType + ", keyword=" + keyword+"]";
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