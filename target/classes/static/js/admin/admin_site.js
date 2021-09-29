// 등록 버튼
function insertBtn(cri){
	let params = "?page="+cri.page+"&perPageNum="+cri.perPageNum;
	window.location.href = "/admin/site/detail"+params;
}


// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchStatus = $("#searchStatus").val();
	let searchType = $("#searchType").val();	
	let keyword = $("#keyword").val();		
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchStatus="+searchStatus+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/site"+params;						
});


// 검색
function search(){
	let keyword = $("#keyword").val();
	let searchStatus = $("#searchStatus").val();
	let searchType = $("#searchType").val();
	let perPageNum = $("#perPageNum option:selected").val();	
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchStatus="+searchStatus+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/site"+params;
}

