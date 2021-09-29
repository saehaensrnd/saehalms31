// 주소찾기
function daumPostCode(){
	new daum.Postcode({
		oncomplete: function(data){
			var addr = '';
			
			if(data.userSelectedType === 'R'){
				addr = data.roadAddress;
			}
			else{
				addr = data.jibunAddress;
			}
			
			document.querySelector("#zip_code").value = data.zonecode;
			document.querySelector("#address").value = addr;
			document.querySelector("#address_detail").focus();
		}
	}).open();
}


// 수정 버튼 클릭
function updateBtn(){
	if($("input[name='student_name']").val().length < 0 || $("input[name='student_name']").val() == ""){
		$("input[name='student_name']").focus();
		alert("회원명이 비어있습니다.");
		return false;
	}
	if($("input[name='student_eng_name']").val().length < 0 || $("input[name='student_eng_name']").val() == ""){
		$("input[name='student_eng_name']").focus();
		alert("회원 영문명이 비어있습니다.");
		return false;
	}
	if($("input[name='student_phone']").val().length < 0 || $("input[name='student_phone']").val() == ""){
		$("input[name='student_phone']").focus();
		alert("회원 휴대폰 번호가 비어있습니다.");
		return false;
	}
	if($("input[name='student_email']").val().length > 0 || $("input[name='student_email']").val() != ""){
		if(!$("input[name='student_email']").val().match(/^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i)){
			$("input[name='student_email']").focus();
			alert("이메일 형식이 아닙니다.");
			return false;
		}
	}
	if($("input[name='student_pwd']").val().length < 0 || $("input[name='student_pwd']").val() == ""){
		$("input[name='student_pwd']").focus();
		alert("비밀번호를 입력해주세요.");
		return false;
	}
	if($("input[name='student_pwd']").val() != $("input[name='student_pwd2']").val()){
		$("input[name='student_pwd2']").focus();
		alert("비밀번호 확인이 다릅니다.");
		return false;
	}
	
	
	var formData = new FormData(document.querySelector('#updateForm'));
	
	$.ajax({   			
		url : '/student/infoModify/updateUser',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("수정되었습니다.");
			//window.location.href = "/student/infoModify";
			location.reload();
		}, error : function() {
			alert("수정 실패");
		}
	}); // ajax		
	
}