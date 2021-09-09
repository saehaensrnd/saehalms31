function startTime(start){
	var s = Number($(start).val());
	if(s >= 21){
		$("#end_time").val(23).prop("selected", true);
	}
	else{
		$("#end_time").val(s+2).prop("selected", true);
	}
}

function endTime(end){
	var e = Number($(end).val());
	if(e <= 2){
		$("#start_time").val(0).prop("selected", true);
	}
	else{
		$("#start_time").val(e-2).prop("selected", true);
	}
}

function changeDate(node){
	var today = new Date();   

	var year = today.getFullYear();
	var month = ('0' + (today.getMonth() + 1)).slice(-2);
	var date = ('0' + today.getDate()).slice(-2);
	
	if(node.value < year+"-"+month+"-"+date){
		alert("오늘 이전의 날짜는 선택이 불가능합니다.");
		node.value = "";
		return;
	}
}

function centerSel(sel){
	$.ajax({
		url : '/userDetail/centerSelect',
		type : 'post',
		data : {
			'center_no': $(sel).val()
		},
		success : function(data) {
			$("#teacher_group_no").find("option:eq(0)").prop("selected", true);
			var teacherSel = document.querySelector("#user_teacher_no");
			teacherSel.options.length = 0;

			console.log(data.length);
			teacherSel.options[0] = new Option("-- 선택 --", 0);
			if(data.length > 0){
				for(var i=1; i<=data.length; i++){
					teacherSel.options[i] = new Option(data[i-1].teacher_name, data[i-1].user_teacher_no);
				}
			}
			
//				jcf.replaceAll();
		}, error : function() {
			alert("studySel 실패");
		}
	}); // ajax
}

function groupSel(sel){
	$.ajax({
		url : '/userDetail/groupSelect',			
		type : 'post',
		data : {
			'teacher_group_no': $(sel).val()
		},
		success : function(data) {
			$("#center_no").find("option:eq(0)").prop("selected", true);
			var teacherSel = document.querySelector("#user_teacher_no");
			teacherSel.options.length = 0;

			console.log(data.length);
			teacherSel.options[0] = new Option("-- 선택 --", 0);
			if(data.length > 0){
				for(var i=1; i<=data.length; i++){
					teacherSel.options[i] = new Option(data[i-1].teacher_name, data[i-1].user_teacher_no);
				}
			}
			
//				jcf.replaceAll();
		}, error : function() {
			alert("studySel 실패");
		}
	}); // ajax
}

function searchTeacher(){
	if($("#start_date").val() == "" || $("#start_date").val() == null){
		alert("시작일을 입력해주세요");
		return false;
	}
	if($("#user_teacher_no").val() == 0){
		alert("tutor를 선택해주세요");
		return false;
	}
	
	var formData = new FormData(document.querySelector('#makeupForm'));
	
	$.ajax({
		url : '/userDetail/searchTeacherLeveltest',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(result) {
			console.log(result.able_times);
			if(result.able_times.length > 0){
				var cal_sel = document.querySelector("#cal_sel");
				cal_sel.innerHTML = "<button type='button' onclick='prevSearch(\""+result.weeks[0]+"\")'>&lt;</button> "+result.weeks[0]+" ~ "+result.weeks[result.weeks.length-1]+" <button type='button' onclick='nextSearch(\""+result.weeks[result.weeks.length-1]+"\")'>&gt;</button>";
				
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				
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

					var stime = addTimeCollon(at.start_time);
					var etime = addTimeCollon(at.end_time);
					cell0.innerText = stime+"~"+etime;

					Array.prototype.forEach.call(at.days, function(d, i){
						if(d.day == 0)	{cell1.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 1)	{cell2.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 2)	{cell3.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 3)	{cell4.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 4)	{cell5.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 5)	{cell6.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 6)	{cell7.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
					});
				});
			}
			else{
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				alert("해당 강사의 조건에 맞는 시간대가 없습니다.");
			}
		}, error : function() {
		}
	}); // ajax
}

