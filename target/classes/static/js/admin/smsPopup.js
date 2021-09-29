function closePopoup() {
	window.close();
}


// SureM API 문자발송 테스트 
function sendSms() {
	
	let reserve_yn;
	
	if($('#reserve_yn').is(":checked") == true){		// 예약 체크
		reserve_yn = '1';
	} else {
		reserve_yn = '0';
	}
	
	let reserve_dt = $('#reserve_dt').val().trim();
	let reserve_hour = $('#reserve_hour').val().trim();
	let reserve_min = $('#reserve_min').val().trim();
	
	if(reserve_yn == '1'){	// 예약선택 되어있다면		
		if(reserve_dt == ""){
			alert("예약일을 확인하세요.");
			$('#reserve_dt').focus();
			return false;
		} else if(reserve_hour == ""){
			alert("예약시간을 확인하세요.");
			$('#reserve_hour').focus();
			return false;
		} else if(reserve_min == ""){
			alert("예약시간을 확인하세요.");
			$('#reserve_min').focus();
			return false;
		}
	}
	
	let form = document.querySelector("#smsForm");
	let data = new FormData(form);
	
	data.append('reserve_flag', reserve_yn);
	
	// 체크선택한 회원 SMS 보내기
	$.ajax({   			
		url : '/sms/checkedSend',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {			
			alert("전송되었습니다.");
			window.close();
		}, error : function() {
			alert("전송 실패");			
		} // success
	}); // ajax
}


// 예약 체크박스 change
function reserveChange(e) {		
	let reserve_yn;	//let reserve_yn = e.value;
	
	if($('#reserve_yn').is(":checked") == true){		// 예약 체크				
		$('.reserveDiv').css("display", "block");
	} else {		
		$('.reserveDiv').css("display", "none");
	}
}


//textarea 바이트 수 체크하는 함수
function fnChkByte(obj){
    var str = obj.value;
	var str_len = str.length;
	
	var rbyte = 0;	
	var one_char = "";
	
	for(var i=0; i<str_len; i++){
		one_char = str.charAt(i);
		if(escape(one_char).length > 4){
		    rbyte += 2;                                         //한글2Byte
		}else{
		    rbyte++;                                            //영문 등 나머지 1Byte
		}
	}
	
	// $('#counter').html("("+rbyte+" / 최대 80자)");    //글자수 실시간 카운팅	
	$('#counter').html("("+rbyte+" byte)");    		   //글자수 실시간 카운팅	
	
	if(rbyte >= 80){	// 80byte 이상 LMS
		$('#type2').prop("checked", true);
	} else{
		$('#type1').prop("checked", true);
	}
	
}



