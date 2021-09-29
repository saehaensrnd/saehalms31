// 등록 버튼
function insertBtn(){	
	window.location.href = "/admin/scheduleClassSite/detail";
}

function goDetail(item){	
	let param = "?schedule_class_site_no="+item.schedule_class_site_no;
	window.location.href = "/admin/scheduleClassSite/detail"+param;
}



// 전체 선택,선택해제
$(document).on("click", ".allCheck", function(){
	let chkList = $('.rowCheck');		
	
	for(let i=0; i<chkList.length; i++ ){
		chkList[i].checked = this.checked;
	}				
});

// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchLanguage = $("#searchLanguage").val();
	let searchSite = $("#searchSite").val();
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchLanguage="+searchLanguage+"&searchSite="+searchSite;
	window.location.href = "/admin/scheduleClassSite"+params;						
});

// 검색
function search(){	
	let perPageNum = $("#perPageNum option:selected").val();
	let searchLanguage = $("#searchLanguage").val();
	let searchSite = $("#searchSite").val();	
		
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchLanguage="+searchLanguage+"&searchSite="+searchSite;
	window.location.href = "/admin/scheduleClassSite"+params;
}





