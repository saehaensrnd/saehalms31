// 수정상태로 들어올 때 콤보 Setting
if(flag == 'update'){
	// 보강신청일 셀렉트박스 초기화
	//let supplementDaySelect = document.getElementById("supplement_date");
	//supplementDaySelect.options.length = 0;
	//supplementDaySelect.options[0] = new Option("보강신청일 선택", "");
		
	// 보강신청일 셀렉트박스 세팅
	//let supplementDayArr;
}





// 수업 셀렉트박스 change 이벤트 발생 시
function classChange(e) {	
	let classNo = e.value;
	
	// 선택된 수업의 수업종료일
	// let classSelect = document.getElementById("class_no"); 
	// classSelect.options[classSelect.selectedIndex].title;

	// 휴강신청일 셀렉트박스 초기화
	let holidaySelect = document.getElementById("holiday_date");
	holidaySelect.options.length = 0;
	
	// 보강신청일 셀렉트박스 초기화
	let supplementDaySelect = document.getElementById("supplement_date");
	supplementDaySelect.options.length = 0;		
	supplementDaySelect.options[0] = new Option("보강신청일 선택", "");
	
	// 휴강신청일 셀렉트박스 세팅
	let holidayArr;

	$.ajax({
		url: '/student/postpone/getHolidayComboByClass',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"class_no": classNo
		},
		success: function(item) {
			holidayArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});

	holidaySelect.options[0] = new Option("휴강신청일 선택", "");

	if (holidayArr.length > 0) {
		for (i = 1; i <= holidayArr.length; i++) {
			holidaySelect.options[i] = new Option(holidayArr[i - 1].study_date, holidayArr[i - 1].study_date);		// text, value
		}
	} else {
		alert("휴강 신청하실 수 있는 날짜가 없습니다.");
	}
	
	jcf.replaceAll();

}

// 특정 기간에 해당하는 날짜 모두 가져오기 - yyyy-MM-dd
function getDateRangeData(param1, param2) {  //param1은 시작일, param2는 종료일이다.
	var res_day = [];
	var ss_day = new Date(param1);
	var ee_day = new Date(param2);

	while (ss_day.getTime() <= ee_day.getTime()) {
		var _mon_ = (ss_day.getMonth() + 1);
		_mon_ = _mon_ < 10 ? '0' + _mon_ : _mon_;
		var _day_ = ss_day.getDate();
		_day_ = _day_ < 10 ? '0' + _day_ : _day_;
		res_day.push(ss_day.getFullYear() + '-' + _mon_ + '-' + _day_);
		ss_day.setDate(ss_day.getDate() + 1);
	}
	return res_day;
}


