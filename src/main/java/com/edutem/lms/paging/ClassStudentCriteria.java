package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class ClassStudentCriteria {

	private int page; 					// 보여줄 페이지 번호
	private int perPageNum; 			// 페이지당 보여줄 게시글의 갯수
	
	// 업체정보 검색 조건
	private String searchType = "";		// 검색 구분
	private String keyword = "";		// 검색 키워드
	
	private String searchSite = "";
	private String searchLanguage = "";
	private String searchStudy = "";			// 교육과정
	private String searchProduct = "";			// 수강상품
	private String searchClassType = "";		// 수업구분 (regular class / level test)
	private String searchStatus = "";			// 수업상태 (대기 / 학습중)
	
	private String searchClassStart_SDT = "";	// 수업시작일 검색시작일
	private String searchClassStart_EDT = "";	// 수업시작일 검색종료일	
	
	private String searchClassEnd_SDT = "";
	private String searchClassEnd_EDT = "";
	
	
					

	public ClassStudentCriteria() {
		// 최초의 기본 값
		this.page = 1;
		this.perPageNum = 10;
	}
	
	
	

	
	
	public String getSearchClassStart_SDT() {
		return searchClassStart_SDT;
	}






	public void setSearchClassStart_SDT(String searchClassStart_SDT) {
		this.searchClassStart_SDT = searchClassStart_SDT;
	}






	public String getSearchClassStart_EDT() {
		return searchClassStart_EDT;
	}






	public void setSearchClassStart_EDT(String searchClassStart_EDT) {
		this.searchClassStart_EDT = searchClassStart_EDT;
	}






	public String getSearchClassEnd_SDT() {
		return searchClassEnd_SDT;
	}






	public void setSearchClassEnd_SDT(String searchClassEnd_SDT) {
		this.searchClassEnd_SDT = searchClassEnd_SDT;
	}






	public String getSearchClassEnd_EDT() {
		return searchClassEnd_EDT;
	}






	public void setSearchClassEnd_EDT(String searchClassEnd_EDT) {
		this.searchClassEnd_EDT = searchClassEnd_EDT;
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




	public String getSearchProduct() {
		return searchProduct;
	}




	public void setSearchProduct(String searchProduct) {
		this.searchProduct = searchProduct;
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