// 목록 버튼 클릭
function goList(cri){
	history.back();
}

$(document).on("click", "#insertBtn", function(){
	let language_type = $('#language_type').val();
	let holiday_date = $('#holiday_date').val();
			
	
	if(language_type == ""){
		alert("언어를 확인하세요.");
		$('#language_type').focus();
		return false;
	} else if(holiday_date == ""){
		alert("휴강일을 확인하세요.");
		$('#holiday_date').focus();
		return false;
	}
			
	// 유효성 검사 통과 	
	let form = document.querySelector("#insertForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/admin/holiday/insert',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function(data) {		
			if(data.result == 1){
				alert(data.resultMsg);
				window.location.href = "/admin/holiday";	
			} else {
				alert(data.resultMsg);
			}							
			 
		}, error : function() {
			alert("등록 실패");
			
		} // success
	}); // ajax
	
});



$(document).on("click", "#updateBtn", function(){	
	let holiday_date = $('#holiday_date').val();			
	
	if(holiday_date == ""){
		alert("휴강일을 확인하세요.");
		$('#holiday_date').focus();
		return false;
	}
			
	// 유효성 검사 통과 	
	let form = document.querySelector("#updateForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/admin/holiday/update',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function(data) {		
			if(data.result == 1){
				alert(data.resultMsg);
				window.location.href = "/admin/holiday";	
			} else {
				alert(data.resultMsg);
			}							
			 
		}, error : function() {
			alert("수정 실패");
			
		} // success
	}); // ajax
	
});

// 엑셀양식다운로드
function downloadExcelFile(){
	document.location.href = '/excelTemplate/sample_holiday.xlsx';
}


$(document).on("click", "#excelInsertBtn", function(){
	let language_type = $('#language_type_excel').val();
	
	if(language_type == ""){
		alert("언어를 확인하세요.");
		$('#language_type').focus();
		return false;
	} 
			
	// 유효성 검사 통과 	
	let form = document.querySelector("#excelInsertForm");		
	form.submit();
});


