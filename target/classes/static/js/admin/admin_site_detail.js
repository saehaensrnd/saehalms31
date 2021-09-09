// 목록 버튼 클릭
function goList(cri){
	history.back();
}


$(document).on("click", "#insertBtn", function(){
	let site_name = $('#site_name').val().trim();	// 이름
	let site_corporate_name = $('#site_corporate_name').val().trim();		// 법인명
	let site_ceo_phone = $('#site_ceo_phone').val().trim();	// 대표연락처
	
	
	if(site_name == ""){
		alert("이름을 확인하세요.");
		$('#site_name').focus();
		return false;
	} else if(site_corporate_name == ""){
		alert("법인명을 확인하세요.");
		$('#site_corporate_name').focus();
		return false;
	} else if(site_ceo_phone == ""){
		alert("대표연락처를 확인하세요.");
		$('#site_ceo_phone').focus();
		return false;
	}	
	
	
	let teacher_group_no = $('#teacher_group_no').val();
	let center_no = $('#center_no').val();
	
	if (teacher_group_no == "" && center_no == ""){
		alert("강사그룹/티칭센터 둘 다 비어있습니다.");
		return false;
	}
	
	
	
	
	let breakFlag = false;	
	// 사이트 중복이름 확인
	$.ajax({
		url: '/admin/site/duplSiteChk',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"site_name": site_name
		},
		success: function(data) {
			if (data.duplCnt != 0) {
				alert("해당 이름으로 등록되어있는 사이트가 있습니다.");
				breakFlag = true;
				//return;				// ajax 밖에서 return		
			} 
		}, error: function() {
			//console.log("실패");
		} // success
	}); // ajax
	if (breakFlag) { return; }
	
	
	// 고객센터 평일시간
	let start_service_normal_hour = $('#start_service_normal_hour').val();
	let start_service_normal_min = $('#start_service_normal_min').val();	
	let end_service_normal_hour = $('#end_service_normal_hour').val();
	let end_service_normal_min = $('#end_service_normal_min').val();
	
	let start_service_normal = "";	// 0600
	let end_service_normal = "";	// 1800	
	
	let site_service_center_normal_time = "";	// 고객센터 평일시간 param
	
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
		site_service_center_normal_time = '';	
	} else {
		site_service_center_normal_time = start_service_normal + '/' + end_service_normal;
	}
	
	
	//console.log("고객센터 평일시간: " + site_service_center_normal_time);	
	/////////////////////////////////////////////////////////////////////////////////////////	
	
	
	// 고객센터 점심시간
	let start_service_lunch_hour = $('#start_service_lunch_hour').val();
	let start_service_lunch_min = $('#start_service_lunch_min').val();	
	let end_service_lunch_hour = $('#end_service_lunch_hour').val();
	let end_service_lunch_min = $('#end_service_lunch_min').val();
	
	let start_service_lunch = "";	// 1200
	let end_service_lunch = "";	// 1300	
	
	let site_service_center_lunch_time = "";	// 고객센터 점심시간 param
	
	if (start_service_lunch_hour != "" && start_service_lunch_min != "") {
		start_service_lunch = start_service_lunch_hour + start_service_lunch_min;
	}	
	if (end_service_lunch_hour != "" && end_service_lunch_min != "") {		
		end_service_lunch = end_service_lunch_hour + end_service_lunch_min;
	}
	
	if(start_service_lunch == "" && end_service_lunch == ""){
		site_service_center_lunch_time = '';	
	} else {
		site_service_center_lunch_time = start_service_lunch + '/' + end_service_lunch;
	}	
	
	//console.log("고객센터 점심시간: " + site_service_center_lunch_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	// 원격센터 평일시간
	let start_remote_normal_hour = $('#start_remote_normal_hour').val();
	let start_remote_normal_min = $('#start_remote_normal_min').val();	
	let end_remote_normal_hour = $('#end_remote_normal_hour').val();
	let end_remote_normal_min = $('#end_remote_normal_min').val();
	
	let start_remote_normal = "";	// 1200
	let end_remote_normal = "";		// 1300	
	
	let site_remote_center_normal_time = "";	// 원격센터 점심시간 param
	
	if (start_remote_normal_hour != "" && start_remote_normal_min != "") {
		start_remote_normal = start_remote_normal_hour + start_remote_normal_min;
	}	
	if (end_remote_normal_hour != "" && end_remote_normal_min != "") {		
		end_remote_normal = end_remote_normal_hour + end_remote_normal_min;
	}
	
	
	if(start_remote_normal == "" && end_remote_normal == ""){
		site_remote_center_normal_time = '';	
	} else {
		site_remote_center_normal_time = start_remote_normal + '/' + end_remote_normal;
	}	
	
	//console.log("원격센터 평일시간: " + site_remote_center_normal_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	// 원격센터 점심시간
	let start_remote_lunch_hour = $('#start_remote_lunch_hour').val();
	let start_remote_lunch_min = $('#start_remote_lunch_min').val();	
	let end_remote_lunch_hour = $('#end_remote_lunch_hour').val();
	let end_remote_lunch_min = $('#end_remote_lunch_min').val();
	
	let start_remote_lunch = "";	// 1200
	let end_remote_lunch = "";		// 1300	
	
	let site_remote_center_lunch_time = "";	// 원격센터 점심시간 param
	
	if (start_remote_lunch_hour != "" && start_remote_lunch_min != "") {
		start_remote_lunch = start_remote_lunch_hour + start_remote_lunch_min;
	}	
	if (end_remote_lunch_hour != "" && end_remote_lunch_min != "") {		
		end_remote_lunch = end_remote_lunch_hour + end_remote_lunch_min;
	}
	
	if(start_remote_lunch == "" && end_remote_lunch == ""){
		site_remote_center_lunch_time = '';	
	} else {
		site_remote_center_lunch_time = start_remote_lunch + '/' + end_remote_lunch;
	}
	
	
	//console.log("원격센터 점심시간: " + site_remote_center_lunch_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#insertForm");
	let data = new FormData(form);
	
	data.append("site_service_center_normal_time", site_service_center_normal_time);	// 고객센터 평일시간
	data.append("site_service_center_lunch_time", site_service_center_lunch_time);		// 고객센터 점심시간
	data.append("site_remote_center_normal_time", site_remote_center_normal_time);		// 원격센터 평일시간
	data.append("site_remote_center_lunch_time", site_remote_center_lunch_time);		// 원격센터 점심시간
	
	
	
	// 체크박스 처리
	let site_leveltest_status;					// 수강신청시 레벨테스트 정보 필수
	let site_common_product_status;				// 수강신청시 공통상품 사용
	let site_free_study_status;					// 무료수업 사용	
	let site_teacher_select;					// 강사선택 미사용	
	
	if($('#site_leveltest_status_chk').is(':checked') === true){
		site_leveltest_status = '1';		
	} else {
		site_leveltest_status = '0';
	}
	
	if($('#site_common_product_status_chk').is(':checked') === true){
		site_common_product_status = '1';		
	} else {
		site_common_product_status = '0';
	}
	
	if($('#site_free_study_status_chk').is(':checked') === true){
		site_free_study_status = '1';		
	} else {
		site_free_study_status = '0';
	}
	
	if($('#site_teacher_select_chk').is(':checked') === true){
		site_teacher_select = '1';		
	} else {
		site_teacher_select = '0';
	}
	
	
	data.append("site_leveltest_status", site_leveltest_status);			// 수강신청시 레벨테스트 정보 필수
	data.append("site_common_product_status", site_common_product_status);	// 수강신청시 공통상품 사용
	data.append("site_free_study_status", site_free_study_status);			// 무료수업 사용
	data.append("site_teacher_select", site_teacher_select);				// 강사선택 미사용
		
	
	let site_face_type_en, site_face_type_cn;	// 화상 레벨테스트 신청 English / Chinese
	let site_call_type_en, site_call_type_cn;   // 전화 레벨테스트 신청 English / Chinese
	
	if($('#site_face_type_en_chk').is(':checked') === true){
		site_face_type_en = '1';		
	} else {
		site_face_type_en = '0';
	}
	
	if($('#site_face_type_cn_chk').is(':checked') === true){
		site_face_type_cn = '1';		
	} else {
		site_face_type_cn = '0';
	}	
	
	if($('#site_call_type_en_chk').is(':checked') === true){
		site_call_type_en = '1';		
	} else {
		site_call_type_en = '0';
	}
	
	if($('#site_call_type_cn_chk').is(':checked') === true){
		site_call_type_cn = '1';		
	} else {
		site_call_type_cn = '0';
	}	
	
	
	let site_face_type;
	
	if(site_face_type_en == '1' && site_face_type_cn == '1') {
		site_face_type = 'English/Chinese';
	} else if(site_face_type_en == '1' && site_face_type_cn == '0'){
		site_face_type = 'English';
	} else if(site_face_type_en == '0' && site_face_type_cn == '1'){
		site_face_type = 'Chinese';
	} else {
		site_face_type = '';
	}
		
	data.append("site_face_type", site_face_type);	// 화상 레벨테스트 신청 English/Chinese
	
	
	
	let site_call_type;
	
	if(site_call_type_en == '1' && site_call_type_cn == '1') {
		site_call_type = 'English/Chinese';
	} else if(site_call_type_en == '1' && site_call_type_cn == '0'){
		site_call_type = 'English';
	} else if(site_call_type_en == '0' && site_call_type_cn == '1'){
		site_call_type = 'Chinese';
	} else {
		site_call_type = '';
	}
		
	data.append("site_call_type", site_call_type);	// 전화 레벨테스트 신청 English/Chinese
	
	
	$.ajax({   			
		url : '/admin/site/insert',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("등록되었습니다.");			
			window.location.href = "/admin/site";								
			 
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
});


