// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchType = $("#searchType").val();	
	let keyword = $("#keyword").val();		
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/timeClass"+params;						
});

function searchCancel(){	
	window.location.href = "/admin/timeClass";
}

// 검색
function search(){
	let keyword = $("#keyword").val();	
	let searchType = $("#searchType").val();
	let perPageNum = $("#perPageNum option:selected").val();
	
	let searchSite = $("#searchSite").val();
	let searchLanguage = $("#searchLanguage").val();
	let searchStudy = $("#searchStudy").val();
	let searchTextBook = $("#searchTextBook").val();
	let searchProductType = $("#searchProductType").val();
	let searchCenter = $("#searchCenter").val();
	let searchTeacher = $("#searchTeacher").val();
	let searchClassType = $("#searchClassType").val();	
	let searchStudyStatus = $("#searchStudyStatus").val();
	let searchClassTime = $("#searchClassTime").val();
	let searchStartDate = $("#searchStartDate").val();
	let searchEndDate = $("#searchEndDate").val();
		
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
	           + "&searchSite="+searchSite
			   + "&searchLanguage="+searchLanguage+ "&searchStudy="+searchStudy+ "&searchTextBook="+searchTextBook 
			   + "&searchProductType="+searchProductType+ "&searchCenter="+searchCenter+ "&searchTeacher="+searchTeacher
               + "&searchClassType="+searchClassType+ "&searchStudyStatus="+searchStudyStatus+ "&searchClassTime="+searchClassTime
			   + "&searchStartDate="+searchStartDate+ "&searchEndDate="+searchEndDate
			   ;
	window.location.href = "/admin/timeClass"+params;
}


// 전체 선택,선택해제
$(document).on("click", ".allCheck", function(){
	let chkList = $('.rowCheck');		
	
	for(let i=0; i<chkList.length; i++ ){
		chkList[i].checked = this.checked;
	}				
});


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


function openUserDetail(user_student_no){
	let flag = "normal";
	window.open("/userDetail?user_student_no="+user_student_no+"&flag="+flag+"&no=", 'userInfoPopup', 'width=1500, height=900 left=200, top=50');
}

function reportBtn(item){
	//console.log(item);
	//console.log(item.room_no);
	//console.log(item.flag);
	
	let no = item.class_score_no;
	
	if(item.flag == "leveltest") {
		no = item.leveltest_no;
	} 
	
	window.open("/reportPopup?flag="+item.flag+"&no="+no, 'reportPopup', 'width=1500, height=900 left=200, top=50');
	
	//$('#testDiv').load(window.location.href = "/admin/timeClass#testDiv");
	
}



function excelBtn(cri){
	$.ajax({
		url : '/admin/timeClass/excelDownload',
		type : 'post',
		data : {
			'searchType' : cri.searchType
		  , 'keyword' : cri.keyword	
		},
		success : function(downloadURL) {
			window.location.href = downloadURL;
			alert("엑셀 다운로드가 완료되었습니다.");
		}, error : function() {
			alert("서버통신 오류");
		} // success
	}); // ajax
	
}


function fileBtn(item) {	
	//console.log(item);
	if(item.flag == 'class'){
		$.ajax({
			url: '/teacher/todaySchedule/videoFile',
			type: 'post',
			data: {
				'flag': item.flag,
				'no': item.class_score_no
			},
			success: function(data) {
				console.log(data);
				window.open("/teacher/todaySchedule/fileList?flag=" +item.flag +"&no=" +item.class_score_no + "&study_date="+data.study_date  + "&urlSplitString=" + data.urlSplitString+ "&recordIdSplitString=" + data.recordIdSplitString, 'fileListPopup', 'width=600, height=500 left=650, top=150');
				
			}, error: function() {
				//alert("서버통신 오류");
			} // success
		}); // ajax
	} else {
		$.ajax({
			url: '/teacher/todaySchedule/videoFile',
			type: 'post',
			data: {
				'flag': item.flag,
				'no': item.leveltest_no
			},
			success: function(data) {
				console.log(data);
				window.open("/teacher/todaySchedule/fileList?flag=" +item.flag +"&no=" +item.leveltest_no + "&study_date="+data.study_date  + "&urlSplitString=" + data.urlSplitString+ "&recordIdSplitString=" + data.recordIdSplitString, 'fileListPopup', 'width=600, height=500 left=650, top=150');				
				
			}, error: function() {
				//alert("서버통신 오류");
			} // success
		}); // ajax
	}

}


