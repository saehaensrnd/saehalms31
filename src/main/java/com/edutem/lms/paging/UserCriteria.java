package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class UserCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	// 업체정보 검색 조건
	private String searchType = "";		// 검색 구분
	private String keyword = "";		// 검색 키워드
	
	private String searchSite = "";
	private String searchStatus = "";		// 1정상 0탈퇴 2대기
	private String searchStartDate = "";
	private String searchEndDate = "";
					

	public UserCriteria() {
		// 최초의 기본 값
		this.page = 1;
		this.perPageNum = 10;
	}
	
	
	

	public String getSearchSite() {
		return searchSite;
	}




	public void setSearchSite(String searchSite) {
		this.searchSite = searchSite;
	}




	public String getSearchStatus() {
		return searchStatus;
	}




	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
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