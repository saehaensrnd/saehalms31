/****************** 유효성 검사 정규식 ******************/
//모든 공백 체크 정규식
var empChk = /\s/g;

//아이디 정규식	: 문자로 시작하며, 그뒤에 문자 or 숫자 2글자이상 30자이하
var idChk = /^[a-zA-Z][a-zA-Z0-9]{2,30}$/;

// 비밀번호 정규식 문자나 숫자 4~12자
//var pwJ = /^[A-Za-z0-9]{4,12}$/; 
//	최소 4 자 최대 12자, 최소 하나의 문자 및 하나의 숫자
var pwChk = /(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{4,12}$/;

// 이름 정규식
var nameChk = /[a-zA-Z가-힝]{2,20}$/;
// 이메일 검사 정규식
var mailChk = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/; //이메일 형식이 알파벳+숫자@알파벳+숫자.알파벳+숫자 형식

// 휴대폰 번호 정규식
var phoneChk = /^01([0|1|6|7|8|9]?)?([0-9]{3,4})?([0-9]{4})$/;

//var telChk = /^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$/;
//var telChk = /^[0-9]+$/g; // 숫자만 입력가능    //[^0-9]/g;   // /^[0-9]*$/;
var telChk = /^[0-9]{9,11}$/;  


//////////////////////////////////////////////////////////////////////////////////////////////////////

if (flag == 'update'){
	CKEDITOR.replace("teacher_name");
}

function goList(cri){			
	//let params = "page="+cri.page+"&perPageNum="+cri.perPageNum;
	//window.location.href = "/admin/teacher?"+params;
	history.back();
}


// 아이디 중복 여부 플래그 submit 버튼 때 사용 
var idFlag = 1;	// (0: 중복아님   1:중복)

// 아이디 중복 체크 및 유효성 검사
$(document).on("blur", "#id", function(){
	var user_id = $('#id').val().toLowerCase().replace(/\s/g, "");  	// 대문자는 소문자로, 스페이스 공백은 "" 전환
	
	//console.log(user_id);
	
	$.ajax({   			
		url : '/idCheck',			
		type : 'post',
		data : {'userId':user_id},	
		
		success : function(data) {				
			if (data == 'exist') {
					return idFlag = 1;					
			} else {
				return idFlag = 0;
			}
		}, error : function() {
				console.log("실패");
		} // success
	}); // ajax
	
});



// 저장 버튼 클릭 시 유효성 검사
$(document).on("click", "#insertBtn", function(){
	let center_no = $('#center_no').val().trim();
	let teacher_language_type = $('#teacher_language_type').val().trim();
	let teacher_name = $('#teacher_name').val().trim();
	let teacher_nickname = $('#teacher_nickname').val().trim();
	let pwd = $('#pwd').val().trim();
	let id = $('#id').val().trim();	
	let teacher_class_type = $('#teacher_class_type').val().trim();
	let teacher_national = $('#teacher_national').val().trim();
	
	let teacher_phone = $('#teacher_phone').val().trim();
	
		
	if(center_no == ""){
		alert("티칭센터를 확인하세요.");
		$('#center_no').focus();
		return false;
	} else if(teacher_language_type == ""){
		alert("언어를 확인하세요.");
		$('#teacher_language_type').focus();
		return false;
	} else if(teacher_nickname == ""){
		alert("수업용 이름을 확인하세요.");
		$('#teacher_nickname').focus();
		return false;
	} else if(teacher_name == ""){
		alert("선생님 전체이름을 확인하세요.");
		$('#teacher_name').focus();
		return false;
	} else if(id == ""){
		alert("아이디를 확인하세요.");
		$('#id').focus();
		return false;
	} else if(pwd == ""){
		alert("비밀번호를 확인하세요.");
		$('#pwd').focus();
		return false;
	} else if(teacher_class_type == ""){
		alert("수업구분을 확인하세요.");
		$('#teacher_class_type').focus();
		return false;
	} else if(teacher_national == ""){
		alert("국적을 확인하세요.");
		$('#teacher_national').focus();
		return false;
	}  
	
	// 휴대폰 유효성검사 추가 10~11자리 숫자
	if(teacher_phone != "" && !telChk.test(teacher_phone)){
		alert("휴대폰 입력 형식이 유효하지 않습니다. [10 ~ 11자리 숫자]");
		$('#teacher_phone').focus();
		return false;
	}	
	 	 			
	
	if (idFlag == 1) {			
		alert("아이디를 확인하세요. 빈칸이거나 사용중인 아이디입니다.");
		$('#id').focus();
		return false;
	}	  		
	
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#insertForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/admin/teacher/insert',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("등록되었습니다.");			
			window.location.href = "/admin/teacher";								
			 
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
});


// 수정 버튼 클릭 시 유효성 검사 - update
$(document).on("click", "#updateBtn", function(){	
	let start_hour = $('#start_hour').val();
	let start_min = $('#start_min').val();
	let end_hour = $('#end_hour').val();
	let end_min = $('#end_min').val();
	
	let teacher_language_type = $('#teacher_language_type').val().trim();
	let teacher_name = $('#teacher_name').val().trim();
	let teacher_nickname = $('#teacher_nickname').val().trim();
	let pwd = $('#pwd').val().trim();	
	let teacher_class_type = $('#teacher_class_type').val().trim();
	let teacher_national = $('#teacher_national').val().trim();
	let teacher_status = $('#teacher_status').val().trim();
	
		
	if(teacher_language_type == ""){
		alert("언어를 확인하세요.");
		$('#teacher_language_type').focus();
		return false;
	} else if(teacher_nickname == ""){
		alert("수업용 이름을 확인하세요.");
		$('#teacher_nickname').focus();
		return false;
	} else if(teacher_name == ""){
		alert("선생님 전체이름을 확인하세요.");
		$('#teacher_name').focus();
		return false;
	} else if(pwd == ""){
		alert("비밀번호를 확인하세요.");
		$('#pwd').focus();
		return false;
	} else if(teacher_class_type == ""){
		alert("수업구분을 확인하세요.");
		$('#teacher_class_type').focus();
		return false;
	} else if(teacher_national == ""){
		alert("국적을 확인하세요.");
		$('#teacher_national').focus();
		return false;
	} else if(teacher_status == ""){
		alert("상태를 확인하세요.");
		$('#teacher_status').focus();
		return false;
	} 	

	// 배정시간 유효성검사.. start_time보다 end_time이 크지 않다면 alert
	if(end_hour < start_hour) {	// 종료시간이 시작시간보다 작다면
		alert("배정시간을 확인하세요. 배정 종료시간이 시작시간보다 같거나 작을 수 없습니다.");		
		return false;
	} else if (end_hour == start_hour) {
		if(end_min <= start_min){
			alert("배정시간을 확인하세요. 배정 종료시간이 시작시간보다 같거나 작을 수 없습니다.");		
			return false;
		}
	}
	
	$("textarea[name='teacher_name']").val(CKEDITOR.instances["teacher_name"].document.getBody().getHtml());
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#updateForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/admin/teacher/update',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("수정되었습니다.");			
			window.location.href = "/admin/teacher";								
			 
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
});


// 강사 근무시간 - 저장버튼 클릭
$(document).on("click", "#timeUpdateBtn", function(){	
			 	
	let form = document.querySelector("#timeTableForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/admin/teacher/timeTableUpdate',			
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
	
});


// 요일 클릭 - 해당 요일 전체선택/해제
function allCheck_col(dayNum){
	let chkList = $(".day"+dayNum);	
	//let chkList = $("input[class='check day"+dayNum+"']");
		
	let checkedCnt = 0;
	
	for(let i=0; i<chkList.length; i++ ){		
		//chkList[i].checked = this.checked;		
		if(chkList[i].checked === true){
			checkedCnt++;
		}		
	}		
	
	if(checkedCnt == chkList.length) {	// 전체선택중이라면 전체해제
		for(let i=0; i<chkList.length; i++ ){
			chkList[i].checked = false;
		}
	} else {	
		for(let i=0; i<chkList.length; i++ ){
			chkList[i].checked = true;
		}
	}	
}


// 주간 전체선택 클릭
function allCheck(){
	let chkList = $(".check");	
	//console.log(chkList.length);
	
	let checkedCnt = 0;
	
	for(let i=0; i<chkList.length; i++ ){			
		if(chkList[i].checked === true){
			checkedCnt++;
		}		
	}
	
	if(checkedCnt == chkList.length) {	// 전체선택중이라면 전체해제
		for(let i=0; i<chkList.length; i++ ){
			chkList[i].checked = false;
		}
	} else {	
		for(let i=0; i<chkList.length; i++ ){
			chkList[i].checked = true;
		}
	}
	
}





