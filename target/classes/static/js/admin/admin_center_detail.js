// 목록 버튼 클릭
function goList(cri){
	history.back();
}


$(document).on("click", "#insertBtn", function(){
	let center_name = $('#center_name').val().trim();	// 이름
	let center_corporate_name = $('#center_corporate_name').val().trim();		// 법인명
	let center_ceo_phone = $('#center_ceo_phone').val().trim();	// 대표연락처
	
	
	if(center_name == ""){
		alert("이름을 확인하세요.");
		$('#center_name').focus();
		return false;
	} else if(center_corporate_name == ""){
		alert("법인명을 확인하세요.");
		$('#center_corporate_name').focus();
		return false;
	} else if(center_ceo_phone == ""){
		alert("대표연락처를 확인하세요.");
		$('#center_ceo_phone').focus();
		return false;
	}	
	
	
	// 고객센터 평일시간
	let start_service_normal_hour = $('#start_service_normal_hour').val();
	let start_service_normal_min = $('#start_service_normal_min').val();	
	let end_service_normal_hour = $('#end_service_normal_hour').val();
	let end_service_normal_min = $('#end_service_normal_min').val();
	
	let start_service_normal = "";	// 0600
	let end_service_normal = "";	// 1800	
	
	let center_service_center_normal_time = "";	// 고객센터 평일시간 param
	
	if (start_service_normal_hour == "" || start_service_normal_min == "") {
		start_service_normal = "";
	} else {
		start_service_normal = start_service_normal_hour + start_service_normal_min;
	}
	
	if (end_service_normal_hour == "" || end_service_normal_min == "") {
		end_service_normal = "";
	} else {
		end_service_normal = end_service_normal_hour + end_service_normal_min;
	}	
	
	if(start_service_normal == "" && end_service_normal == ""){
		center_service_center_normal_time = '';	
	} else {
		center_service_center_normal_time = start_service_normal + '/' + end_service_normal;
	}
	
	
	//console.log("고객센터 평일시간: " + center_service_center_normal_time);	
	/////////////////////////////////////////////////////////////////////////////////////////	
	
	
	// 고객센터 점심시간
	let start_service_lunch_hour = $('#start_service_lunch_hour').val();
	let start_service_lunch_min = $('#start_service_lunch_min').val();	
	let end_service_lunch_hour = $('#end_service_lunch_hour').val();
	let end_service_lunch_min = $('#end_service_lunch_min').val();
	
	let start_service_lunch = "";	// 1200
	let end_service_lunch = "";	// 1300	
	
	let center_service_center_lunch_time = "";	// 고객센터 점심시간 param
	
	if (start_service_lunch_hour != "" && start_service_lunch_min != "") {
		start_service_lunch = start_service_lunch_hour + start_service_lunch_min;
	}	
	if (end_service_lunch_hour != "" && end_service_lunch_min != "") {		
		end_service_lunch = end_service_lunch_hour + end_service_lunch_min;
	}
	
	if(start_service_lunch == "" && end_service_lunch == ""){
		center_service_center_lunch_time = '';	
	} else {
		center_service_center_lunch_time = start_service_lunch + '/' + end_service_lunch;
	}
	
	
	//console.log("고객센터 점심시간: " + center_service_center_lunch_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	// 원격센터 평일시간
	let start_remote_normal_hour = $('#start_remote_normal_hour').val();
	let start_remote_normal_min = $('#start_remote_normal_min').val();	
	let end_remote_normal_hour = $('#end_remote_normal_hour').val();
	let end_remote_normal_min = $('#end_remote_normal_min').val();
	
	let start_remote_normal = "";	// 1200
	let end_remote_normal = "";		// 1300	
	
	let center_remote_center_normal_time = "";	// 원격센터 점심시간 param
	
	if (start_remote_normal_hour != "" && start_remote_normal_min != "") {
		start_remote_normal = start_remote_normal_hour + start_remote_normal_min;
	}	
	if (end_remote_normal_hour != "" && end_remote_normal_min != "") {		
		end_remote_normal = end_remote_normal_hour + end_remote_normal_min;
	}
	
	
	if(start_remote_normal == "" && end_remote_normal == ""){
		center_remote_center_normal_time = '';	
	} else {
		center_remote_center_normal_time = start_remote_normal + '/' + end_remote_normal;
	}
	
	//console.log("원격센터 평일시간: " + center_remote_center_normal_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	// 원격센터 점심시간
	let start_remote_lunch_hour = $('#start_remote_lunch_hour').val();
	let start_remote_lunch_min = $('#start_remote_lunch_min').val();	
	let end_remote_lunch_hour = $('#end_remote_lunch_hour').val();
	let end_remote_lunch_min = $('#end_remote_lunch_min').val();
	
	let start_remote_lunch = "";	// 1200
	let end_remote_lunch = "";		// 1300	
	
	let center_remote_center_lunch_time = "";	// 원격센터 점심시간 param
	
	if (start_remote_lunch_hour != "" && start_remote_lunch_min != "") {
		start_remote_lunch = start_remote_lunch_hour + start_remote_lunch_min;
	}	
	if (end_remote_lunch_hour != "" && end_remote_lunch_min != "") {		
		end_remote_lunch = end_remote_lunch_hour + end_remote_lunch_min;
	}
	
	if(start_remote_lunch == "" && end_remote_lunch == ""){
		center_remote_center_lunch_time = '';	
	} else {
		center_remote_center_lunch_time = start_remote_lunch + '/' + end_remote_lunch;
	}
	
	
	//console.log("원격센터 점심시간: " + center_remote_center_lunch_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#insertForm");
	let data = new FormData(form);
	
	data.append("center_service_center_normal_time", center_service_center_normal_time);	// 고객센터 평일시간
	data.append("center_service_center_lunch_time", center_service_center_lunch_time);		// 고객센터 점심시간
	data.append("center_remote_center_normal_time", center_remote_center_normal_time);		// 원격센터 평일시간
	data.append("center_remote_center_lunch_time", center_remote_center_lunch_time);		// 원격센터 점심시간
	
	
	$.ajax({   			
		url : '/admin/center/insert',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("등록되었습니다.");			
			window.location.href = "/admin/center";								
			 
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
});




$(document).on("click", "#updateBtn", function(){
	let center_name = $('#center_name').val().trim();	// 이름
	let center_corporate_name = $('#center_corporate_name').val().trim();		// 법인명
	let center_ceo_phone = $('#center_ceo_phone').val().trim();	// 대표연락처
	
	
	if(center_name == ""){
		alert("이름을 확인하세요.");
		$('#center_name').focus();
		return false;
	} else if(center_corporate_name == ""){
		alert("법인명을 확인하세요.");
		$('#center_corporate_name').focus();
		return false;
	} else if(center_ceo_phone == ""){
		alert("대표연락처를 확인하세요.");
		$('#center_ceo_phone').focus();
		return false;
	}	
	
	
	// 고객센터 평일시간
	let start_service_normal_hour = $('#start_service_normal_hour').val();
	let start_service_normal_min = $('#start_service_normal_min').val();	
	let end_service_normal_hour = $('#end_service_normal_hour').val();
	let end_service_normal_min = $('#end_service_normal_min').val();
	
	let start_service_normal = "";	// 0600
	let end_service_normal = "";	// 1800	
	
	let center_service_center_normal_time = "";	// 고객센터 평일시간 param
	
	if (start_service_normal_hour == "" || start_service_normal_min == "") {
		start_service_normal = "";
	} else {
		start_service_normal = start_service_normal_hour + start_service_normal_min;
	}
	
	if (end_service_normal_hour == "" || end_service_normal_min == "") {
		end_service_normal = "";
	} else {
		end_service_normal = end_service_normal_hour + end_service_normal_min;
	}
	
	if(start_service_normal == "" && end_service_normal == ""){
		center_service_center_normal_time = '';	
	} else {
		center_service_center_normal_time = start_service_normal + '/' + end_service_normal;
	}	
	
	//console.log("고객센터 평일시간: " + center_service_center_normal_time);	
	/////////////////////////////////////////////////////////////////////////////////////////	
	
	
	// 고객센터 점심시간
	let start_service_lunch_hour = $('#start_service_lunch_hour').val();
	let start_service_lunch_min = $('#start_service_lunch_min').val();	
	let end_service_lunch_hour = $('#end_service_lunch_hour').val();
	let end_service_lunch_min = $('#end_service_lunch_min').val();
	
	let start_service_lunch = "";	// 1200
	let end_service_lunch = "";	// 1300	
	
	let center_service_center_lunch_time = "";	// 고객센터 점심시간 param
	
	if (start_service_lunch_hour != "" && start_service_lunch_min != "") {
		start_service_lunch = start_service_lunch_hour + start_service_lunch_min;
	}	
	if (end_service_lunch_hour != "" && end_service_lunch_min != "") {		
		end_service_lunch = end_service_lunch_hour + end_service_lunch_min;
	}
	
	if(start_service_lunch == "" && end_service_lunch == ""){
		center_service_center_lunch_time = '';	
	} else {
		center_service_center_lunch_time = start_service_lunch + '/' + end_service_lunch;
	}
	
	//console.log("고객센터 점심시간: " + center_service_center_lunch_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	// 원격센터 평일시간
	let start_remote_normal_hour = $('#start_remote_normal_hour').val();
	let start_remote_normal_min = $('#start_remote_normal_min').val();	
	let end_remote_normal_hour = $('#end_remote_normal_hour').val();
	let end_remote_normal_min = $('#end_remote_normal_min').val();
	
	let start_remote_normal = "";	// 1200
	let end_remote_normal = "";		// 1300	
	
	let center_remote_center_normal_time = "";	// 원격센터 점심시간 param
	
	if (start_remote_normal_hour != "" && start_remote_normal_min != "") {
		start_remote_normal = start_remote_normal_hour + start_remote_normal_min;
	}	
	if (end_remote_normal_hour != "" && end_remote_normal_min != "") {		
		end_remote_normal = end_remote_normal_hour + end_remote_normal_min;
	}
	
	
	if(start_remote_normal == "" && end_remote_normal == ""){
		center_remote_center_normal_time = '';	
	} else {
		center_remote_center_normal_time = start_remote_normal + '/' + end_remote_normal;
	}
	
	//console.log("원격센터 평일시간: " + center_remote_center_normal_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	// 원격센터 점심시간
	let start_remote_lunch_hour = $('#start_remote_lunch_hour').val();
	let start_remote_lunch_min = $('#start_remote_lunch_min').val();	
	let end_remote_lunch_hour = $('#end_remote_lunch_hour').val();
	let end_remote_lunch_min = $('#end_remote_lunch_min').val();
	
	let start_remote_lunch = "";	// 1200
	let end_remote_lunch = "";		// 1300	
	
	let center_remote_center_lunch_time = "";	// 원격센터 점심시간 param
	
	if (start_remote_lunch_hour != "" && start_remote_lunch_min != "") {
		start_remote_lunch = start_remote_lunch_hour + start_remote_lunch_min;
	}	
	if (end_remote_lunch_hour != "" && end_remote_lunch_min != "") {		
		end_remote_lunch = end_remote_lunch_hour + end_remote_lunch_min;
	}
	
	if(start_remote_lunch == "" && end_remote_lunch == ""){
		center_remote_center_lunch_time = '';	
	} else {
		center_remote_center_lunch_time = start_remote_lunch + '/' + end_remote_lunch;
	}
	
	//console.log("원격센터 점심시간: " + center_remote_center_lunch_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#updateForm");
	let data = new FormData(form);
	
	data.append("center_service_center_normal_time", center_service_center_normal_time);	// 고객센터 평일시간
	data.append("center_service_center_lunch_time", center_service_center_lunch_time);		// 고객센터 점심시간
	data.append("center_remote_center_normal_time", center_remote_center_normal_time);		// 원격센터 평일시간
	data.append("center_remote_center_lunch_time", center_remote_center_lunch_time);		// 원격센터 점심시간
	
	
	$.ajax({   			
		url : '/admin/center/update',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("수정되었습니다.");			
			window.location.href = "/admin/center";								
			 
		}, error : function() {
			alert("수정 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
});