function videoLogBtn(item) {
	console.log(item);
	if(item.flag == 'class'){
		let class_score_no = item.class_score_no;
		window.open("/teacher/todaySchedule/videoLogList?flag="+item.flag+"&no="+class_score_no, 'logPopup', 'width=700, height=500 left=600, top=150');
	} else {
		window.open("/teacher/todaySchedule/videoLogList?flag="+item.flag+"&no="+item.leveltest_no, 'logPopup', 'width=700, height=500 left=600, top=150');
	}
}




function makeupCancelBtn(item){
	//console.log(item.class_score_no);
	
	if(confirm("makeup cancel?")){
	
		$.ajax({   			
			url : '/reportPopup/report/cancelMakeup',			
			type : 'post',
			data : {
				'class_score_no' : item.class_score_no
			},				
			success : function(data) {
				alert("Successful");
				location.reload();
				
			}, error : function() {
				alert("통신 실패");
				//console.log("실패");
			} // success
		}); // ajax
	}
}



// 언어 셀렉트박스 change 이벤트 발생 시 - 교육과정, 학습교재 콤보박스 셀렉트박스 Setting  // 강사도 추가해야함
function languageChange(e) {	
	let language = e.value;
	
	// 학습교재 셀렉트박스 초기화
	let textbookSelect = document.getElementById("searchTextBook");
	textbookSelect.options.length = 0;
	textbookSelect.options[0] = new Option("전체", "all");

	// 교육과정 셀렉트박스 초기화
	let studySelect = document.getElementById("searchStudy");
	studySelect.options.length = 0;
	
	
	// 교육과정 셀렉트박스 세팅
	let studyArr;

	$.ajax({
		url: '/admin/classInfo/getStudyComboByLanguage',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"language": language
		},
		success: function(item) {
			studyArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});

	studySelect.options[0] = new Option("전체", "all");

	if (studyArr.length > 0) {
		for (i = 1; i <= studyArr.length; i++) {
			studySelect.options[i] = new Option(studyArr[i - 1].study_name, studyArr[i - 1].study_no);		// text, value
		}
	}	
	
	
	
	
	// 강사 셀렉트박스 초기화
	let teacherSelect = document.getElementById("searchTeacher");
	teacherSelect.options.length = 0;
	
	
	// 강사 셀렉트박스 세팅
	let teacherArr;

	$.ajax({
		url: '/admin/classInfo/getTeacherComboByLanguage',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"searchLanguage": language
		},
		success: function(item) {
			teacherArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});

	teacherSelect.options[0] = new Option("전체", "all");

	if (teacherArr.length > 0) {
		for (i = 1; i <= teacherArr.length; i++) {
			teacherSelect.options[i] = new Option(teacherArr[i - 1].teacher_name, teacherArr[i - 1].user_teacher_no);		// text, value
		}
	}	
	
	
	
	
	
	jcf.replaceAll();	
} 


// 교육과정 셀렉트박스 change 이벤트 발생 시 - 학습교재 콤보박스 셀렉트박스 Setting
function studyChange(e) {	
	let study = e.value;
	
	// 학습교재 셀렉트박스 초기화
	let textbookSelect = document.getElementById("searchTextBook");
	textbookSelect.options.length = 0;
	
	// 교육과정 셀렉트박스 세팅
	let textbookArr;

	$.ajax({
		url: '/admin/classInfo/getTextbookComboByStudy',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"searchStudy": study
		},
		success: function(item) {
			textbookArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});

	textbookSelect.options[0] = new Option("전체", "all");

	if (textbookArr.length > 0) {
		for (i = 1; i <= textbookArr.length; i++) {
			textbookSelect.options[i] = new Option(textbookArr[i - 1].textbook_name, textbookArr[i - 1].textbook_no);		// text, value
		}
	}	
	
	jcf.replaceAll();	
} 

