function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}

function updateClassBtn(){
	var formData = new FormData(document.querySelector('#logForm'));

	$.ajax({
		url : '/userDetail/updateClass',
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(result) {
			if(result){
				alert("수정되었습니다.");
				parent.document.location.reload();
			}
			else{
				alert("수강기간을 다시 확인해주세요.");
			}
		}, error : function() {
			alert("등록 실패");
		}
	}); // ajax
}

function deleteClass(product_personnel, class_no, class_student_no){
	if(confirm("삭제 하시겠습니까?")){
		$.ajax({
			url : '/userDetail/deleteClass',			
			type : 'post',
			data : {
				'product_personnel': product_personnel,
				'class_no': class_no,
				'class_student_no': class_student_no,
			},
			success : function() {
				alert("삭제되었습니다.");
				parent.document.location.reload();
			}, error : function() {
				alert("삭제 실패");
			}
		}); // ajax
	}
}

function deleteLeveltest(leveltest_no){
	if(confirm("삭제 하시겠습니까?")){
		$.ajax({
			url : '/userDetail/deleteLeveltest',			
			type : 'post',
			data : {
				'leveltest_no': leveltest_no
			},
			success : function() {
				alert("삭제되었습니다.");
				parent.document.location.reload();
			}, error : function() {
				alert("삭제 실패");
			}
		}); // ajax
	}
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



