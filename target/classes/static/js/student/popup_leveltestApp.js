function addZero(number){
	return (number < 10)? "0"+number:number;
}

function addTimeCollon(time){
	return [time.slice(0,2), ":", time.slice(2)].join("");
}

function selfClose(){
	self.close();
}


function languageChange(e){
	$.ajax({
		url: '/student/levelTest/leveltestApp/getTeacherCombo',
		type: 'post',
		data: {
			'language_type': e.value,
		},
		success : function(data) {
			var teacherSel = document.querySelector("#user_teacher_no");
			teacherSel.options.length = 0;

			//console.log(data.length);
			teacherSel.options[0] = new Option("--선택--", "");
			if(data.length > 0){
				for(var i=1; i<=data.length; i++){
					console.log(data[i-1].teacher_name);
					teacherSel.options[i] = new Option(data[i-1].teacher_name, data[i-1].user_teacher_no);
				}
			}
			
			jcf.replaceAll();
		},
		error : function() {
			alert("서버 에러");
		}
	});
}



function getFormatDate(date) {
	var year = date.getFullYear();              //yyyy
	var month = (1 + date.getMonth());          //M
	month = month >= 10 ? month : '0' + month;  //month 두자리로 저장
	var day = date.getDate();                   //d
	day = day >= 10 ? day : '0' + day;          //day 두자리로 저장
	return year + '-' + month + '-' + day;       //'-' 추가하여 yyyy-mm-dd 형태 생성 가능
}


// 레벨테스트일(검색일) - 신청일(금일) + 1 이상만 허용
/*
function searchDateChange(e){
	let selectedDay = new Date(e.value);	
	
	let today = new Date();
	let formatToday = getFormatDate(today);		// yyyy-MM-dd
	
	let tommorow = new Date(formatToday);	
	tommorow.setDate(tommorow.getDate() + 1);
	
	// 선택한 날짜가 당일 또는 이전 날짜라면 alert 후 날짜 초기화
	if(selectedDay < tommorow){
		alert("레벨테스트일은 익일 이후의 날짜만 선택 가능합니다.");
		$('#search_date').val('');
	}
}
*/



function startTime(start){
	console.log("d?");
	var s = Number($(start).val());
	if(s >= 21){
		$("#end_time").val(23).prop("selected", true);
	}
	else{
		$("#end_time").val(s+2).prop("selected", true);
	}
	
	jcf.replaceAll();
}

function endTime(end){
	var e = Number($(end).val());
	if(e <= 2){
		$("#start_time").val(0).prop("selected", true);
	}
	else{
		$("#start_time").val(e-2).prop("selected", true);
	}
	
	jcf.replaceAll();
}


