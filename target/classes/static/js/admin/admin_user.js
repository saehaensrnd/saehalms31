function search(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();
	
	let searchSite = $("#searchSite").val();	
	let searchStatus = $("#searchStatus").val();	
	let searchStartDate = $("#searchStartDate").val();
	let searchEndDate = $("#searchEndDate").val();	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
	           + "&searchSite="+searchSite + "&searchStatus="+searchStatus + "&searchStartDate="+searchStartDate + "&searchEndDate="+searchEndDate;
	          
	window.location.href = "/admin/user"+params;
}

function searchCancel(){	
	window.location.href = "/admin/user";
}

function makeSearchParam(cri){	
	let params = "page="+cri.page+"&perPageNum="+cri.perPageNum+"&searchType="+cri.searchType+"&keyword="+cri.keyword;		
	return params;
}

function deleteBtn(user_no){
	if(confirm("탈퇴처리 하시겠습니까?")){	
		$.ajax({   			
			url : '/admin/user/deleteUser',			
			type : 'post',			
			data: {'user_student_no' : user_no},					
			success : function() {				
				alert("탈퇴되었습니다.");                    
                window.location.href = "/admin/user";
			}, error : function() {
				alert("서버통신 오류");
			}
		}); // ajax			
	}	
}

function restoreBtn(user_no){
	if(confirm("복구처리 하시겠습니까?")){		
		$.ajax({   			
			url : '/admin/user/restoreUser',			
			type : 'post',			
			data: {'user_student_no' : user_no},					
			success : function() {				
				alert("복구되었습니다.");                    
                window.location.href = "/admin/user";
			}, error : function() {
				alert("서버통신 오류");
			} // success
		}); // ajax			
	}	
}


// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();
	
	let searchSite = $("#searchSite").val();	
	let searchStatus = $("#searchStatus").val();	
	let searchStartDate = $("#searchStartDate").val();
	let searchEndDate = $("#searchEndDate").val();		
	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
	           + "&searchSite="+searchSite + "&searchStatus="+searchStatus + "&searchStartDate="+searchStartDate + "&searchEndDate="+searchEndDate;
	
	window.location.href = "/admin/user"+params;
});

// 상세보기
function goDetail(no, cri){
	let params = "?no="+no +"&"+ makeSearchParam(cri);	
	window.location.href = "/admin/user/detail"+params;
}

function openUserDetail(user_student_no){
	let flag = "normal";
	window.open("/userDetail?user_student_no="+user_student_no+"&flag="+flag+"&no=", 'userInfoPopup', 'width=1500, height=900 left=200, top=50');
}

// 등록 버튼
function insertBtn(cri){	
	let params = "?"+ makeSearchParam(cri);
	window.location.href = "/admin/user/detail"+params;
}

//전체 유저 SMS 전송
function allSendBtn(){
	$.ajax({
		url : '/admin/user/sendAllUser',
		type : 'post',
		success : function(user_student_no) {
			window.open("/sms/openPopup?selectedArr="+user_student_no, 'smsPopup', 'width=550, height=650 left=650, top=150');
		}, error : function() {
			alert("서버통신 오류");
		} // success
	}); // ajax
}

// 선택한 유저 SMS 전송
function checkedSendBtn(){
	let cnt = $(".rowCheck:checked").length;	
	let selectedArr = new Array();
	
	$(".rowCheck:checked").each(function() {		
		selectedArr.push($(this).attr('name'));		// user_no
	});	
	
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
	} else {		
		window.open("/sms/openPopup?selectedArr="+selectedArr, 'smsPopup', 'width=550, height=650 left=650, top=150');		
	}
}

// excel downlaod
function excelDownBtn(cri){
	$.ajax({
		url : '/admin/user/downAllUser',
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

function insertLeveltest(){
	let cnt = $(".rowCheck:checked").length;
	
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
	}
	else if(cnt == 1){
		var user_student_no = $(".rowCheck:checked").attr('name');
		window.open("/userDetail/makeLeveltest?user_student_no="+user_student_no, 'makeLeveltest', 'width=800, height=540 left=550, top=70');
	}
	else {		
		alert("학생을 한명만 선택해주세요.");		
	}
}

