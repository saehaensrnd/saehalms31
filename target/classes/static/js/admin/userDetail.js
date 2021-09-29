function makeClass(user_student_no){
	console.log("makeClass");
	window.open("/userDetail/makeClass?user_student_no="+user_student_no, 'makeLeveltest', 'width=800, height=840 left=550, top=70');
}

function makeLeveltest(user_student_no){
	window.open("/userDetail/makeLeveltest?user_student_no="+user_student_no, 'makeLeveltest', 'width=800, height=540 left=550, top=70');
}

function infoBtn(flag, no, user_student_no){
	var detail_iframe = document.querySelector(".detailInfoDiv iframe");
	detail_iframe.src = "/userDetail/userDetail_info?flag="+flag+"&no="+no+"&user_student_no="+user_student_no;
}

function setTutor(flag, no, user_student_no){
	var detail_iframe = document.querySelector(".detailInfoDiv iframe");
	
	if(flag == "class"){
		detail_iframe.src = "/userDetail/userDetail_setTutorClass?flag="+flag+"&no="+no+"&user_student_no="+user_student_no;
	}
	else if(flag == "leveltest"){
		detail_iframe.src = "/userDetail/userDetail_setTutorLeveltest?flag="+flag+"&no="+no+"&user_student_no="+user_student_no;
	}
}

function classLog(flag, no, user_student_no){
	var detail_iframe = document.querySelector(".detailInfoDiv iframe");
	detail_iframe.src = "/userDetail/userDetail_classLog?flag="+flag+"&no="+no+"&user_student_no="+user_student_no;
}