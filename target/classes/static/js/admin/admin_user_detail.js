var idCheck = false;

window.onload = function(){
	CKEDITOR.replace( "student_etc" );
	
	idCheck = false;
}

// 목록 버튼 클릭
function goList(cri){
	let params = "?page="+cri.page+"&perPageNum="+cri.perPageNum+"&keyword="+cri.keyword;
	window.location.href = "/admin/user"+params;
}

function onlyEnglish(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^A-Za-z\s]/g, '');
	}
}

function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}

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

function changeID(){
	idCheck = false;
}

function checkID(){
	if($("input[name='student_id']").val() == ""){
		idCheck = false;
		$("input[name='student_id']").focus();
		alert("아이디를 입력해주세요.");
	}
	else{
		$.ajax({   			
			url : '/admin/user/checkID',			
			type : 'post',
			data : {
				id: $("input[name='student_id']").val()
			},
			success : function(isID) {						
				 if(isID){
					idCheck = false;
					 $("input[name='student_id']").focus();
					 alert("중복되는 아이디입니다.");
				 }
				 else{
					 idCheck = true;
					 alert("사용할 수 있는 아이디입니다.");
				 }
			}, error : function() {
				alert("아이디 중복확인 오류");
			}
		}); // ajax
	}
}

function userInsert(){
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
	if($("input[name='student_id']").val().length < 0 || $("input[name='student_id']").val() == ""){
		$("input[name='student_id']").focus();
		alert("아이디를 입력해주세요.");
		return false;
	}
	if(!idCheck){
		$("input[name='student_id']").focus();
		alert("아이디 중복확인을 해주세요.");
		return false;
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
	if($("input[name='student_phone']").val().length < 0 || $("input[name='student_phone']").val() == ""){
		$("input[name='student_phone']").focus();
		alert("회원 휴대폰 번호가 비어있습니다.");
		return false;
	}
	if($("input[name='student_phone']").val().length < 9){
		$("input[name='student_phone']").focus();
		alert("휴대폰 번호가 너무 짧습니다.");
		return false;
	}
	if($("input[name='student_email']").val().length > 0 || $("input[name='student_email']").val() != ""){
		if(!$("input[name='student_email']").val().match(/^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i)){
			$("input[name='student_email']").focus();
			alert("이메일 형식이 아닙니다.");
			return false;
		}
	}
	
	
	$("textarea[name='student_etc'").val(CKEDITOR.instances["student_etc"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#student_form'));
	
	$.ajax({   			
		url : '/admin/user/insertUser',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("등록되었습니다.");			
			window.location.href = "/admin/user";
			 
		}, error : function() {
			alert("등록 실패");
		}
	}); // ajax
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
	
	$("textarea[name='student_etc'").val(CKEDITOR.instances["student_etc"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#student_form'));
	
	$.ajax({   			
		url : '/admin/user/updateUser',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("수정되었습니다.");
			window.location.href = "/admin/user";					
			 
		}, error : function() {
			alert("수정 실패");
		}
	}); // ajax		
	
}


