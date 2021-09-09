package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class ApplicationCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	private String searchType = "";		// 검색 구분
	private String keyword = "";		// 검색 키워드
	private String searchStartDt = "";	// 검색 기간 start
	private String searchEndDt = "";	// 검색 기간 end	
					

	public ApplicationCriteria() {
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




	public String getSearchStartDt() {
		return searchStartDt;
	}




	public void setSearchStartDt(String searchStartDt) {
		this.searchStartDt = searchStartDt;
	}




	public String getSearchEndDt() {
		return searchEndDt;
	}




	public void setSearchEndDt(String searchEndDt) {
		this.searchEndDt = searchEndDt;
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
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum + ", searchType=" + searchType + ", keyword=" + keyword
				+ ", searchStartDt=" + searchStartDt + ", searchEndDt=" + searchEndDt+"]";
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