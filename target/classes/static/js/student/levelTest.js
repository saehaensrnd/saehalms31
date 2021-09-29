function reportBtn(item){
	console.log(item);
	window.open("/student/myClass/levelReport?leveltest_no=" + item.leveltest_no, 'levelReportPopup', 'width=800, height=850 left=550, top=30');
}


function leveltestPopupBtn(user, leveltestBtnFlag){
	if(leveltestBtnFlag == "1"){
		window.open("/student/levelTest/leveltestApp?user_student_no="+user.user_student_no, 'leveltestPopup', 'width=870, height=840 left=550, top=70');	
	} else {
		alert("레벨테스트신청 기간이 아닙니다.");
	}
}