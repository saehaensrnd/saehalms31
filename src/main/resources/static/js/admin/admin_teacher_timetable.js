function search(){
	let searchCenter = $('#searchCenter').val();
	let searchTeacher = $('#searchTeacher').val();
	
	if(searchTeacher == ""){
		alert("강사를 선택하세요.");
		$('#searchTeacher').focus();
		return false;
	}
	
	let params = "?searchCenter="+searchCenter+"&searchTeacher="+searchTeacher;
	window.location.href = "/admin/teacher/timetable"+params;	
}


// 티칭센터 셀렉트박스 change 이벤트 발생 시 - 강사 콤보박스 셀렉트박스 Setting (전화 or 화상 담당 강사)
function centerChange(e) {	
	let center = e.value;

	// 강사 셀렉트박스 초기화
	let teacherSelect = document.getElementById("searchTeacher");
	teacherSelect.options.length = 0;
	/*
	for (i = 0; i < teacherSelect.options.length; i++) {
		teacherSelect.options[i] = null;
	}
	*/
	
	// 강사 셀렉트박스 세팅
	let teacherArr;

	$.ajax({
		url: '/admin/teacher/getTeacherComboByCenterNo',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"searchCenter": center
		},
		success: function(item) {
			teacherArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});

	teacherSelect.options[0] = new Option("-- 선택 --", "");

	if (teacherArr.length > 0) {
		for (i = 1; i <= teacherArr.length; i++) {
			teacherSelect.options[i] = new Option(teacherArr[i - 1].teacher_name, teacherArr[i - 1].user_teacher_no);		// text, value
		}
	}
	
	jcf.replaceAll();

}




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



function holidayDeleteBtn(teacher_holiday_no){
	//console.log(teacher_holiday_no);
	
	$.ajax({   			
		url : '/admin/teacher/deleteHoliday',			
		type : 'post',
		data : {'teacher_holiday_no' : teacher_holiday_no},		
		success : function() {			
			location.reload();			
			 
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax	
}

function holidayUpdate(teacher){		
	let teacher_holiday_date = $('#teacher_holiday_date').val().trim();
	
	if(teacher_holiday_date == ""){
		alert("휴일을 선택하세요.");
		$('#teacher_holiday_date').focus();
		return false;
	}
	
	$.ajax({   			
		url : '/admin/teacher/insertHoliday',			
		type : 'post',
		data : {'user_teacher_no' : teacher.user_teacher_no
		      , 'teacher_holiday_date': teacher_holiday_date},		
		success : function(data) {	
			if(data.result == 1){
				location.reload();	
			} else {
				alert(data.resultMsg);
			}
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax	
	
}






