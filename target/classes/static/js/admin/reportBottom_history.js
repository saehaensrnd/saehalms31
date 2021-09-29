function classStudentNoChange(e) {
	let classStudentNo = e.value;
	let searchStudyDate = $('#searchStudyDate').val();
	
	if(flag == 'class'){
		window.location.href = "/reportPopup/history?flag=class&class_no=" + class_no+"&class_student_no=" + classStudentNo+"&class_score_no=" + class_score_no+"&searchStudyDate="+searchStudyDate;	
	} else {
		window.location.href = "/reportPopup/history?flag=leveltest&leveltest_no=" + leveltest_no+"&searchStudyDate="+searchStudyDate;
	}
	
	//window.location.href = "/reportPopup/history?class_no=" + class_no+"&class_student_no=" + classStudentNo+"&class_score_no=" + class_score_no;
}


function search() {
	let class_studentNo = $('#class_student_no').val();	
	let searchStudyDate = $('#searchStudyDate').val();
	
	if(flag == 'class'){
		window.location.href = "/reportPopup/history?flag=class&class_no=" + class_no+"&class_student_no=" + class_studentNo+"&class_score_no=" + class_score_no+"&searchStudyDate="+searchStudyDate;	
	} else {
		window.location.href = "/reportPopup/history?flag=leveltest&leveltest_no=" + leveltest_no+"&searchStudyDate="+searchStudyDate;
	}
	
}