function prevSearch(date){
	var formData = new FormData(document.querySelector('#makeupForm'));
	formData.set("start_date", moment(date).add("-1", "d").format("YYYY-MM-DD"));
	
	$.ajax({
		url : '/userDetail/searchTeacherLeveltest',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(result) {
			console.log(result.able_times);
			if(result.able_times.length > 0){
				var cal_sel = document.querySelector("#cal_sel");
				cal_sel.innerHTML = "<button type='button' onclick='prevSearch(\""+result.weeks[0]+"\")'>&lt;</button> "+result.weeks[0]+" ~ "+result.weeks[result.weeks.length-1]+" <button type='button' onclick='nextSearch(\""+result.weeks[result.weeks.length-1]+"\")'>&gt;</button>";
				
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				
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

					var stime = addTimeCollon(at.start_time);
					var etime = addTimeCollon(at.end_time);
					cell0.innerText = stime+"~"+etime;

					Array.prototype.forEach.call(at.days, function(d, i){
						if(d.day == 0)	{cell1.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 1)	{cell2.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 2)	{cell3.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 3)	{cell4.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 4)	{cell5.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 5)	{cell6.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 6)	{cell7.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
					});
				});
			}
			else{
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				alert("해당 강사의 조건에 맞는 시간대가 없습니다.");
			}
		}, error : function() {
		}
	}); // ajax
}

function nextSearch(date){
	var formData = new FormData(document.querySelector('#makeupForm'));
	formData.set("start_date", moment(date).add("1", "d").format("YYYY-MM-DD"));
	
	$.ajax({
		url : '/userDetail/searchTeacherLeveltest',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(result) {
			console.log(result.able_times);
			if(result.able_times.length > 0){
				var cal_sel = document.querySelector("#cal_sel");
				cal_sel.innerHTML = "<button type='button' onclick='prevSearch(\""+result.weeks[0]+"\")'>&lt;</button> "+result.weeks[0]+" ~ "+result.weeks[result.weeks.length-1]+" <button type='button' onclick='nextSearch(\""+result.weeks[result.weeks.length-1]+"\")'>&gt;</button>";
				
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				
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

					var stime = addTimeCollon(at.start_time);
					var etime = addTimeCollon(at.end_time);
					cell0.innerText = stime+"~"+etime;

					Array.prototype.forEach.call(at.days, function(d, i){
						if(d.day == 0)	{cell1.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 1)	{cell2.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 2)	{cell3.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 3)	{cell4.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 4)	{cell5.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 5)	{cell6.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
						if(d.day == 6)	{cell7.innerHTML = "<input type='radio' class_time='"+stime+"' name='time_sel' value='"+d.study_date+"'>"}
					});
				});
			}
			else{
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td id='day0'>일</td><td id='day1'>월</td><td id='day2'>화</td><td id='day3'>수</td><td id='day4'>목</td><td id='day5'>금</td><td id='day6'>토</td></tr>";
				alert("해당 강사의 조건에 맞는 시간대가 없습니다.");
			}
		}, error : function() {
		}
	}); // ajax
}

function setTutor(){
	if($("input:radio[name='time_sel']:checked").length == 0){
		alert("수업시간을 선택해주세요.");
		return false;
	}

	var formData = new FormData(document.querySelector('#makeupForm'));
	formData.append("study_date", $("input:radio[name='time_sel']:checked").val());
	formData.append("class_time", $("input:radio[name='time_sel']:checked").attr("class_time"));
	
	$.ajax({
		url : '/userDetail/makeUp',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {
			alert("보충수업이 등록되었습니다.");
			opener.parent.location.reload();
			self.close();
		}, error : function() {
			alert("makeUp 에러");
		}
	}); // ajax
}

function addZero(number){
	return (number < 10)? "0"+number:number;
}

function addTimeCollon(time){
	return [time.slice(0,2), ":", time.slice(2)].join("");
}