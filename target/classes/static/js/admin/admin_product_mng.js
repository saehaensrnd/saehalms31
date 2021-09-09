function search(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/productMng"+params;
}

function makeSearchParam(cri){	
	let params = "page="+cri.page+"&perPageNum="+cri.perPageNum+"&searchType="+cri.searchType+"&keyword="+cri.keyword;		
	return params;
}

function deleteBtn(user_no){
	if(confirm("탈퇴처리 하시겠습니까?")){	
		$.ajax({   			
			url : '/admin/productMng/deleteUser',			
			type : 'post',			
			data: {'user_student_no' : user_no},					
			success : function() {				
				alert("탈퇴되었습니다.");                    
                window.location.href = "/admin/productMng";
			}, error : function() {
				alert("서버통신 오류");
			}
		}); // ajax			
	}	
}

// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/productMng"+params;
});

// 상세보기
function goDetail(no, cri){
	let params = "?no="+no +"&"+ makeSearchParam(cri);	
	window.location.href = "/admin/productMng/detail"+params;
}

// 등록 버튼
function insertBtn(cri){	
	let params = "?"+ makeSearchParam(cri);
	window.location.href = "/admin/productMng/detail"+params;
}

$(document).ready(function(){
	$("#all_check").click(function(){
		if($(this).prop("checked")){
			$(".rowCheck").prop("checked", true);
		}
		else{
			$(".rowCheck").prop("checked", false);
		}
	});

	$(".rowCheck").click(function(){
		if($(".rowCheck:checked").length == $(".rowCheck").length){
			$("#all_check").prop("checked", true);
		}
		else{
			$("#all_check").prop("checked", false);
		}
	});
});