$(document).on("click", ".close", function(){   
	$('.myModal').css('display','none');
	$('body').css("overflow", "scroll");   
});

function closeBtn(){
	$('.myModal').css('display','none');
	$('body').css("overflow", "scroll");
}


// 수정 전 입력 정보 확인 modal......
//$(document).on("click", "#updateModal", function(){

function openModal(flag){
	
	// 고객센터 평일시간
	let start_service_normal_hour = $('#start_service_normal_hour').val();
	let start_service_normal_min = $('#start_service_normal_min').val();	
	let end_service_normal_hour = $('#end_service_normal_hour').val();
	let end_service_normal_min = $('#end_service_normal_min').val();
	
	let start_service_normal = "";	
	let end_service_normal = "";
	
	if (start_service_normal_hour != "" && start_service_normal_min != "") {		
		start_service_normal = start_service_normal_hour + ":" + start_service_normal_min;
	}	
	if (end_service_normal_hour != "" && end_service_normal_min != "") {		
		end_service_normal = end_service_normal_hour + ":" + end_service_normal_min;		 
	}
	
	
	// 고객센터 점심시간
	let start_service_lunch_hour = $('#start_service_lunch_hour').val();
	let start_service_lunch_min = $('#start_service_lunch_min').val();	
	let end_service_lunch_hour = $('#end_service_lunch_hour').val();
	let end_service_lunch_min = $('#end_service_lunch_min').val();
	
	let start_service_lunch = "";	
	let end_service_lunch = "";
	
	if (start_service_lunch_hour != "" && start_service_lunch_min != "") {		
		start_service_lunch = start_service_lunch_hour + ":" + start_service_lunch_min;
	}	
	if (end_service_lunch_hour != "" && end_service_lunch_min != "") {		
		end_service_lunch = end_service_lunch_hour + ":" + end_service_lunch_min;		 
	}
	
	// 원격센터 평일시간
	let start_remote_normal_hour = $('#start_remote_normal_hour').val();
	let start_remote_normal_min = $('#start_remote_normal_min').val();	
	let end_remote_normal_hour = $('#end_remote_normal_hour').val();
	let end_remote_normal_min = $('#end_remote_normal_min').val();
	
	let start_remote_normal = "";	
	let end_remote_normal = "";
	
	if (start_remote_normal_hour != "" && start_remote_normal_min != "") {		
		start_remote_normal = start_remote_normal_hour + ":" + start_remote_normal_min;
	}	
	if (end_remote_normal_hour != "" && end_remote_normal_min != "") {		
		end_remote_normal = end_remote_normal_hour + ":" + end_remote_normal_min;		 
	}
	
	// 원격센터 점심시간
	let start_remote_lunch_hour = $('#start_remote_lunch_hour').val();
	let start_remote_lunch_min = $('#start_remote_lunch_min').val();	
	let end_remote_lunch_hour = $('#end_remote_lunch_hour').val();
	let end_remote_lunch_min = $('#end_remote_lunch_min').val();
	
	let start_remote_lunch = "";	
	let end_remote_lunch = "";
	
	if (start_remote_lunch_hour != "" && start_remote_lunch_min != "") {		
		start_remote_lunch = start_remote_lunch_hour + ":" + start_remote_lunch_min;
	}	
	if (end_remote_lunch_hour != "" && end_remote_lunch_min != "") {		
		end_remote_lunch = end_remote_lunch_hour + ":" + end_remote_lunch_min;		 
	}
	
	
	
	$('#modal_1').html("사이트명 : " + $('#site_name').val().trim());
	$('#modal_2').html("법인명 : " + $('#site_corporate_name').val().trim());
	$('#modal_3').html("대표이사 : " + $('#site_ceo').val().trim());
	$('#modal_4').html("사업자등록번호 : " + $('#site_registration_number').val().trim());
	$('#modal_5').html("통신판매업신고번호 : " + $('#site_telecommunication_number').val().trim());
	$('#modal_6').html("대표연락처 : " + $('#site_ceo_phone').val().trim());
	$('#modal_7').html("대표이메일 : " + $('#site_telecommunication_number').val().trim());
	$('#modal_8').html("대표FAX : " + $('#site_ceo_fax').val().trim());
	$('#modal_9').html("주소 : " + $('#site_address').val().trim());
	$('#modal_10').html("담당자이름 : " + $('#site_manager_name').val().trim());
	
	$('#modal_11').html("담당자연락처 : " + $('#site_manager_phone').val().trim());
	$('#modal_12').html("담당자이메일 : " + $('#site_manager_email').val().trim());
	$('#modal_13').html("사용 언어 : " + $('#site_language').val().trim());
	$('#modal_14').html("고객센터 연락처 : " + $('#site_service_center_phone').val().trim());
	$('#modal_15').html("고객센터 평일시간 : " + start_service_normal+ (start_service_normal == '' ? '' : '~') + end_service_normal);
	$('#modal_16').html("고객센터 점심시간 : " + start_service_lunch+ (start_service_lunch == '' ? '' : '~') + end_service_lunch);
	$('#modal_17').html("고객센터 간단문구 : " + $('#site_service_center_explain').val().trim());
	$('#modal_18').html("원격센터 연락처 : " + $('#site_remote_center_phone').val().trim());
	$('#modal_19').html("원격센터 평일시간 : " + start_remote_normal+ (start_remote_normal == '' ? '' : '~') + end_remote_normal);
	$('#modal_20').html("원격센터 점심시간 : " + start_remote_lunch+ (start_remote_lunch == '' ? '' : '~') + end_remote_lunch);
	
	$('#modal_21').html("원격센터 간단문구 : " + $('#site_remote_center_explain').val().trim());
	$('#modal_22').html("도메인 : " + $('#site_url').val().trim());
	

	//console.log($("input[name = 'site_status']").val());
	
	// 상태
	var siteStatus = document.getElementsByName('site_status');
	var site_status; 	
	for(var i=0; i<siteStatus.length; i++) {
	    if(siteStatus[i].checked) {
	        site_status = siteStatus[i].value;
	    }
	}	
	$('#modal_23').html("상태 : " + (site_status == '1' ? '사용' : '중지'));
	
	$('#modal_24').html("레벨테스트 신청제한 : " + $('#site_leveltest_limit').val().trim() + "회");	
	
	// 수강신청시 레벨테스트 정보 필수
	if($('#site_leveltest_status_chk').is(':checked') === true){
		site_leveltest_status = 'Y';		
	} else {
		site_leveltest_status = 'N';
	}
	$('#modal_50').html("수강신청시 레벨테스트 정보 필수 : " + site_leveltest_status);
	
	
	
	let site_face_type_en, site_face_type_cn;	// 화상 레벨테스트 신청 English / Chinese
	let site_call_type_en, site_call_type_cn;   // 전화 레벨테스트 신청 English / Chinese
	
	// 화상레벨테스트 신청
	if($('#site_face_type_en_chk').is(':checked') === true){
		site_face_type_en = 'English';		
	} else {
		site_face_type_en = '';
	}
	
	if($('#site_face_type_cn_chk').is(':checked') === true){
		site_face_type_cn = 'Chinese';		
	} else {
		site_face_type_cn = '';
	}	
	
	// 전화레벨테스트 신청
	if($('#site_call_type_en_chk').is(':checked') === true){
		site_call_type_en = 'English';		
	} else {
		site_call_type_en = '';
	}
	
	if($('#site_call_type_cn_chk').is(':checked') === true){
		site_call_type_cn = 'Chinese';		
	} else {
		site_call_type_cn = '';
	}	
	
	
	$('#modal_25').html("화상 레벨테스트 신청 : " + site_face_type_en + " " + site_face_type_cn);
	$('#modal_26').html("전화 레벨테스트 신청 : " + site_call_type_en + " " + site_call_type_cn);
	
	
	// 수강신청 방식
	var enrolmentWay = document.getElementsByName('site_enrolment_way');
	var site_enrolment_way; 	
	for(var i=0; i<enrolmentWay.length; i++) {
	    if(enrolmentWay[i].checked) {
	        site_enrolment_way = enrolmentWay[i].value;
	    }
	}
	$('#modal_27').html("수강신청 방식 : " + (site_enrolment_way == '0' ? '기수일정' : '자유'));
	
	
	if($('#site_common_product_status_chk').is(':checked') === true){
		site_common_product_status = 'Y';		
	} else {
		site_common_product_status = 'N';
	}	
	$('#modal_51').html("수강신청시 공통상품 사용 : " + site_common_product_status);
	
	
	// 수강신청 방식
	var startdateWay = document.getElementsByName('site_study_startdate_way');
	var site_study_startdate_way; 	
	for(var i=0; i<startdateWay.length; i++) {
	    if(startdateWay[i].checked) {
	        site_study_startdate_way = startdateWay[i].value;
	    }
	}
	$('#modal_28').html("수강시작일 방식 : " + (site_study_startdate_way == '0' ? '기수일정' : '자유'));
	
	$('#modal_29').html("온라인입금1 : " + $('#site_bank_name1').val().trim() + " | " + $('#site_bank_number1').val().trim() + " | " + $('#site_bank_owner1').val().trim());
	$('#modal_30').html("온라인입금2 : " + $('#site_bank_name2').val().trim() + " | " + $('#site_bank_number2').val().trim() + " | " + $('#site_bank_owner2').val().trim());
	
	$('#modal_31').html("택배사 지정 : " + $('#site_parcel').val().trim());
	$('#modal_32').html("수강신청 문구 : " + $('#site_enrolment_text').val());
	
	
	if($('#site_free_study_status_chk').is(':checked') === true){
		site_free_study_status = 'Y';		
	} else {
		site_free_study_status = 'N';
	}	
	$('#modal_33').html("무료수업 사용 : " + site_free_study_status);
	
	
	if($('#site_teacher_select_chk').is(':checked') === true){
		site_teacher_select = 'Y';		
	} else {
		site_teacher_select = 'N';
	}	
	$('#modal_34').html("강사선택 미사용 : " + site_teacher_select);
	
	let groupSelect = document.getElementById("teacher_group_no")
	let groupName = groupSelect.options[groupSelect.selectedIndex].text;
	
	let centerSelect = document.getElementById("center_no")
	let centerName = centerSelect.options[centerSelect.selectedIndex].text;
	$('#modal_35').html("강사그룹, 티칭센터 : " + (groupName == '-- 선택 --' ? '' : groupName) + ' / ' + (centerName == '-- 선택 --' ? '' : centerName));
	
	
	// 수강권 사용여부
	var ticketStatus = document.getElementsByName('site_study_ticket_status');
	var site_study_ticket_status; 	
	for(var i=0; i<ticketStatus.length; i++) {
	    if(ticketStatus[i].checked) {
	        site_study_ticket_status = ticketStatus[i].value;
	    }
	}	
	$('#modal_36').html("수강권 사용여부 : " + (site_study_ticket_status == '0' ? '사용안함' : '사용'));
	
	// 사용자 E-book보기
	var ebookStatus = document.getElementsByName('site_ebook_status');
	var site_ebook_status; 	
	for(var i=0; i<ebookStatus.length; i++) {
	    if(ebookStatus[i].checked) {
	        site_ebook_status = ebookStatus[i].value;
	    }
	}	
	$('#modal_37').html("사용자 E-book 보기 : " + (site_ebook_status == '1' ? 'Y' : 'N'));

	
	
	// flag에 따른 저장/수정 버튼
	let a = '';
	if(flag == 'update'){	
		a = '<button class="btn add" type="button" id="updateBtn">저장</button>';		
		$('#modalBtnDiv').html(a);		
	} else {
		a = '<button class="btn add" type="button" id="insertBtn">저장</button>';		
		$('#modalBtnDiv').html(a);
	}

	$('.myModal').css('display','block');
	$('body').css("overflow", "hidden");
}	
	
	




