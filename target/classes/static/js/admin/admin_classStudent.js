function search(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();
	
	let searchSite = $("#searchSite").val();
	let searchLanguage = $("#searchLanguage").val();
	let searchStudy = $("#searchStudy").val();
	let searchProduct = $("#searchProduct").val();
	let searchClassType = $("#searchClassType").val();
	let searchStatus = $("#searchStatus").val();
	
	let searchClassStart_SDT = $("#searchClassStart_SDT").val();
	let searchClassStart_EDT = $("#searchClassStart_EDT").val();
	let searchClassEnd_SDT = $("#searchClassEnd_SDT").val();
	let searchClassEnd_EDT = $("#searchClassEnd_EDT").val();	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
	           + "&searchSite="+searchSite+ "&searchLanguage="+searchLanguage+ "&searchStudy="+searchStudy
			   + "&searchProduct="+searchProduct+ "&searchClassType="+searchClassType+ "&searchStatus="+searchStatus
			   + "&searchClassStart_SDT="+searchClassStart_SDT+ "&searchClassStart_EDT="+searchClassStart_EDT
			   + "&searchClassEnd_SDT="+searchClassEnd_SDT+ "&searchClassEnd_EDT="+searchClassEnd_EDT;
	
	window.location.href = "/admin/classStudentMng"+params;
}

function searchCancel(){	
	window.location.href = "/admin/classStudentMng";
}

function makeSearchParam(cri){
	let params = "page="+cri.page+"&perPageNum="+cri.perPageNum+"&searchType="+cri.searchType+"&keyword="+cri.keyword;
	return params;
}

// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();	

	let searchSite = $("#searchSite").val();
	let searchLanguage = $("#searchLanguage").val();
	let searchStudy = $("#searchStudy").val();
	let searchProduct = $("#searchProduct").val();
	let searchClassType = $("#searchClassType").val();
	let searchStatus = $("#searchStatus").val();
	
	let searchClassStart_SDT = $("#searchClassStart_SDT").val();
	let searchClassStart_EDT = $("#searchClassStart_EDT").val();
	let searchClassEnd_SDT = $("#searchClassEnd_SDT").val();
	let searchClassEnd_EDT = $("#searchClassEnd_EDT").val();	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword
	           + "&searchSite="+searchSite+ "&searchLanguage="+searchLanguage+ "&searchStudy="+searchStudy
			   + "&searchProduct="+searchProduct+ "&searchClassType="+searchClassType+ "&searchStatus="+searchStatus
			   + "&searchClassStart_SDT="+searchClassStart_SDT+ "&searchClassStart_EDT="+searchClassStart_EDT
			   + "&searchClassEnd_SDT="+searchClassEnd_SDT+ "&searchClassEnd_EDT="+searchClassEnd_EDT;
	window.location.href = "/admin/classStudentMng"+params;
});

function openUserDetail(user_student_no){
	let flag = "normal";
	window.open("/userDetail?user_student_no="+user_student_no+"&flag="+flag+"&no=", 'userInfoPopup', 'width=1500, height=900 left=200, top=50');
}

//excel downlaod
function excelDownBtn(cri){
	$.ajax({
		url : '/admin/user/downClassStudent',
		type : 'post',
		data:{
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



// 언어 셀렉트박스 change 이벤트 발생 시 - 수강과정, 수강상품 콤보박스 셀렉트박스 Setting
function languageChange(e) {	
	let searchLanguage = e.value;

	// 수강과정 셀렉트박스 초기화
	let studySelect = document.getElementById("searchStudy");
	studySelect.options.length = 0;
	
	
	// 수강과정 셀렉트박스 세팅
	let studyArr;

	$.ajax({
		url: '/admin/classInfo/getStudyComboByLanguage',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"language": searchLanguage
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
	
	
	
	// 수강상품 셀렉트박스 초기화
	let productSelect = document.getElementById("searchProduct");
	productSelect.options.length = 0;
	
	
	// 수강상품 셀렉트박스 세팅
	let productArr;

	$.ajax({
		url: '/admin/classInfo/getProductComboByLanguage',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"searchLanguage": searchLanguage
		},
		success: function(item) {
			productArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});

	productSelect.options[0] = new Option("전체", "all");

	if (productArr.length > 0) {
		for (i = 1; i <= productArr.length; i++) {
			productSelect.options[i] = new Option(productArr[i - 1].product_name, productArr[i - 1].product_no);		// text, value
		}
	}	
	
	
	jcf.replaceAll();	
} 

