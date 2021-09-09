function selfClose(){
	self.close();
}

function searchStudent(){
	var formData = new FormData(document.querySelector('#student_search_form'));
	
	$.ajax({   			
		url : '/userDetail/search_student',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(data) {
			console.log(data);
			var studentSel = document.querySelector("#user_student_no");
			studentSel.options.length = 0;

			if(data.length > 0){
				for(var i=0; i<data.length; i++){
					studentSel.options[i] = new Option(data[i].student_name, data[i].user_student_no);
				}
			}
			else{
				studentSel.options[0] = new Option("-- 선택 --", 0);
			}
			
			jcf.replaceAll();
			
			alert("검색이 완료되었습니다.");
		}, error : function() {
			alert("검색 실패");
		}
	}); // ajax
}

function selectStudent(){
	if($("#user_student_no").val() == 0){
		alert("학생을 선택해주세요");
	}
	else{
		window.open("/userDetail/makeLeveltest?user_student_no="+$("#user_student_no").val(), 'makeLeveltest', 'width=800, height=540 left=550, top=70');
	}
}