function search(){
	let keyword = $("#keyword").val();
	//let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();	
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&keyword="+keyword;
	params += "&study_language="+$("#language").val()+"&textbook_status="+$("#textbook_status").val()+"&study_no="+$("#study_no").val();
	window.location.href = "/admin/textbookMng"+params;
}

function makeSearchParam(cri){	
	let params = "page="+cri.page+"&perPageNum="+cri.perPageNum+"&keyword="+cri.keyword;		
	return params;
}

function deleteBtn(){
	$(".delete_check:checked").each(function(){
		$.ajax({   			
			url : '/admin/textbookMng/deleteTextbook',			
			type : 'post',			
			data: {
				'textbook_no' : $(this).val()
			},					
			success : function() {
				console.log($(this).val()+" 삭제 완료");
			}, error : function() {
				console.log("서버통신 오류");
			}
		}); // ajax
	});

	alert("삭제되었습니다.");
    window.location.href = "/admin/textbookMng";
}


// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	var perPageNum = $("#perPageNum option:selected").val();	
	let keyword = $("#keyword").val();	
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&keyword="+keyword;	

	window.location.href = "/admin/textbookMng"+params;			
			
});


// 상세보기
function goDetail(no, cri){
	let params = "?no="+no +"&"+ makeSearchParam(cri);	
	window.location.href = "/admin/user/detail"+params;
}


// 등록 버튼
function insertBtn(cri){	
	let params = "?"+ makeSearchParam(cri);
	window.location.href = "/admin/textbookMng/detail"+params;
}





