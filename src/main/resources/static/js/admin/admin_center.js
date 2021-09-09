// 등록 버튼
function insertBtn(cri){
	let params = "?page="+cri.page+"&perPageNum="+cri.perPageNum;
	window.location.href = "/admin/center/detail"+params;
}

/*
function deleteBtn(teacher_no){
	
	if(confirm("삭제 하시겠습니까?")){
		console.log(teacher_no);			
			
		
		$.ajax({   			
			url : '/admin/center/delete',			
			type : 'post',			
			data: {'no' : teacher_no},					
			success : function() {				
				alert("삭제 되었습니다.");                    
                window.location.href = "/admin/center";				

			}, error : function() {
				//alert("삭제 실패. 수강중인 과정이 있습니다.");
				alert("삭제 실패.");
			} // success
		}); // ajax
					
	}	
}
*/

// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchStatus = $("#searchStatus").val();
	let searchType = $("#searchType").val();	
	let keyword = $("#keyword").val();		
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchStatus="+searchStatus+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/center"+params;						
});


// 검색
function search(){
	let keyword = $("#keyword").val();
	let searchStatus = $("#searchStatus").val();
	let searchType = $("#searchType").val();
	let perPageNum = $("#perPageNum option:selected").val();	
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchStatus="+searchStatus+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/center"+params;
}

