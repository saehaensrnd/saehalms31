// 목록 버튼 클릭
function goList(cri){
	history.back();
}


function deleteBtn(class_no){
	if(confirm("삭제 하시겠습니까? \n(1:N 수업일 경우 해당 수업을 듣는 모든 수강자의 수업이 삭제됩니다.)")){
		$.ajax({   			
			url : '/admin/classInfo/deleteClass',			
			type : 'post',
			data : {'class_no' : class_no
			},			
			success : function() {		
				alert("삭제되었습니다.");
				window.location.href = "/admin/classInfo";	
			}, error : function() {
				alert("삭제 실패");
				
			} // success
		}); // ajax
	}
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




