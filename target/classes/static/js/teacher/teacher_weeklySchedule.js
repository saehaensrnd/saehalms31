function search(cri) {
	let thisDay;
	let today = new Date();
	
	if(cri.thisDay == ""){
		thisDay = getFormatDate(today);	
	} else {
		thisDay = cri.thisDay;
	}
	
	let params = "?thisDay="+thisDay;
	
	window.location.href = "/teacher/weeklySchedule" + params;
}


function getFormatDate(date) {
	var year = date.getFullYear();              //yyyy
	var month = (1 + date.getMonth());          //M
	month = month >= 10 ? month : '0' + month;  //month 두자리로 저장
	var day = date.getDate();                   //d
	day = day >= 10 ? day : '0' + day;          //day 두자리로 저장
	return year + '-' + month + '-' + day;       //'-' 추가하여 yyyy-mm-dd 형태 생성 가능
}


function prevBtn(cri){
	let thisDay = new Date(cri.thisDay); 
	thisDay.setDate(new Date(cri.thisDay).getDate() - 7);
	
	let prevDay = getFormatDate(thisDay);		// 지난주 날짜
	
	let params = "?thisDay="+prevDay;
	window.location.href = "/teacher/weeklySchedule" + params;
}

function nextBtn(cri){
	let thisDay = new Date(cri.thisDay); 
	thisDay.setDate(new Date(cri.thisDay).getDate() + 7);
	
	let nextDay = getFormatDate(thisDay);		// 지난주 날짜
	
	let params = "?thisDay="+nextDay;
	window.location.href = "/teacher/weeklySchedule" + params;
}


function goDetail(classList, nodeId, levelTestList) {	
	if (classList.length > 0 && levelTestList.length > 0) {
		let a = '';
		
		for(let i = 0; i<classList.length; i++){
			console.log(classList[i].studentInfoListString);
			
			let studentInfoArr = classList[i].studentInfoListString.split('#');
			
			for(let k=0; k<studentInfoArr.length; k++){
				let infoArr = studentInfoArr[k].split(',');
				
				let user_student_no = infoArr[0];
				let student_name = infoArr[1];
				let student_id = infoArr[2];
				
				a += '<a class="link2" onclick="openModal(' + classList[i].class_no + ',' + user_student_no + ", 'class'" + ' )" >' + student_name + ' ['+student_id+'] </a><br>';
			}
		}
		
		for(let i = 0; i<levelTestList.length; i++){
			console.log(levelTestList[i].levelStudentInfoListString);
			
			let levelTestStudentInfoArr = levelTestList[i].levelStudentInfoListString.split('#');
			
			for(let k=0; k<levelTestStudentInfoArr.length; k++){
				let infoArr = levelTestStudentInfoArr[k].split(',');
				
				let user_student_no = infoArr[0];
				let student_name = infoArr[1];
				let student_id = infoArr[2];
				
				a += '<a class="link2" onclick="openModal(' + levelTestList[i].leveltest_no + ',' + user_student_no + ", 'levelTest'" + ' )" >' + student_name + ' ['+student_id+'] </a><br>';
			}
		}
		
		document.getElementById(nodeId).innerHTML = a;
		
	} else if (classList.length > 0) {
		let a = '';
		
		for(let i = 0; i<classList.length; i++){
			console.log(classList[i].studentInfoListString);
			
			let studentInfoArr = classList[i].studentInfoListString.split('#');
			
			for(let k=0; k<studentInfoArr.length; k++){
				let infoArr = studentInfoArr[k].split(',');
				
				let user_student_no = infoArr[0];
				let student_name = infoArr[1];
				let student_id = infoArr[2];
				
				a += '<a class="link2" onclick="openModal(' + classList[i].class_no + ',' + user_student_no + ", 'class'" + ' )" >' + student_name + ' ['+student_id+'] </a><br>';
			}
		}
		
		document.getElementById(nodeId).innerHTML = a;
				
		//document.getElementById(nodeId).innerText = classList[0].student_name;
		//document.getElementById(nodeId).setAttribute('onclick', 'openModal(' + classList[0].class_no + ',' + classList[0].user_no + ',' + ', "class")');
	} else if (levelTestList.length > 0) {
		let a = '';
		
		for(let i = 0; i<levelTestList.length; i++){
			console.log(levelTestList[i].levelStudentInfoListString);
			
			let levelTestStudentInfoArr = levelTestList[i].levelStudentInfoListString.split('#');
			
			for(let k=0; k<levelTestStudentInfoArr.length; k++){
				let infoArr = levelTestStudentInfoArr[k].split(',');
				
				let user_student_no = infoArr[0];
				let student_name = infoArr[1];
				let student_id = infoArr[2];
				
				a += '<a class="link2" onclick="openModal(' + levelTestList[i].leveltest_no + ',' + user_student_no + ", 'levelTest'" + ' )" >' + student_name + ' ['+student_id+'] </a><br>';
			}
		}
		
		document.getElementById(nodeId).innerHTML = a;
	}
}


function openModal(no, user_student_no, flag) {
	//window.open("/userDetail?no="+no+"&user_student_no="+user_student_no+"&flag="+flag, 'classDetailPopup', 'width=1500, height=900 left=200, top=50');
	window.open("/teacher/userDetail?user_student_no=" + user_student_no + "&flag=" + flag + "&no=" + no , 'userInfoPopup', 'width=1100, height=800 left=400, top=50');
}


document.querySelectorAll(".inClass").forEach((node, index) => {
	// tmp.classList.add("active");
	// tmp.classList.remove("active");

	//console.log(node);	
	let lesson_time = node.getAttribute('name');
	
	// 수업시간 20분이면 한칸 더, 25 or 30분이면 2칸, ...
	let nextCnt = lesson_time.substr(0,1) - 1;
	
	if(lesson_time.substr(1,1) != '0'){	 // 분단위 일의자리가 0이 아닌 1~9 사이라면 십분단위 Cnt + 1 
		nextCnt++;
	}
	
	
	// 수업시간에 따른 밑에 칸 class 변경
	let nodeIndex = getIndex(node);		// 형제요소중 몇번째 index인지
	let parent = node.parentNode;		// 부모 row element
	let target;
	
	//console.log(nextCnt);
	
	for(let i=0; i<nextCnt; i++){
		if(i == 0){
			if (parent.nextElementSibling != null){
				target = parent.nextElementSibling.childNodes[nodeIndex];
				target.setAttribute('class', 'inClass');	
			}				
		} else {
			if (target.parentNode.nextElementSibling != null){
				target = target.parentNode.nextElementSibling.childNodes[nodeIndex];
				target.setAttribute('class', 'inClass');	
			}		
		}		 
		
	}
	


});




// 자신의 인덱스 구하기
function getIndex(ele) {
	for (var i = 0; i < ele.parentNode.childNodes.length; i++) {
		if (ele.parentNode.childNodes[i] === ele) {
			//console.log('elemIndex = ' + i);
			return i;
		}
	}
}