function searchBtn(){
	
	if($("#leveltest_language").val() == '--선택--'){
		alert("레벨테스트 언어를 선택해주세요.");
		return false;
	}
	if($("#time").val() == '--선택--'){
		alert("레벨테스트 시간을 선택해주세요.");
		return false;
	}
	
	
	
	let start_date = $("#start_date").val();
	
	
	let startDate = new Date(start_date);
	let getDay = startDate.getDay();
	
	// 해당 검색일(오늘) 금요일 또는 토요일이라면 다음주 검색되도록 검색일 변경 	
	if(getDay == 5 || getDay == 6){		
		startDate.setDate(new Date(startDate).getDate() + 3);
		start_date = getFormatDate(startDate);
	}
	
	var formData = new FormData(document.querySelector('#leveltestForm'));
	formData.set("start_date", start_date);
	
	$.ajax({
		url : '/popup/leveltestApp/search',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(result) {
			var cal_sel = document.querySelector("#cal_sel");
			cal_sel.innerHTML = "<button type='button' class='prevSearch' onclick='prevSearch(\""+result.weeks[0]+"\")'>&lt;</button> "+result.weeks[0]+" ~ "+result.weeks[result.weeks.length-1]+" <button type='button' class='nextSearch' onclick='nextSearch(\""+result.weeks[result.weeks.length-1]+"\")'>&gt;</button>";
			
			console.log(result.able_times);
			if(result.able_times.length > 0){
				//var cal_sel = document.querySelector("#cal_sel");
				//cal_sel.innerHTML = "<button type='button' class='prevSearch' onclick='prevSearch(\""+result.weeks[0]+"\")'>&lt;</button> "+result.weeks[0]+" ~ "+result.weeks[result.weeks.length-1]+" <button type='button' class='nextSearch' onclick='nextSearch(\""+result.weeks[result.weeks.length-1]+"\")'>&gt;</button>";
				
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td>선생님</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				
				var days = ["일", "월", "화", "수", "목", "금", "토"];
				for(var i=0; i<7; i++){
					document.querySelector("#day"+i).innerHTML = days[i]+"<br>("+moment(result.weeks[i]).format("MM-DD")+")"; 
				}
				
				Array.prototype.forEach.call(result.able_times, function(at, i){
					var row = teacher_sel.insertRow();
					var cell0 = row.insertCell(0);					
					var cell1 = row.insertCell(1);
					var cell2 = row.insertCell(2);
					var cell3 = row.insertCell(3);
					var cell4 = row.insertCell(4);
					var cell5 = row.insertCell(5);
					var cell6 = row.insertCell(6);
					var cell7 = row.insertCell(7);
					var cell8 = row.insertCell(8);

					var stime = addTimeCollon(at.start_time);
					var etime = addTimeCollon(at.end_time);
					cell0.innerText = stime+"~"+etime;
					cell1.innerText = at.teacher_name;

					Array.prototype.forEach.call(at.days, function(d, i){
						if(d.day == 0)	{cell2.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 1)	{cell3.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 2)	{cell4.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 3)	{cell5.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 4)	{cell6.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 5)	{cell7.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 6)	{cell8.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
					});
				});
			}
			else{
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td>선생님</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				alert("해당 시간대 조건에 맞는 강사가 없습니다.");
			}
			
		}, error : function() {
			
		}
	}); // ajax
	
}



function nextSearch(date){ 
	var formData = new FormData(document.querySelector('#leveltestForm'));
	formData.set("start_date", moment(date).add("1", "d").format("YYYY-MM-DD"));
	
	$.ajax({
		url : '/popup/leveltestApp/search',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(result) {
			
			console.log(result.able_times);
			if(result.able_times.length > 0){
				var cal_sel = document.querySelector("#cal_sel");
				cal_sel.innerHTML = "<button type='button' class='prevSearch' onclick='prevSearch(\""+result.weeks[0]+"\")'>&lt;</button> "+result.weeks[0]+" ~ "+result.weeks[result.weeks.length-1]+" <button type='button' class='nextSearch' onclick='nextSearch(\""+result.weeks[result.weeks.length-1]+"\")'>&gt;</button>";
				
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td>선생님</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				
				var days = ["일", "월", "화", "수", "목", "금", "토"];
				for(var i=0; i<7; i++){
					document.querySelector("#day"+i).innerHTML = days[i]+"<br>("+moment(result.weeks[i]).format("MM-DD")+")"; 
				}
				
				Array.prototype.forEach.call(result.able_times, function(at, i){
					var row = teacher_sel.insertRow();
					var cell0 = row.insertCell(0);					
					var cell1 = row.insertCell(1);
					var cell2 = row.insertCell(2);
					var cell3 = row.insertCell(3);
					var cell4 = row.insertCell(4);
					var cell5 = row.insertCell(5);
					var cell6 = row.insertCell(6);
					var cell7 = row.insertCell(7);
					var cell8 = row.insertCell(8);

					var stime = addTimeCollon(at.start_time);
					var etime = addTimeCollon(at.end_time);
					cell0.innerText = stime+"~"+etime;
					cell1.innerText = at.teacher_name;

					Array.prototype.forEach.call(at.days, function(d, i){
						if(d.day == 0)	{cell2.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 1)	{cell3.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 2)	{cell4.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 3)	{cell5.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 4)	{cell6.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 5)	{cell7.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 6)	{cell8.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
					});
				});
			}
			else{
				//var teacher_sel = document.querySelector("#teacher_sel");
				//teacher_sel.innerHTML = "<tr><td>수업시간</td><td>선생님</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				alert("해당 시간대 조건에 맞는 강사가 없습니다.");
				
			}
			
		}, error : function() {
			
		}
	}); // ajax
	
}



function prevSearch(date){ 
	var formData = new FormData(document.querySelector('#leveltestForm'));
	formData.set("start_date", moment(date).add("-1", "d").format("YYYY-MM-DD"));
	
	$.ajax({
		url : '/popup/leveltestApp/search',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(result) {
			
			console.log(result.able_times);
			if(result.able_times.length > 0){
				var cal_sel = document.querySelector("#cal_sel");
				cal_sel.innerHTML = "<button type='button' class='prevSearch' onclick='prevSearch(\""+result.weeks[0]+"\")'>&lt;</button> "+result.weeks[0]+" ~ "+result.weeks[result.weeks.length-1]+" <button type='button' class='nextSearch' onclick='nextSearch(\""+result.weeks[result.weeks.length-1]+"\")'>&gt;</button>";
				
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td>선생님</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				
				var days = ["일", "월", "화", "수", "목", "금", "토"];
				for(var i=0; i<7; i++){
					document.querySelector("#day"+i).innerHTML = days[i]+"<br>("+moment(result.weeks[i]).format("MM-DD")+")"; 
				}
				
				Array.prototype.forEach.call(result.able_times, function(at, i){
					var row = teacher_sel.insertRow();
					var cell0 = row.insertCell(0);					
					var cell1 = row.insertCell(1);
					var cell2 = row.insertCell(2);
					var cell3 = row.insertCell(3);
					var cell4 = row.insertCell(4);
					var cell5 = row.insertCell(5);
					var cell6 = row.insertCell(6);
					var cell7 = row.insertCell(7);
					var cell8 = row.insertCell(8);

					var stime = addTimeCollon(at.start_time);
					var etime = addTimeCollon(at.end_time);
					cell0.innerText = stime+"~"+etime;
					cell1.innerText = at.teacher_name;

					Array.prototype.forEach.call(at.days, function(d, i){
						if(d.day == 0)	{cell2.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 1)	{cell3.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 2)	{cell4.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 3)	{cell5.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 4)	{cell6.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 5)	{cell7.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 6)	{cell8.innerHTML = "<input type='radio' class_time='"+stime+"' user_teacher_no='"+at.user_teacher_no+"' name='time_sel' value='"+d.study_date+"'>"}
					});
				});
			}
			else{
				//var teacher_sel = document.querySelector("#teacher_sel");
				//teacher_sel.innerHTML = "<tr><td>수업시간</td><td>선생님</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				alert("해당 시간대 조건에 맞는 강사가 없습니다.");
				
			}
			
		}, error : function() {
			
		}
	}); // ajax
	
}


// 신청 등록
function insertBtn(){
	if($("input:radio[name='time_sel']:checked").length == 0){
		alert("수업시간을 선택해주세요.");
		return false;
	}
	
	//console.log("study_date", $("input:radio[name='time_sel']:checked").val());
	//console.log("class_time", $("input:radio[name='time_sel']:checked").attr("class_time"));	
	//console.log("user_teacher_no", $("input:radio[name='time_sel']:checked").attr("user_teacher_no"));
	
	$.ajax({
		url : '/popup/leveltestApp/insertLeveltest',			
		type : 'post',
		data : {
			"study_date": $("input:radio[name='time_sel']:checked").val(),
			"class_time": $("input:radio[name='time_sel']:checked").attr("class_time"),		
			"user_teacher_no": $("input:radio[name='time_sel']:checked").attr("user_teacher_no"),
			
			"user_student_no": $("#user_student_no").val(),
			"leveltest_language": $("#leveltest_language").val(),
			"leveltest_type": $("#leveltest_type").val(),
			"class_phone": $("#class_phone").val()
		},
		success : function() {
			alert("레벨테스트 신청이 완료되었습니다.");
			//window.location.reload();
			window.close();
		}, error : function() {
			alert("레벨테스트 신청 실패");
		}
	}); // ajax
}








