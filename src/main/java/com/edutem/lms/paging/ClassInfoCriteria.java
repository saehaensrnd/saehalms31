package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class ClassInfoCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	// 업체정보 검색 조건
	private String searchType = "";		// 검색 구분
	private String keyword = "";		// 검색 키워드
	
	private String searchSite = "";
	private String searchLanguage = "";
	private String searchStudy = "";		// 교육과정
	private String searchTextBook = "";		// 학습교재
	private String searchCenter = "";
	private String searchStatus = "";
	private String searchClassType = "";
	private String searchStartDate = "";
	private String searchEndDate = "";
	
	private String searchTimeOrderBy = "";			// time 정렬
	private String searchTeacherOrderBy = "";		// teacher 정렬

	public ClassInfoCriteria() {
		// 최초의 기본 값
		this.page = 1;
		this.perPageNum = 10;
	}
	
	
	
	
	
	
	public String getSearchTimeOrderBy() {
		return searchTimeOrderBy;
	}


	public void setSearchTimeOrderBy(String searchTimeOrderBy) {
		this.searchTimeOrderBy = searchTimeOrderBy;
	}


	public String getSearchTeacherOrderBy() {
		return searchTeacherOrderBy;
	}

	public void setSearchTeacherOrderBy(String searchTeacherOrderBy) {
		this.searchTeacherOrderBy = searchTeacherOrderBy;
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




	public String getSearchStatus() {
		return searchStatus;
	}




	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}




	public String getSearchClassType() {
		return searchClassType;
	}




	public void setSearchClassType(String searchClassType) {
		this.searchClassType = searchClassType;
	}




	public String getSearchStartDate() {
		return searchStartDate;
	}




	public void setSearchStartDate(String searchStartDate) {
		this.searchStartDate = searchStartDate;
	}




	public String getSearchEndDate() {
		return searchEndDate;
	}




	public void setSearchEndDate(String searchEndDate) {
		this.searchEndDate = searchEndDate;
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