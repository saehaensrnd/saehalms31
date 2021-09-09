window.onload = function(){
	CKEDITOR.replace( "student_etc" );
}

function clase(){
	window.close();
}


// 패스워드 초기화 버튼
function updateEtc(){
	$("textarea[name='student_etc'").val(CKEDITOR.instances["student_etc"].document.getBody().getHtml());
	
	let form = document.querySelector("#etcForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/userInfo/updateEtc',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {			
			alert("수정되었습니다.");
			window.close();
			window.opener.parent.location.reload();
		}, error : function() {
			alert("수정 실패");		
		} // success
	}); // ajax
	
}