$(document).on("click", "#updateBtn", function(){
	let site_name = $('#site_name').val().trim();	// 이름
	let site_corporate_name = $('#site_corporate_name').val().trim();		// 법인명
	let site_ceo_phone = $('#site_ceo_phone').val().trim();	// 대표연락처
	
	let old_site_name = $('#old_site_name').val().trim();	// 수정 전 이름 (사이트명 변경했는지 확인용.. 변경했다면 중복검사)
	
	if(site_name == ""){
		alert("이름을 확인하세요.");
		$('#site_name').focus();
		return false;
	} else if(site_corporate_name == ""){
		alert("법인명을 확인하세요.");
		$('#site_corporate_name').focus();
		return false;
	} else if(site_ceo_phone == ""){
		alert("대표연락처를 확인하세요.");
		$('#site_ceo_phone').focus();
		return false;
	}	
	
	let teacher_group_no = $('#teacher_group_no').val();
	let center_no = $('#center_no').val();
	
	if (teacher_group_no == "" && center_no == ""){
		alert("강사그룹/티칭센터 둘 다 비어있습니다.");
		return false;
	}
	
	// 사이트명 수정했다면 중복검사
	if (old_site_name != site_name){
		let breakFlag = false;	
		// 사이트 중복이름 확인
		$.ajax({
			url: '/admin/site/duplSiteChk',
			type: 'post',
			async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
			data: {
				"site_name": site_name
			},
			success: function(data) {
				if (data.duplCnt != 0) {
					alert("해당 이름으로 등록되어있는 사이트가 있습니다.");
					breakFlag = true;
					//return;				// ajax 밖에서 return		
				} 
			}, error: function() {
				//console.log("실패");
			} // success
		}); // ajax
		if (breakFlag) { return; }
	}
	
	
	
	
	// 고객센터 평일시간
	let start_service_normal_hour = $('#start_service_normal_hour').val();
	let start_service_normal_min = $('#start_service_normal_min').val();	
	let end_service_normal_hour = $('#end_service_normal_hour').val();
	let end_service_normal_min = $('#end_service_normal_min').val();
	
	let start_service_normal = "";	// 0600
	let end_service_normal = "";	// 1800
	
	let start_service_normal2 = "";
	let end_service_normal2 = "";	
	
	let site_service_center_normal_time = "";	// 고객센터 평일시간 param
	
	if (start_service_normal_hour == "" || start_service_normal_min == "") {
		start_service_normal = "";
	} else {
		start_service_normal = start_service_normal_hour + start_service_normal_min;
		start_service_normal2 = start_service_normal_hour + ":" + start_service_normal_min;
	}
	
	if (end_service_normal_hour == "" || end_service_normal_min == "") {
		end_service_normal = "";
	} else {
		end_service_normal = end_service_normal_hour + end_service_normal_min;
		end_service_normal2 = end_service_normal_hour + ":" + end_service_normal_min;
	}
	
	if(start_service_normal == "" && end_service_normal == ""){
		site_service_center_normal_time = '';	
	} else {
		site_service_center_normal_time = start_service_normal + '/' + end_service_normal;
	}
	
	
	//console.log("고객센터 평일시간: " + site_service_center_normal_time);	
	/////////////////////////////////////////////////////////////////////////////////////////	
	
	
	// 고객센터 점심시간
	let start_service_lunch_hour = $('#start_service_lunch_hour').val();
	let start_service_lunch_min = $('#start_service_lunch_min').val();	
	let end_service_lunch_hour = $('#end_service_lunch_hour').val();
	let end_service_lunch_min = $('#end_service_lunch_min').val();
	
	let start_service_lunch = "";	// 1200
	let end_service_lunch = "";	// 1300	
	
	let start_service_lunch2 = "";	
	let end_service_lunch2 = "";
	
	let site_service_center_lunch_time = "";	// 고객센터 점심시간 param
	
	if (start_service_lunch_hour != "" && start_service_lunch_min != "") {
		start_service_lunch = start_service_lunch_hour + start_service_lunch_min;
		start_service_lunch2 = start_service_lunch_hour + ":" + start_service_lunch_min;
	}	
	if (end_service_lunch_hour != "" && end_service_lunch_min != "") {		
		end_service_lunch = end_service_lunch_hour + end_service_lunch_min;
		end_service_lunch2 = end_service_lunch_hour + ":" + end_service_lunch_min;
	}
	
	if(start_service_lunch == "" && end_service_lunch == ""){
		site_service_center_lunch_time = '';	
	} else {
		site_service_center_lunch_time = start_service_lunch + '/' + end_service_lunch;
	}
	
	
	//console.log("고객센터 점심시간: " + site_service_center_lunch_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	// 원격센터 평일시간
	let start_remote_normal_hour = $('#start_remote_normal_hour').val();
	let start_remote_normal_min = $('#start_remote_normal_min').val();	
	let end_remote_normal_hour = $('#end_remote_normal_hour').val();
	let end_remote_normal_min = $('#end_remote_normal_min').val();
	
	let start_remote_normal = "";	// 1200
	let end_remote_normal = "";		// 1300	
	
	let start_remote_normal2 = "";	
	let end_remote_normal2 = "";
	
	let site_remote_center_normal_time = "";	// 원격센터 점심시간 param
	
	if (start_remote_normal_hour != "" && start_remote_normal_min != "") {
		start_remote_normal = start_remote_normal_hour + start_remote_normal_min;
		start_remote_normal2 = start_remote_normal_hour + ":" + start_remote_normal_min;
	}	
	if (end_remote_normal_hour != "" && end_remote_normal_min != "") {		
		end_remote_normal = end_remote_normal_hour + end_remote_normal_min;
		end_remote_normal2 = end_remote_normal_hour + ":" + end_remote_normal_min;
	}
	
	if(start_remote_normal == "" && end_remote_normal == ""){
		site_remote_center_normal_time = '';	
	} else {
		site_remote_center_normal_time = start_remote_normal + '/' + end_remote_normal;
	}
	
	
	//console.log("원격센터 평일시간: " + site_remote_center_normal_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	// 원격센터 점심시간
	let start_remote_lunch_hour = $('#start_remote_lunch_hour').val();
	let start_remote_lunch_min = $('#start_remote_lunch_min').val();	
	let end_remote_lunch_hour = $('#end_remote_lunch_hour').val();
	let end_remote_lunch_min = $('#end_remote_lunch_min').val();
	
	let start_remote_lunch = "";	// 1200
	let end_remote_lunch = "";		// 1300	
	
	let start_remote_lunch2 = "";
	let end_remote_lunch2 = "";	
	
	let site_remote_center_lunch_time = "";	// 원격센터 점심시간 param
	
	if (start_remote_lunch_hour != "" && start_remote_lunch_min != "") {
		start_remote_lunch = start_remote_lunch_hour + start_remote_lunch_min;
		start_remote_lunch2 = start_remote_lunch_hour + ":" + start_remote_lunch_min;
	}	
	if (end_remote_lunch_hour != "" && end_remote_lunch_min != "") {		
		end_remote_lunch = end_remote_lunch_hour + end_remote_lunch_min;
		end_remote_lunch2 = end_remote_lunch_hour + ":" + end_remote_lunch_min;
	}
	
	
	if(start_remote_lunch == "" && end_remote_lunch == ""){
		site_remote_center_lunch_time = '';	
	} else {
		site_remote_center_lunch_time = start_remote_lunch + '/' + end_remote_lunch;
	}	
	
	//console.log("원격센터 점심시간: " + site_remote_center_lunch_time);	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#updateForm");
	let data = new FormData(form);
	
	data.append("site_service_center_normal_time", site_service_center_normal_time);	// 고객센터 평일시간
	data.append("site_service_center_lunch_time", site_service_center_lunch_time);		// 고객센터 점심시간
	data.append("site_remote_center_normal_time", site_remote_center_normal_time);		// 원격센터 평일시간
	data.append("site_remote_center_lunch_time", site_remote_center_lunch_time);		// 원격센터 점심시간
	
	
	// 체크박스 처리
	let site_leveltest_status;					// 수강신청시 레벨테스트 정보 필수
	let site_common_product_status;				// 수강신청시 공통상품 사용
	let site_free_study_status;					// 무료수업 사용	
	let site_teacher_select;					// 강사선택 미사용	
	
	if($('#site_leveltest_status_chk').is(':checked') === true){
		site_leveltest_status = '1';		
	} else {
		site_leveltest_status = '0';
	}
	
	if($('#site_common_product_status_chk').is(':checked') === true){
		site_common_product_status = '1';		
	} else {
		site_common_product_status = '0';
	}
	
	if($('#site_free_study_status_chk').is(':checked') === true){
		site_free_study_status = '1';		
	} else {
		site_free_study_status = '0';
	}
	
	if($('#site_teacher_select_chk').is(':checked') === true){
		site_teacher_select = '1';		
	} else {
		site_teacher_select = '0';
	}
	
	
	data.append("site_leveltest_status", site_leveltest_status);			// 수강신청시 레벨테스트 정보 필수
	data.append("site_common_product_status", site_common_product_status);	// 수강신청시 공통상품 사용
	data.append("site_free_study_status", site_free_study_status);			// 무료수업 사용
	data.append("site_teacher_select", site_teacher_select);				// 강사선택 미사용
		
	
	let site_face_type_en, site_face_type_cn;	// 화상 레벨테스트 신청 English / Chinese
	let site_call_type_en, site_call_type_cn;   // 전화 레벨테스트 신청 English / Chinese
	
	if($('#site_face_type_en_chk').is(':checked') === true){
		site_face_type_en = '1';		
	} else {
		site_face_type_en = '0';
	}
	
	if($('#site_face_type_cn_chk').is(':checked') === true){
		site_face_type_cn = '1';		
	} else {
		site_face_type_cn = '0';
	}	
	
	if($('#site_call_type_en_chk').is(':checked') === true){
		site_call_type_en = '1';		
	} else {
		site_call_type_en = '0';
	}
	
	if($('#site_call_type_cn_chk').is(':checked') === true){
		site_call_type_cn = '1';		
	} else {
		site_call_type_cn = '0';
	}	
	
	
	let site_face_type;
	
	if(site_face_type_en == '1' && site_face_type_cn == '1') {
		site_face_type = 'English/Chinese';
	} else if(site_face_type_en == '1' && site_face_type_cn == '0'){
		site_face_type = 'English';
	} else if(site_face_type_en == '0' && site_face_type_cn == '1'){
		site_face_type = 'Chinese';
	} else {
		site_face_type = '';
	}
		
	data.append("site_face_type", site_face_type);	// 화상 레벨테스트 신청 English/Chinese
	
	
	
	let site_call_type;
	
	if(site_call_type_en == '1' && site_call_type_cn == '1') {
		site_call_type = 'English/Chinese';
	} else if(site_call_type_en == '1' && site_call_type_cn == '0'){
		site_call_type = 'English';
	} else if(site_call_type_en == '0' && site_call_type_cn == '1'){
		site_call_type = 'Chinese';
	} else {
		site_call_type = '';
	}
		
	data.append("site_call_type", site_call_type);	// 전화 레벨테스트 신청 English/Chinese
	
	
	
	// 상단로고 이미지 삭제 체크박스
	let site_top_logo_del = "0";
	if($('#site_top_logo_del_chk').is(':checked') === true){
		site_top_logo_del = '1';		
	} else {
		site_top_logo_del = '0';
	}	
	
	data.append("site_top_logo_del", site_top_logo_del);
	
	// 상단로고 이미지 삭제 체크박스
	let site_bottom_logo_del = "0";
	if($('#site_bottom_logo_del_chk').is(':checked') === true){
		site_bottom_logo_del = '1';		
	} else {
		site_bottom_logo_del = '0';
	}	
	
	data.append("site_bottom_logo_del", site_bottom_logo_del)
	
	/*
	let checkMsg = "수정하시겠습니까?";
	checkMsg += "\n\n사이트명 : " + site_name;
	checkMsg += "\n법인명 : " + site_corporate_name;
	checkMsg += "\n대표이사 : " + $('#site_ceo').val().trim();
	checkMsg += "\n사업자등록번호 : " + $('#site_registration_number').val().trim();
	checkMsg += "\n통신판매업신고번호 : " + $('#site_telecommunication_number').val().trim();
	checkMsg += "\n대표연락처 : " + site_ceo_phone;
	checkMsg += "\n대표이메일 : " + $('#site_telecommunication_number').val().trim();
	checkMsg += "\n대표FAX : " + $('#site_ceo_fax').val().trim();
	checkMsg += "\n주소 : " + $('#site_address').val().trim();
	
	checkMsg += "\n담당자이름 : " + $('#site_manager_name').val().trim();
	checkMsg += "\n담당자연락처 : " + $('#site_manager_phone').val().trim();
	checkMsg += "\n담당자이메일 : " + $('#site_manager_email').val().trim();
	checkMsg += "\n사용 언어 : " + $('#site_language').val().trim();
	checkMsg += "\n고객센터 연락처 : " + $('#site_service_center_phone').val().trim();
	checkMsg += "\n고객센터 평일시간 : " + start_service_normal2 + "~" + end_service_normal2;
	checkMsg += "\n고객센터 점심시간 : " + start_service_lunch2 + "~" + end_service_lunch2;
	checkMsg += "\n고객센터 간단문구 : " + $('#site_service_center_explain').val().trim();	
	
	checkMsg += "\n원격센터 연락처 : " + $('#site_remote_center_phone').val().trim();
	checkMsg += "\n원격센터 평일시간 : " + start_remote_normal2 + "~" + end_remote_normal2;
	checkMsg += "\n원격센터 점심시간 : " + start_remote_lunch2 + "~" + end_remote_lunch2;
	checkMsg += "\n원격센터 간단문구 : " + $('#site_remote_center_explain').val().trim();
	
	checkMsg += "\n도메인 : " + $('#site_url').val().trim();	
	checkMsg += "\n주소 : " + $('#site_address').val().trim();
	checkMsg += "\n주소 : " + $('#site_address').val().trim();
	checkMsg += "\n주소 : " + $('#site_address').val().trim();
	checkMsg += "\n주소 : " + $('#site_address').val().trim();
	checkMsg += "\n주소 : " + $('#site_address').val().trim();
	*/
	
	$.ajax({   			
		url : '/admin/site/update',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("수정되었습니다.");			
			window.location.href = "/admin/site";								
			 
		}, error : function() {
			alert("수정 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
	
	
	//if(confirm(checkMsg)){
	
	//}
	
	
	
});





