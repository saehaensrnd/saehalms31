function makeSearchParam(cri){
	// 오름차순 내림차순 정렬 파라미터 - 페이지 제외 파라미터 생성
	let params = "?searchType="+cri.searchType+"&keyword="+cri.keyword
	          + "&searchSite="+cri.searchSite+ "&searchCenter="+cri.searchCenter+ "&searchStatus="+cri.searchStatus
			  + "&searchClassType="+cri.searchClassType+ "&searchStartDate="+cri.searchStartDate+ "&searchEndDate="+cri.searchEndDate
			  + "&searchLanguage="+cri.searchLanguage+ "&searchStudy="+cri.searchStudy+ "&searchTextBook="+cri.searchTextBook;
	return params;
}

function timeOrderBy(cri, orderParam){
	// 페이지 정보 제외 검색조건은 그대로 time 정렬순서 변경		// orderParam: desc, asc
	let params = makeSearchParam(cri) + "&searchTimeOrderBy="+orderParam;	
	window.location.href = "/admin/classInfo"+params;
}

function teacherOrderBy(cri, orderParam){
	// 페이지 정보 제외 검색조건은 그대로 teacher 정렬순서 변경		// orderParam: desc, asc
	let params = makeSearchParam(cri) + "&searchTeacherOrderBy="+orderParam;	
	window.location.href = "/admin/classInfo"+params;
}
/////////////////////////////////////////////////////////////////////////////////////


// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchType = $("#searchType").val();	
	let keyword = $("#keyword").val();		
	
	let searchSite = $("#searchSite").val();
	let searchCenter = $("#searchCenter").val();
	let searchStatus = $("#searchStatus").val();
	let searchClassType = $("#searchClassType").val();
	let searchStartDate = $("#searchStartDate").val();
	let searchEndDate = $("#searchEndDate").val();
	
	let searchLanguage = $("#searchLanguage").val();
	let searchStudy = $("#searchStudy").val();
	let searchTextBook = $("#searchTextBook").val();
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
			   + "&searchSite="+searchSite+ "&searchCenter="+searchCenter+ "&searchStatus="+searchStatus
			   + "&searchClassType="+searchClassType+ "&searchStartDate="+searchStartDate+ "&searchEndDate="+searchEndDate
			   + "&searchLanguage="+searchLanguage+ "&searchStudy="+searchStudy+ "&searchTextBook="+searchTextBook
               + "&searchTimeOrderBy="+cri.searchTimeOrderBy+ "&searchTeacherOrderBy="+cri.searchTeacherOrderBy
	window.location.href = "/admin/classInfo"+params;						
});


// 검색
function search(){
	let keyword = $("#keyword").val().trim();	
	let searchType = $("#searchType").val();
	let perPageNum = $("#perPageNum option:selected").val();
	
	let searchSite = $("#searchSite").val();
	let searchCenter = $("#searchCenter").val();
	let searchStatus = $("#searchStatus").val();
	let searchClassType = $("#searchClassType").val();
	let searchStartDate = $("#searchStartDate").val();
	let searchEndDate = $("#searchEndDate").val();
	
	let searchLanguage = $("#searchLanguage").val();
	let searchStudy = $("#searchStudy").val();
	let searchTextBook = $("#searchTextBook").val();
		
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
			   + "&searchSite="+searchSite+ "&searchCenter="+searchCenter+ "&searchStatus="+searchStatus
			   + "&searchClassType="+searchClassType+ "&searchStartDate="+searchStartDate+ "&searchEndDate="+searchEndDate
			   + "&searchLanguage="+searchLanguage+ "&searchStudy="+searchStudy+ "&searchTextBook="+searchTextBook;
	window.location.href = "/admin/classInfo"+params;
}

function searchCancel(){	
	window.location.href = "/admin/classInfo";
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

// excel 버튼
function excelBtn(cri){
	
	// 수업기본정보 엑셀 다운로드 SaehaLmsExcelRestcontroller
	$.ajax({
		url : '/admin/classInfo/excelDownload',
		type : 'post',
		data : {
			'searchType' : cri.searchType
		  , 'keyword' : cri.keyword	
			
		  , 'searchSite' : cri.searchSite
		  , 'searchCenter' : cri.searchCenter
		  , 'searchStatus' : cri.searchStatus
		  , 'searchClassType' : cri.searchClassType
		  , 'searchStartDate' : cri.searchStartDate
		  , 'searchEndDate' : cri.searchEndDate
		  , 'searchLanguage' : cri.searchLanguage
		  , 'searchStudy' : cri.searchStudy
		  , 'searchTextBook' : cri.searchTextBook
		  , 'searchTimeOrderBy' : cri.searchTimeOrderBy
		  , 'searchTeacherOrderBy' : cri.searchTeacherOrderBy
		},
		success : function(downloadURL) {
			window.location.href = downloadURL;
			alert("엑셀 다운로드가 완료되었습니다.");
		}, error : function() {
			alert("서버통신 오류");
		} // success
	}); // ajax
	
}

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



