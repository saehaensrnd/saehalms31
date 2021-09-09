function search(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/leveltestMng"+params;
}

function insertLeveltest(){
	window.open("/userDetail/searchStudent", 'searchStudent', 'width=800, height=540 left=550, top=70');
}

// excel downlaod
function excelDownBtn(cri){
	$.ajax({
		url : '/admin/user/downAllLeveltest',
		type : 'post',
		data: {
			"searchType": cri.searchType,
			"keyword": cri.keyword
		},
		success : function(downloadURL) {
			window.location.href = downloadURL;
			alert("엑셀 다운로드가 완료되었습니다.");
		}, error : function() {
			alert("서버통신 오류");
		} // success
	}); // ajax
}

function openUserDetail(user_student_no){
	let flag = "normal";
	window.open("/userDetail?user_student_no="+user_student_no+"&flag="+flag+"&no=", 'userInfoPopup', 'width=1500, height=900 left=200, top=50');
}