// 휴강신청일 셀렉트박스 change 이벤트 발생 시 - 보강신청일 셀렉트박스 Setting
function holidayChange(e) {		
	let holiday = e.value;	
	
	let supplementDaySelect = document.getElementById("supplement_date");
	
	// 보강신청일 셀렉트박스 초기화	
	supplementDaySelect.options.length = 0;	
	supplementDaySelect.options[0] = new Option("보강신청일 선택", "");
	
	// 휴강신청일 셀렉트박스나 요청수업 선택이 제대로 되어있지 않을 시 초기화
	if(e.value == '' || $('#class_no').val() == ''){
		supplementDaySelect.options.length = 0;	
		supplementDaySelect.options[0] = new Option("보강신청일 선택", "");
		return false;
	}	
	
	// 보강신청일 셀렉트박스 세팅
	let supplementDayArr;
	
	let search_startDate = holiday;
	// 선택된 수업의 수업종료일
	let classSelect = document.getElementById("class_no"); 
	let search_endDate = classSelect.options[classSelect.selectedIndex].title;

	//console.log("search_startDate : " + search_startDate);
	//console.log("search_endDate : " + search_endDate);

	//let startDt = new Date('2021-07-29');
	
	let startDt = new Date(search_startDate);	
	startDt.setDate(startDt.getDate() + 1);			// 선택한 휴강신청일보다 하루 뒤 부터 선택가능
	let endDt = new Date(search_endDate);			// 수업종료일
	
	
	var diffMs = endDt.getTime() - startDt.getTime() ;
    var diffDay = diffMs / (1000*60*60*24) ;
	
	//console.log("★★★★★★★★★★★★★★★★★");
	//console.log(diffDay);
	
	
	
	// 휴강신청일 다음날 ~ 수업종료일 차이가 0보다 작다면 초기화   // 0이면 보강신청가능일과 수업종료일이 같은 경우
	if(diffDay < 0){ 		
		supplementDaySelect.options.length = 0;		
		supplementDaySelect.options[0] = new Option("보강신청일 선택", "");
		alert("보강 신청하실 수 있는 날짜가 없습니다.");		
		return false;
	}
	
	
	
	supplementDayArr = getDateRangeData(startDt, endDt)
	
	//console.log(supplementDayArr);
	//console.log(supplementDayArr.length);
	
	let isDayCnt = 0;
	let compare_dt;
	let containsDay = [1,2,3,4,5];	// 평일
	let removeArr = new Array();
	
	// 주말 제거
	for (let i = 0; i < supplementDayArr.length; i++) {
		// 요일 비교해서 해당 요일과 다르면 remove push	(평일이 아니라면)
		compare_dt = new Date(supplementDayArr[i]);

		for (let k = 0; k < containsDay.length; k++) {
			if (compare_dt.getDay() == containsDay[k]) {
				isDayCnt++;
			}
		}

		// 주말이라면
		if (isDayCnt == 0) {
			removeArr.push(i);
		}

		// 초기화
		isDayCnt = 0;
	}
	
	// 삭제할 요소들 뒤에서 부터 제거
	for (let i = supplementDayArr.length - 1; i >= 0; i--) {
		for (let k = 0; k < removeArr.length; k++) {
			if (i == removeArr[k]) {
				supplementDayArr.splice(i, 1);
			}
		}
	}
	
	//console.log("최종");
	//console.log(supplementDayArr);
		
	
	if(supplementDayArr.length > 0){
		supplementDaySelect.options[0] = new Option("보강신청일 선택", "");		
		
		for (i = 1; i <= supplementDayArr.length; i++) {
			supplementDaySelect.options[i] = new Option(supplementDayArr[i - 1], supplementDayArr[i - 1]);		// text, value
		}
	}
	
	jcf.replaceAll();
}



function insertBtn(){
	let postpone_phone = $('#postpone_phone').val().trim();
	let class_no = $('#class_no').val().trim();
	let holiday_date = $('#holiday_date').val().trim();
	let supplement_date = $('#supplement_date').val().trim();
	let postpone_content = $('#postpone_content').val().trim();
	
	
	if(postpone_phone == ""){
		alert("연락처를 확인하세요.");
		$('#postpone_phone').focus();
		return false;
	} else if(class_no == ""){
		alert("요청수업을 확인하세요.");
		$('#class_no').focus();
		return false;
	} else if(holiday_date == ""){
		alert("휴강신청일을 확인하세요.");
		$('#holiday_date').focus();
		return false;
	} else if(supplement_date == ""){
		alert("보강신청일을 확인하세요.");
		$('#supplement_date').focus();
		return false;
	} else if(postpone_content == ""){
		alert("사유를 확인하세요.");
		$('#postpone_content').focus();
		return false;
	}
	
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#insertForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/student/postpone/insert',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("등록되었습니다.");			
			window.location.href = "/student/postpone";								
			 
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
}






