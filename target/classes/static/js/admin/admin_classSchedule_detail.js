/* MonthPicker 옵션 */
options = {
	pattern: 'yyyy-mm', // Default is 'mm/yyyy' and separator char is not mandatory
	selectedYear: new Date().getFullYear(), // 2021,
	startYear: 2010,
	finalYear: new Date().getFullYear() + 20,  // 2060,
	monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
};

/* MonthPicker Set */
$('#semester_ym').monthpicker(options);	

/* MonthPicker 선택 이벤트
$('#semester_ym').monthpicker().bind('monthpicker-click-month', function (e, month) {
	alert("선택하신 월은 : " + month + "월");
});
*/


// 목록 버튼 클릭
function goList(cri){
	history.back();
}

$(document).on("click", "#insertBtn", function(){
	let language_type = $('#language_type').val();
	let semester_ym = $('#semester_ym').val();
	
	let reapplication_start_date = $('#reapplication_start_date').val();
	let reapplication_end_date = $('#reapplication_end_date').val();
	let application_start_date = $('#application_start_date').val();
	let application_end_date = $('#application_end_date').val();
	let class_start_date = $('#class_start_date').val();
	let class_end_date = $('#class_end_date').val();
	let level_application_start_date = $('#level_application_start_date').val();
	let level_application_end_date = $('#level_application_end_date').val();
			
	
	if(language_type == ""){
		alert("언어를 확인하세요.");
		$('#language_type').focus();
		return false;
	} else if(semester_ym == ""){
		alert("기수명을 확인하세요.");
		$('#semester_ym').focus();
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
	} else if(class_start_date == ""){
		alert("수업 시작일을 확인하세요.");
		$('#class_start_date').focus();
		return false;
	} else if(class_end_date == ""){
		alert("수업 종료일을 확인하세요.");
		$('#class_end_date').focus();
		return false;
	} 
	
	let dt_reapplication_start_date = new Date(reapplication_start_date);
	let dt_reapplication_end_date = new Date(reapplication_end_date);
	let dt_application_start_date = new Date(application_start_date);
	let dt_application_end_date = new Date(application_end_date);
	let dt_class_start_date = new Date(class_start_date);
	let dt_class_end_date = new Date(class_end_date);	
	
	if (dt_reapplication_start_date > dt_reapplication_end_date){
		alert("재수강 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
		return false;
	} else if (dt_application_start_date > dt_application_end_date){
		alert("일반 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
		return false;
	} else if (dt_class_start_date > dt_class_end_date){
		alert("수업 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
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
	
	
	
	// 체크박스 처리
	let saturday_status = '0';
	let sunday_status = '0';
	
	if($('#saturday_status_chk').is(':checked') === true){
		saturday_status = '1';		
	}
	if($('#sunday_status_chk').is(':checked') === true){
		sunday_status = '1';		
	} 
	
			
	// 유효성 검사 통과 	
	let form = document.querySelector("#insertForm");
	let data = new FormData(form);	
	
	data.append('saturday_status', saturday_status);	// 토요일 수업가능 여부
	data.append('sunday_status', sunday_status);		// 일요일 수업가능 여부	
	
	$.ajax({   			
		url : '/admin/classSchedule/insert',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function(data) {		
			if(data.result == 1){
				alert(data.resultMsg);
				window.location.href = "/admin/classSchedule";	
			} else {
				alert(data.resultMsg);
			}						
			 
		}, error : function() {
			alert("등록 실패");
			
		} // success
	}); // ajax
	
});



$(document).on("click", "#updateBtn", function(){	
	let semester_ym = $('#semester_ym').val();
	
	let reapplication_start_date = $('#reapplication_start_date').val();
	let reapplication_end_date = $('#reapplication_end_date').val();
	let application_start_date = $('#application_start_date').val();
	let application_end_date = $('#application_end_date').val();
	let class_start_date = $('#class_start_date').val();
	let class_end_date = $('#class_end_date').val();
	let level_application_start_date = $('#level_application_start_date').val();
	let level_application_end_date = $('#level_application_end_date').val();
			
	
	if(semester_ym == ""){
		alert("기수명을 확인하세요.");
		$('#semester_ym').focus();
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
	} else if(class_start_date == ""){
		alert("수업 시작일을 확인하세요.");
		$('#class_start_date').focus();
		return false;
	} else if(class_end_date == ""){
		alert("수업 종료일을 확인하세요.");
		$('#class_end_date').focus();
		return false;
	} 	
	
	let dt_reapplication_start_date = new Date(reapplication_start_date);
	let dt_reapplication_end_date = new Date(reapplication_end_date);
	let dt_application_start_date = new Date(application_start_date);
	let dt_application_end_date = new Date(application_end_date);
	let dt_class_start_date = new Date(class_start_date);
	let dt_class_end_date = new Date(class_end_date);
	
		
	if (dt_reapplication_start_date > dt_reapplication_end_date){
		alert("재수강 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
		return false;
	} else if (dt_application_start_date > dt_application_end_date){
		alert("일반 신청 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
		return false;
	} else if (dt_class_start_date > dt_class_end_date){
		alert("수업 종료일을 확인하세요. 종료일이 시작일 보다 작을 수 없습니다");		
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

	
	// 체크박스 처리
	let saturday_status = '0';
	let sunday_status = '0';
	
	if($('#saturday_status_chk').is(':checked') === true){
		saturday_status = '1';		
	}
	if($('#sunday_status_chk').is(':checked') === true){
		sunday_status = '1';		
	} 
	
			
	// 유효성 검사 통과 	
	let form = document.querySelector("#updateForm");
	let data = new FormData(form);	
	
	data.append('saturday_status', saturday_status);	// 토요일 수업가능 여부
	data.append('sunday_status', sunday_status);		// 일요일 수업가능 여부	
	
	$.ajax({   			
		url : '/admin/classSchedule/update',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function(data) {		
			if(data.result == 1){
				alert(data.resultMsg);
				window.location.href = "/admin/classSchedule";	
			} else {
				alert(data.resultMsg);
			}						
			 
		}, error : function() {
			alert("등록 실패");
			
		} // success
	}); // ajax
	
});


function deleteBtn(item){
	if(confirm("삭제 하시겠습니까?")){
		$.ajax({   			
			url : '/admin/classSchedule/delete',			
			type : 'post',			
			data: {'schedule_class_no' : item.schedule_class_no
			},					
			success : function() {	
                alert("삭제되었습니다.");
                window.location.href = "/admin/classSchedule";
			}, error : function() {
				alert("삭제 실패");
			} // success
		}); // ajax						
	}
	
}

