function makeSearchParam(cri){
	// 오름차순 내림차순 정렬 파라미터 - 페이지 제외 파라미터 생성
	let params = "?searchType="+cri.searchType+"&keyword="+cri.keyword
	          + "&searchSite="+cri.searchSite+ "&searchDate="+cri.searchDate+ "&searchClassType="+cri.searchClassType
			  + "&searchClassCategory="+cri.searchClassCategory+ "&searchLanguage="+cri.searchLanguage+ "&searchStudy="+cri.searchStudy
			  + "&searchTextBook="+cri.searchTextBook+ "&searchCenter="+cri.searchCenter+ "&searchTeacher="+cri.searchTeacher;
	return params;
}

function timeOrderBy(cri, orderParam){
	// 페이지 정보 제외 검색조건은 그대로 time 정렬순서 변경		// orderParam: desc, asc
	let params = makeSearchParam(cri) + "&searchTimeOrderBy="+orderParam;	
	window.location.href = "/admin/modifyClassDate"+params;
}
/////////////////////////////////////////////////////////////////////////////////////

function openUserDetail(user_student_no){
	let flag = "normal";
	window.open("/userDetail?user_student_no="+user_student_no+"&flag="+flag+"&no=", 'userInfoPopup', 'width=1500, height=900 left=200, top=50');
}


// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchType = $("#searchType").val();	
	let keyword = $("#keyword").val();		
	
	let searchSite = $("#searchSite").val();
	let searchDate = $("#searchDate").val();	
	let searchClassType = $("#searchClassType").val();
	let searchClassCategory = $("#searchClassCategory").val();		
	let searchLanguage = $("#searchLanguage").val();
	let searchStudy = $("#searchStudy").val();
	let searchTextBook = $("#searchTextBook").val();	
	let searchCenter = $("#searchCenter").val();
	let searchTeacher = $("#searchTeacher").val();
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
			   + "&searchSite="+searchSite+ "&searchDate="+searchDate+ "&searchClassType="+searchClassType
			   + "&searchClassCategory="+searchClassCategory+ "&searchLanguage="+searchLanguage+ "&searchStudy="+searchStudy
			   + "&searchTextBook="+searchTextBook+ "&searchCenter="+searchCenter+ "&searchTeacher="+searchTeacher;
	window.location.href = "/admin/modifyClassDate"+params;					
});


// 검색
function search(){
	let keyword = $("#keyword").val().trim();	
	let searchType = $("#searchType").val();
	let perPageNum = $("#perPageNum option:selected").val();
	
	let searchSite = $("#searchSite").val();
	let searchDate = $("#searchDate").val();	
	let searchClassType = $("#searchClassType").val();
	let searchClassCategory = $("#searchClassCategory").val();		
	let searchLanguage = $("#searchLanguage").val();
	let searchStudy = $("#searchStudy").val();
	let searchTextBook = $("#searchTextBook").val();	
	let searchCenter = $("#searchCenter").val();
	let searchTeacher = $("#searchTeacher").val();
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
			   + "&searchSite="+searchSite+ "&searchDate="+searchDate+ "&searchClassType="+searchClassType
			   + "&searchClassCategory="+searchClassCategory+ "&searchLanguage="+searchLanguage+ "&searchStudy="+searchStudy
			   + "&searchTextBook="+searchTextBook+ "&searchCenter="+searchCenter+ "&searchTeacher="+searchTeacher;
	window.location.href = "/admin/modifyClassDate"+params;
}

function searchCancel(){	
	window.location.href = "/admin/modifyClassDate";
}



// 전체 선택,선택해제
$(document).on("click", ".allCheck", function(){
	let chkList = $('.rowCheck');		
	
	for(let i=0; i<chkList.length; i++ ){
		chkList[i].checked = this.checked;
	}				
});


// 언어 셀렉트박스 change 이벤트 발생 시 - 교육과정, 학습교재 콤보박스 셀렉트박스 Setting
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
		url: '/admin/modifyClassDate/getTeacherCombo',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"searchLanguage": language,
			"searchCenter": $("#searchCenter").val()
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


