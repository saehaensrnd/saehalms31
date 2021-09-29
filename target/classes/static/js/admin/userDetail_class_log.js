function fileBtn(item) {
	if(item.flag == 'class'){
		$.ajax({
			url: '/teacher/todaySchedule/videoFile',
			type: 'post',
			data: {
				'flag': item.flag,
				'no': item.class_score_no
			},
			success: function(data) {
				console.log(data);
				window.open("/teacher/todaySchedule/fileList?flag=" +item.flag +"&no=" +item.class_score_no + "&study_date="+data.study_date  + "&urlSplitString=" + data.urlSplitString+ "&recordIdSplitString=" + data.recordIdSplitString, 'fileListPopup', 'width=600, height=500 left=650, top=150');
				
			}, error: function() {
				//alert("서버통신 오류");
			} // success
		}); // ajax
	}
	
	/*$.ajax({
		url: '/teacher/todaySchedule/videoFile',
		type: 'post',
		data: {
			'class_score_no': class_score_no
		},
		success: function(data) {
			console.log(data);
			window.open("/teacher/todaySchedule/fileList?study_date="+data.study_date + "&class_score_no=" + data.class_score_no  + "&urlSplitString=" + data.urlSplitString+ "&recordIdSplitString=" + data.recordIdSplitString, 'fileListPopup', 'width=600, height=500 left=650, top=150');
			
		}, error: function() {
			//alert("서버통신 오류");
		} // success
	}); // ajax*/
}

function addZero(number){
	return (number < 10)? "0"+number:number;
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

$(document).ready(function(){
	$("#all_check").click(function(){
		if($(this).prop("checked")){
			$(".rowCheck").prop("checked", true);
		}
		else{
			$(".rowCheck").prop("checked", false);
		}
	});

	$(".rowCheck").click(function(){
		if($(".rowCheck:checked").length == $(".rowCheck").length){
			$("#all_check").prop("checked", true);
		}
		else{
			$("#all_check").prop("checked", false);
		}
	});
});

function changeCancel(flag){
	let cnt = $(".rowCheck:checked").length;	
	let selectedArr = new Array();
	
	$(".rowCheck:checked").each(function() {		
		selectedArr.push($(this).attr('name'));		// class_score_no
	});
	
	console.log(selectedArr);
	
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
	} else {
		if(confirm("정말로 취소 하시겠습니까?")){	
			if(flag == "class"){
				$.ajax({
					url: '/userDetail/changeCancelClass',
					type: 'post',
					data: {
						"selectedArr": selectedArr
					},
					success: function() {
						alert("선택된 수업들을 취소했습니다.");
						window.location.reload();
					}, error: function() {
						alert("서버통신 오류");
					}
				}); // ajax
			}
			else if(flag == "leveltest"){
				$.ajax({
					url: '/userDetail/changeCancelLeveltest',
					type: 'post',
					data: {
						"selectedArr": selectedArr
					},
					success: function() {
						alert("선택된 수업들을 취소했습니다.");
						window.location.reload();
					}, error: function() {
						alert("서버통신 오류");
					}
				}); // ajax
			}
		}
	}
}

function makeUpClass(class_no, user_student_no){
	window.open("/userDetail/makeUpClass?class_no="+class_no+"&user_student_no="+user_student_no, 'makeUpClass', 'width=800, height=540 left=550, top=70');
}

function changeProgress(class_log_no, user_student_no){
	window.open("/userDetail/changeProgress?class_log_no="+class_log_no+"&user_student_no="+user_student_no, 'changeProgress', 'width=800, height=540 left=550, top=70');
}

function modifyClass(class_log_no, class_no, user_student_no){
	window.open("/userDetail/modifyClass?class_log_no="+class_log_no+"&class_no="+class_no+"&user_student_no="+user_student_no, 'makeUpClass', 'width=800, height=540 left=550, top=70');
}

function modifyLeveltest(leveltest_no, user_student_no){
	window.open("/userDetail/modifyLeveltest?no="+leveltest_no+"&user_student_no="+user_student_no, 'makeUpClass', 'width=800, height=540 left=550, top=70');
}

function deleteClass(class_score_no){
	if(confirm("삭제 하시겠습니까?")){
		$.ajax({   			
			url : '/userDetail/deleteClassScore',
			type : 'post',
			data: {'class_score_no' : class_score_no},
			success : function() {				
				alert("삭제 되었습니다.");                    
				window.location.reload();
			}, error : function() {
				alert("삭제 실패");
			} // success
		}); // ajax			
	}	
}

function deleteLeveltest(leveltest_no){
	if(confirm("삭제 하시겠습니까?")){
		$.ajax({   			
			url : '/userDetail/deleteLeveltest',
			type : 'post',
			data: {'leveltest_no' : leveltest_no},
			success : function() {				
				alert("삭제 되었습니다.");                    
				window.location.reload();
			}, error : function() {
				alert("삭제 실패");
			} // success
		}); // ajax			
	}	
}