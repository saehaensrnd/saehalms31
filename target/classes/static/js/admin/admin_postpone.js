function search(){
	let keyword = $("#keyword").val();
	var perPageNum = $("#perPageNum option:selected").val();	
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&keyword="+keyword;
	window.location.href = "/admin/postponeMng"+params;
}

function makeSearchParam(cri){	
	let params = "page="+cri.page+"&perPageNum="+cri.perPageNum+"&keyword="+cri.keyword;		
	return params;
}


// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	var perPageNum = $("#perPageNum option:selected").val();	
	let keyword = $("#keyword").val();	
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&keyword="+keyword;	

	window.location.href = "/admin/postponeMng"+params;			
			
});

function openUserDetail(user_student_no){
	let flag = "normal";
	window.open("/userDetail?user_student_no="+user_student_no+"&flag="+flag+"&no=", 'userInfoPopup', 'width=1500, height=900 left=200, top=50');
}

//excel downlaod
function excelDownBtn(){
	$.ajax({
		url : '/admin/user/downAllPostpone',
		type : 'post',
		success : function(downloadURL) {
			window.location.href = downloadURL;
			alert("엑셀 다운로드가 완료되었습니다.");
		}, error : function() {
			alert("서버통신 오류");
		} // success
	}); // ajax
}

