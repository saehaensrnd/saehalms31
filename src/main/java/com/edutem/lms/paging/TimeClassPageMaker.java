package com.edutem.lms.paging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class TimeClassPageMaker {

	private int totalCount; // 전체 데이터 갯수
	private int displayPageNum = 5; // 한번에 보여질 페이지 번호의 갯수
	
	private int startPage; // 현재화면에서 보이는 start 번호
	private int endPage; // 현재화면에서 보이는 end 번호
	private boolean prev; // 이전 버튼 활성화 여부
	private boolean next; // 다음 버튼 활성화 여부
	
	private TimeClassCriteria cri;
	
	private int lastPage;
	
	

	
	//업체정보 조회	
	public TimeClassPageMaker(TimeClassCriteria cri,int total){
		
		// total = 12...
		this.cri=cri;
		int realEnd = (int)(Math.ceil((total * 1.0) / cri.getPerPageNum()));
		
		lastPage = realEnd; 
		
		
		endPage = (int) (Math.ceil(cri.getPage() / (double) displayPageNum) * displayPageNum);		 
        startPage = (endPage - displayPageNum) +1;
		
		
		if(realEnd < this.endPage) {
			this.endPage=realEnd;
		}
		
		this.next = getEndPage() < realEnd;
		this.prev = getStartPage()>1;				
	}	
	
			
	private String encoding(String keyword) {
		if(keyword == null || keyword.trim().length() == 0) { 
			return "";
		}		 
		try {
			return URLEncoder.encode(keyword, "UTF-8");
		} catch(UnsupportedEncodingException e) { 
			return ""; 
		}
	} 
	
	///////////////////////////////////////////////////////////////////////////////////////////
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		
		calcData();
	}
	
	private void calcData() {
		endPage = (int)(Math.ceil(cri.getPage() / (double) displayPageNum) * displayPageNum);
		startPage = (endPage - displayPageNum) + 1;
		
		int tempEndPage = (int)(Math.ceil(totalCount / (double)cri.getPerPageNum()));
		
		if(endPage > tempEndPage) {
			endPage = tempEndPage;
		}
		
		prev = cri.getPage() == 1 ? false:true;
		next = cri.getPage() * cri.getPerPageNum() >= totalCount ? false:true;
	}
	
	
	
	
	

	public int getDisplayPageNum() {
		return displayPageNum;
	}

	public void setDisplayPageNum(int displayPageNum) {
		this.displayPageNum = displayPageNum;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public boolean isPrev() {
		return prev;
	}

	public void setPrev(boolean prev) {
		this.prev = prev;
	}

	public boolean isNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

	public TimeClassCriteria getCri() {
		return cri;
	}

	public void setCri(TimeClassCriteria cri) {
		this.cri = cri;
	}
	
	
	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}
	
}