// 티칭센터 셀렉트박스 change 이벤트 발생 시 - 교육과정, 학습교재 콤보박스 셀렉트박스 Setting
function centerChange(e) {
	let center = e.value;
	
	// 강사 셀렉트박스 초기화
	let teacherSelect = document.getElementById("searchTeacher");
	teacherSelect.options.length = 0;	
	
	// 강사 셀렉트박스 세팅
	let teacherArr;

	$.ajax({
		url: '/admin/modifyClassDate/getTeacherCombo',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"searchLanguage": $("#searchLanguage").val(),
			"searchCenter": center
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




function changingBtn(){	
	let choosedDate = $("#choosedDate").val();
	
	let cnt = $(".rowCheck:checked").length;
	let notChkCnt = $(".rowCheck:not(:checked)").length;
		
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
		return false;
	} else if (choosedDate == ""){
		alert("변경날짜를 선택하세요.");
		return false;
	}
	
	
	let sameLogCnt = 0;
	$(".rowCheck:checked").each(function(i, chkItem) {
		console.log($(chkItem).attr('flag'));
		
		let chkFlag = $(chkItem).attr('flag');
		let chkClassLogNo = $(chkItem).attr('class_log_no');
		
		// 체크선택 된 수강 내역 중 class_log_no / 체크선택 안된 수강 내역중 class_log_no를 비교하여 같은 class_log 내용인데 선택이 모두 안되어있다면 alert 후 return  
		if(chkFlag == 'class'){			
			$(".rowCheck:not(:checked)").each(function(k, notChkItem) {
				let notChkClassLogNo = $(notChkItem).attr('class_log_no');
				
				if(chkClassLogNo == notChkClassLogNo) {
					sameLogCnt++;
				}
			});		
		}
		
	});
	
	if (sameLogCnt > 0) {
		alert("선택된 1:N 수업 중 체크 안된 같은 수업내용이 있습니다.");
		return false;
	}
	
	
	
	let classLogNoArr = new Array();
	let leveltestNoArr = new Array();
	$(".rowCheck:checked").each(function(i, chkItem) {
		classLogNoArr.push($(chkItem).attr('class_log_no'));
		leveltestNoArr.push($(chkItem).attr('leveltest_no'));
	});
	
		
	$.ajax({   			
		url : '/admin/modifyClassDate/changing',			
		type : 'post',			
		data: {'classLogNoArr' : classLogNoArr
			 , 'leveltestNoArr' : leveltestNoArr
		     , 'choosedDate': choosedDate
		},					
		success : function(data) {
				alert(data.resultMsg);
				//window.location.href = "/admin/modifyClassDate";
				location.reload(); 

		}, error : function() {
				alert("변경 실패");
		} // success
	}); // ajax
}






function cancelBtn(){
	let cnt = $(".rowCheck:checked").length;
	let notChkCnt = $(".rowCheck:not(:checked)").length;
		
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
		return false;
	}
	
	
	let sameLogCnt = 0;
	$(".rowCheck:checked").each(function(i, chkItem) {
		console.log($(chkItem).attr('flag'));
		
		let chkFlag = $(chkItem).attr('flag');
		let chkClassLogNo = $(chkItem).attr('class_log_no');
		
		// 체크선택 된 수강 내역 중 class_log_no / 체크선택 안된 수강 내역중 class_log_no를 비교하여 같은 class_log 내용인데 선택이 모두 안되어있다면 alert 후 return  
		if(chkFlag == 'class'){			
			$(".rowCheck:not(:checked)").each(function(k, notChkItem) {
				let notChkClassLogNo = $(notChkItem).attr('class_log_no');
				
				if(chkClassLogNo == notChkClassLogNo) {
					sameLogCnt++;
				}
			});		
		}
		
	});
	
	if (sameLogCnt > 0) {
		alert("선택된 1:N 수업 중 체크 안된 같은 수업내용이 있습니다.");
		return false;
	}
		
	
	let classLogNoArr = new Array();
	let leveltestNoArr = new Array();
	$(".rowCheck:checked").each(function(i, chkItem) {
		classLogNoArr.push($(chkItem).attr('class_log_no'));
		leveltestNoArr.push($(chkItem).attr('leveltest_no'));
	});
	
		
	$.ajax({   			
		url : '/admin/modifyClassDate/cancel',			
		type : 'post',			
		data: {'classLogNoArr' : classLogNoArr
			 , 'leveltestNoArr' : leveltestNoArr
		},					
		success : function(data) {
				alert(data.resultMsg);				
				location.reload(); 
				
		}, error : function() {
				alert("변경 실패");
		} // success
	}); // ajax	
}




function getFormatDate(date) {
	var year = date.getFullYear();              //yyyy
	var month = (1 + date.getMonth());          //M
	month = month >= 10 ? month : '0' + month;  //month 두자리로 저장
	var day = date.getDate();                   //d
	day = day >= 10 ? day : '0' + day;          //day 두자리로 저장
	return year + '-' + month + '-' + day;       //'-' 추가하여 yyyy-mm-dd 형태 생성 가능
}

function choosedDateChange(e){
	let selectedDay = new Date(e.value);
	
	//let today = new Date();
	//let formatToday = getFormatDate(today);		// yyyy-MM-dd	
	//let tommorow = new Date(formatToday);	
	//tommorow.setDate(tommorow.getDate() + 1);
		
	let today = new Date(getFormatDate(new Date()));
	//console.log(today);
	
	if(selectedDay < today){
		alert("변경일은 금일 이전 날짜를 선택할 수 없습니다.");
		$('#choosedDate').val('');
	}	
}








