// 언어 셀렉트박스 change 이벤트 발생 시 - 기수년월 콤보박스 셀렉트박스 Setting
function languageChange(e) {	
	let languageType = e.value;

	// 강사 셀렉트박스 세팅
	let semesterYmArr;

	$.ajax({
		url: '/admin/scheduleClassSite/getSemesterYmComboByLanguage',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"language_type": languageType
		},
		success: function(item) {
			semesterYmArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});
	
	// 기수년월 셀렉트박스 초기화
	let semesterYmSelect = document.getElementById("semester_ym");
	/*for (i = 0; i < semesterYmSelect.options.length; i++) {
		semesterYmSelect.options[i] = null;
	}*/
	semesterYmSelect.options.length = 0;
	

	if (semesterYmArr.length > 0) {
		semesterYmSelect.options[0] = new Option("-- 선택 --", "");
	} else {
		semesterYmSelect.options[0] = new Option("-- 언어를 선택하세요 --", "");
	}

	if (semesterYmArr.length > 0) {
		for (i = 1; i <= semesterYmArr.length; i++) {
			semesterYmSelect.options[i] = new Option(semesterYmArr[i - 1].semester_ym, semesterYmArr[i - 1].semester_ym);		// text, value
		}
	}
	
	jcf.replaceAll();

}

// 목록 버튼 클릭
function goList(cri){
	history.back();
}


$(document).on("click", "#insertBtn", function(){
	let language_type = $('#language_type').val();
	let semester_ym = $('#semester_ym').val();
	let site_no = $('#site_no').val();
	
	let reapplication_start_date = $('#reapplication_start_date').val();
	let reapplication_end_date = $('#reapplication_end_date').val();
	let application_start_date = $('#application_start_date').val();
	let application_end_date = $('#application_end_date').val();		
	let level_application_start_date = $('#level_application_start_date').val();
	let level_application_end_date = $('#level_application_end_date').val();
			
	
	if(language_type == ""){
		alert("언어를 확인하세요.");
		$('#language_type').focus();
		return false;
	} else if(semester_ym == ""){
		alert("기수 년월을 확인하세요.");
		$('#semester_ym').focus();
		return false;
	} else if(site_no == ""){
		alert("사이트를 확인하세요.");
		$('#site_no').focus();
		return false;
	} else if(reapplication_start_date == ""){
		alert("재수강 신청 시작일을 확인하세요.");
		$('#reapplication_start_date').focus();
		return false;
	} else if(reapplication_end_date == ""){
		alert("재수강 신청 종료일을 확인하세요.");
		$('#reapplication_end_date').focus();
		return false;
	} else if(application_start_date == ""){
		alert("일반 신청 시작일을 확인하세요.");
		$('#application_start_date').focus();
		return false;
	} else if(application_end_date == ""){
		alert("일반 신청 종료일을 확인하세요.");
		$('#application_end_date').focus();
		return false;
	}	
	 
	let dt_reapplication_start_date = new Date(reapplication_start_date);
	let dt_reapplication_end_date = new Date(reapplication_end_date);
	let dt_application_start_date = new Date(application_start_date);
	let dt_application_end_date = new Date(application_end_date);	
		
	
	if (dt_reapplication_start_date > dt_reapplication_end_date){
		alert("재수강 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
		return false;
	} else if (dt_application_start_date > dt_application_end_date){
		alert("일반 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
		return false;
	} 
	
		
	if(level_application_start_date != "" && level_application_end_date != "") {	
		let dt_level_application_start_date = new Date(level_application_start_date);
		let dt_level_application_end_date = new Date(level_application_end_date);
			
		if (dt_level_application_start_date > dt_level_application_end_date){
			alert("레벨테스트 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
			return false;
		}
	}
	
			
	// 유효성 검사 통과 	
	let form = document.querySelector("#insertForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/admin/scheduleClassSite/insert',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function(data) {		
			if(data.result == 1){
				alert(data.resultMsg);
				window.location.href = "/admin/scheduleClassSite";	
			} else {
				alert(data.resultMsg);
			}						
			 
		}, error : function() {
			alert("등록 실패");
			
		} // success
	}); // ajax
	
});


$(document).on("click", "#updateBtn", function(){
	let reapplication_start_date = $('#reapplication_start_date').val();
	let reapplication_end_date = $('#reapplication_end_date').val();
	let application_start_date = $('#application_start_date').val();
	let application_end_date = $('#application_end_date').val();		
	let level_application_start_date = $('#level_application_start_date').val();
	let level_application_end_date = $('#level_application_end_date').val();
			
	
	if(reapplication_start_date == ""){
		alert("재수강 신청 시작일을 확인하세요.");
		$('#reapplication_start_date').focus();
		return false;
	} else if(reapplication_end_date == ""){
		alert("재수강 신청 종료일을 확인하세요.");
		$('#reapplication_end_date').focus();
		return false;
	} else if(application_start_date == ""){
		alert("일반 신청 시작일을 확인하세요.");
		$('#application_start_date').focus();
		return false;
	} else if(application_end_date == ""){
		alert("일반 신청 종료일을 확인하세요.");
		$('#application_end_date').focus();
		return false;
	}	
	 
	let dt_reapplication_start_date = new Date(reapplication_start_date);
	let dt_reapplication_end_date = new Date(reapplication_end_date);
	let dt_application_start_date = new Date(application_start_date);
	let dt_application_end_date = new Date(application_end_date);	
		
	
	if (dt_reapplication_start_date > dt_reapplication_end_date){
		alert("재수강 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
		return false;
	} else if (dt_application_start_date > dt_application_end_date){
		alert("일반 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
		return false;
	} 
	
		
	if(level_application_start_date != "" && level_application_end_date != "") {	
		let dt_level_application_start_date = new Date(level_application_start_date);
		let dt_level_application_end_date = new Date(level_application_end_date);
			
		if (dt_level_application_start_date > dt_level_application_end_date){
			alert("레벨테스트 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
			return false;
		}
	}
	
			
	// 유효성 검사 통과 	
	let form = document.querySelector("#updateForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/admin/scheduleClassSite/update',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function(data) {		
			if(data.result == 1){
				alert(data.resultMsg);
				window.location.href = "/admin/scheduleClassSite";	
			} else {
				alert(data.resultMsg);
			}
		}, error : function() {
			alert("수정 실패");			
		} // success
	}); // ajax
	
});


function deleteBtn(item){
	if(confirm("삭제 하시겠습니까?")){
		$.ajax({   			
			url : '/admin/scheduleClassSite/delete',			
			type : 'post',			
			data: {'schedule_class_site_no' : item.schedule_class_site_no
			},					
			success : function() {	
                alert("삭제되었습니다.");
                window.location.href = "/admin/scheduleClassSite";
			}, error : function() {
				alert("삭제 실패");
			} // success
		}); // ajax						
	}
	
}

