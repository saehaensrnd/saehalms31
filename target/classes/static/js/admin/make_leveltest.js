window.onload = function(){
	CKEDITOR.replace( "leveltest_content" );
}

function selfClose(){
	self.close();
}

function insertLeveltest(){
	$("textarea[name='leveltest_content'").val(CKEDITOR.instances["leveltest_content"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#leveltest_form'));
	
	$.ajax({   			
		url : '/userDetail/insertLeveltest',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(count) {
			if(count == -1){
				alert("등록되었습니다.");
				opener.parent.location.reload();
				self.close();
			}
			else{
				alert("레벨테스트를 "+count+"회 신청하여 더이상 레벨테스트를 등록할 수 없는 유저입니다.");
			}
		}, error : function() {
			alert("등록 실패");
		}
	}); // ajax
}

function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}