// "UPDATE" 휴강신청일 셀렉트박스 change 이벤트 발생 시 - 보강신청일 셀렉트박스 Setting
function holidayChangeByUpdate(e) {		
	let holiday = e.value;	
	
	// 휴강신청일 변경 선택 시 휴강신청일 오늘기준 날짜로 새로 Setting
	if(holiday == 'change'){
		// 휴강신청일 셀렉트박스 세팅
		let holidaySelect = document.getElementById("holiday_date");
				
		
		let holidayArr;
		
		$.ajax({
			url: '/student/postpone/getHolidayComboByClass',
			type: 'post',
			async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
			data: {
				"class_no": postpone.class_no
			},
			success: function(item) {
				holidayArr = item;
			}, error: function() {
				//console.log("실패");
			} 			
		});
		;

		if (holidayArr.length > 0) {
			holidaySelect.options.length = 0;
			holidaySelect.options[0] = new Option("휴강신청일 선택", "")
		
			for (i = 1; i <= holidayArr.length; i++) {
				holidaySelect.options[i] = new Option(holidayArr[i - 1].study_date, holidayArr[i - 1].study_date);		// text, value
			}
			
			holidaySelect.options[0].selected = true;
		} else {
			alert("휴강 신청하실 수 있는 날짜가 없습니다.");
		}		
		
		jcf.replaceAll();		
		return false;
	}
	
		
	//////////////////////////////////////////////////////////////////////////	
	let supplementDaySelect = document.getElementById("supplement_date");
	
	// 보강신청일 셀렉트박스 초기화	
	supplementDaySelect.options.length = 0;	
	supplementDaySelect.options[0] = new Option("보강신청일 선택", "");
	
	// 휴강신청일 선택 눌렀을땐 보강신청일 초기화만
	if(e.value == ''){
		supplementDaySelect.options.length = 0;	
		supplementDaySelect.options[0] = new Option("보강신청일 선택", "");
		return false;
	}	
	
	// 보강신청일 셀렉트박스 세팅
	let supplementDayArr;
	
	let search_startDate = holiday;
	// 수업의 수업종료일
	let search_endDate = postpone.end_date;

	//console.log("search_startDate : " + search_startDate);
	//console.log("search_endDate : " + search_endDate);

	//let startDt = new Date('2021-07-29');
	
	let startDt = new Date(search_startDate);	
	startDt.setDate(startDt.getDate() + 1);			// 선택한 휴강신청일보다 하루 뒤 부터 선택가능
	let endDt = new Date(search_endDate);			// 수업종료일
	
	
	var diffMs = endDt.getTime() - startDt.getTime() ;
    var diffDay = diffMs / (1000*60*60*24) ;
	
	// 휴강신청일 다음날 ~ 수업종료일 차이가 0보다 작다면 초기화   // 0이면 보강신청가능일과 수업종료일이 같은 경우
	if(diffDay < 0){ 		
		supplementDaySelect.options.length = 0;		
		supplementDaySelect.options[0] = new Option("보강신청일 선택", "");
		alert("보강 신청하실 수 있는 날짜가 없습니다.");		
		return false;
	}
	
	supplementDayArr = getDateRangeData(startDt, endDt)

	
	let isDayCnt = 0;
	let compare_dt;
	let containsDay = [1,2,3,4,5];	// 평일
	let removeArr = new Array();
	
	// 주말 제거
	for (let i = 0; i < supplementDayArr.length; i++) {
		// 요일 비교해서 해당 요일과 다르면 remove push	(평일이 아니라면)
		compare_dt = new Date(supplementDayArr[i]);

		for (let k = 0; k < containsDay.length; k++) {
			if (compare_dt.getDay() == containsDay[k]) {
				isDayCnt++;
			}
		}

		// 주말이라면
		if (isDayCnt == 0) {
			removeArr.push(i);
		}

		// 초기화
		isDayCnt = 0;
	}
	
	// 삭제할 요소들 뒤에서 부터 제거
	for (let i = supplementDayArr.length - 1; i >= 0; i--) {
		for (let k = 0; k < removeArr.length; k++) {
			if (i == removeArr[k]) {
				supplementDayArr.splice(i, 1);
			}
		}
	}	
	//console.log("최종");
	//console.log(supplementDayArr);
			
	if(supplementDayArr.length > 0){
		supplementDaySelect.options[0] = new Option("보강신청일 선택", "");		
		
		for (i = 1; i <= supplementDayArr.length; i++) {
			supplementDaySelect.options[i] = new Option(supplementDayArr[i - 1], supplementDayArr[i - 1]);		// text, value
		}
	}
	
	jcf.replaceAll();
}



function updateBtn(){
	let postpone_phone = $('#postpone_phone').val().trim();	
	let holiday_date = $('#holiday_date').val().trim();
	let supplement_date = $('#supplement_date').val().trim();
	let postpone_content = $('#postpone_content').val().trim();
	
	
	if(postpone_phone == ""){
		alert("연락처를 확인하세요.");
		$('#postpone_phone').focus();
		return false;
	} else if(holiday_date == ""){
		alert("휴강신청일을 확인하세요.");
		$('#holiday_date').focus();
		return false;
	} else if(supplement_date == ""){
		alert("보강신청일을 확인하세요.");
		$('#supplement_date').focus();
		return false;
	} else if(postpone_content == ""){
		alert("사유를 확인하세요.");
		$('#postpone_content').focus();
		return false;
	}
	
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#updateForm");
	let data = new FormData(form);
	
	$.ajax({   			
		url : '/student/postpone/update',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("수정되었습니다.");			
			window.location.href = "/student/postpone";								
			 
		}, error : function() {
			alert("수정 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
}


