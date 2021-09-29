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

function studySel(sel){
	$.ajax({
		url : '/userDetail/studySelect',			
		type : 'post',
		data : {
			'study_no': $(sel).val()
		},
		success : function(data) {
			var textbookSel = document.querySelector("#textbook_no");
			textbookSel.options.length = 0;

			console.log(data.length);
			textbookSel.options[0] = new Option("-- 선택 --", 0);
			if(data.length > 0){
				for(var i=1; i<=data.length; i++){
					textbookSel.options[i] = new Option(data[i-1].textbook_name, data[i-1].textbook_no);
				}
			}
			
//				jcf.replaceAll();
		}, error : function() {
			alert("studySel 실패");
		}
	}); // ajax
}

function searchTeacher(){
	if($("input:checkbox[name='teacher_week']:checked").length == 0){
		alert("요일을 체크해주세요");
		return false;
	}
	if($("#user_teacher_no").val() == 0){
		alert("tutor를 선택해주세요");
		return false;
	}
	
	var tmp = "";
	$("input:checkbox[name='teacher_week']:checked").each(function(){
		tmp += $(this).val()+"/";
	});
	
	var week = tmp.substring(0, tmp.length-1);
	var formData = new FormData(document.querySelector('#setTutorForm'));
	formData.append("week", week);
	
	$.ajax({
		url : '/userDetail/searchTeacher',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(able_times) {
			if(able_times.length > 0){
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td>[티칭센터] 선생님</td><td>선택</td></tr>";
				
				Array.prototype.forEach.call(able_times, function(at, i){
					var row = teacher_sel.insertRow();
					var cell1 = row.insertCell(0);
					var cell2 = row.insertCell(1);
					var cell3 = row.insertCell(2);
					
					cell1.innerText = addTimeCollon(at.start_time)+"~"+addTimeCollon(at.end_time);
					cell2.innerText = "["+at.center_name+"]"+at.teacher_name;
					cell3.innerHTML = "<input type='radio' name='time_sel' value='"+addTimeCollon(at.start_time)+"'>";
				});
			}
			else{
				var teacher_sel = document.querySelector("#teacher_sel");
				teacher_sel.innerHTML = "<tr><td>수업시간</td><td>[티칭센터] 선생님</td><td>선택</td></tr>";
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
	if($("#textbook_no").val() == 0){
		alert("수업 교재를 선택해주세요.");
		return false;
	}

	var formData = new FormData(document.querySelector('#setTutorForm'));
	formData.append("class_time", $("input:radio[name='time_sel']:checked").val());
	
	$.ajax({
		url : '/userDetail/setTutor',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {
			alert("강사가 배정되었습니다.");
			window.location.reload();
		}, error : function() {
			alert("강사 배정 에러");
		}
	}); // ajax
}

function addTimeCollon(time){
	return [time.slice(0,2), ":", time.slice(2)].join("");
}

function userDetail_info(flag, no, user_student_no){
	window.location.href = "/userDetail/userDetail_info?flag="+flag+"&no="+no+"&user_student_no="+user_student_no;
}

function userDetail_classLog(flag, no, user_student_no){
	window.location.href = "/userDetail/userDetail_classLog?flag="+flag+"&no="+no+"&user_student_no="+user_student_no;
}

function userDetail_setTutor(flag, no, user_student_no){
	if(flag == "class"){
		window.location.href = "/userDetail/userDetail_setTutorClass?flag="+flag+"&no="+no+"&user_student_no="+user_student_no;
	}
	else if(flag == "leveltest"){
		window.location.href = "/userDetail/userDetail_setTutorLeveltest?flag="+flag+"&no="+no+"&user_student_no="+user_student_no;
	}
}