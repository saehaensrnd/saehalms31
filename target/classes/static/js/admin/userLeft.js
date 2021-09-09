window.onload = function(){
	CKEDITOR.replace( "student_etc" );
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

function updateUser() {
	let student_parent_phone_status = '0';
	if($('#student_parent_phone_status_chk').is(':checked') === true){
		student_parent_phone_status = '1';		
	}
	let student_blacklist_status = '0';
	if($('#student_blacklist_status_chk').is(':checked') === true){
		student_blacklist_status = '1';		
	}
	let student_phone_status = '0';
	if($('#student_phone_status_chk').is(':checked') === true){
		student_phone_status = '1';		
	}
	let student_email_status = '0';
	if($('#student_email_status_chk').is(':checked') === true){
		student_email_status = '1';		
	}
	
	$("textarea[name='student_etc'").val(CKEDITOR.instances["student_etc"].document.getBody().getHtml());
	
	let form = document.querySelector("#userForm");
	let data = new FormData(form);
	
	data.append('student_parent_phone_status', student_parent_phone_status);
	data.append('student_blacklist_status', student_blacklist_status);
	data.append('student_phone_status', student_phone_status);
	data.append('student_email_status', student_email_status);
	
	
	$.ajax({   			
		url : '/userInfo/userUpdate',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {			
			alert("수정되었습니다.");
			location.reload();			
			//window.location.href = "/center/teacher";								
			 
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax
}

// 패스워드 초기화 버튼
function pwdClear(user){
	if(confirm("비밀번호를 초기화하시겠습니까?")){
		$.ajax({   			
			url : '/userInfo/userPwdClear',			
			type : 'post',
			data : { 
				'id': user.id,
				'user_no' : user.user_no
			},						
			success : function() {			
				alert("비밀번호가 초기화되었습니다.");
			}, error : function() {
				alert("초기화 실패");
			} // success
		}); // ajax
	}
}

// 유저 SMS 전송
function smsBtn(user){
	let selectedArr = new Array();
	selectedArr.push(user.user_student_no);	
		
	window.open("/sms/openPopup?selectedArr="+selectedArr, 'smsPopup', 'width=550, height=650 left=650, top=150');	
}


// 특이사항 상세보기
function etcPopup(user){
	window.open("/userInfo/ectPopup?user_no=" +user.user_no, 'etcPopup', 'width=600, height=550 left=650, top=150');		
		
}