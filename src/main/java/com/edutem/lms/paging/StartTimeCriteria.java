package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class StartTimeCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	// 업체정보 검색 조건
	private String searchLanguage = "";	
	private String searchRunningTime = "";
	private String searchSite = "";
					

	public StartTimeCriteria() {
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


	


	public String getSearchLanguage() {
		return searchLanguage;
	}




	public void setSearchLanguage(String searchLanguage) {
		this.searchLanguage = searchLanguage;
	}




	public String getSearchRunningTime() {
		return searchRunningTime;
	}




	public void setSearchRunningTime(String searchRunningTime) {
		this.searchRunningTime = searchRunningTime;
	}




	@Override
	public String toString() {
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum +"]";
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