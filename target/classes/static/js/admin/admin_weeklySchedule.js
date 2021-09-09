// 티칭센터 셀렉트박스 change 이벤트 발생 시 - 선생님 콤보박스 셀렉트박스 Setting
function centerChange(e) {	
	let center_no = e.value;

	// 강사 셀렉트박스 세팅
	let teacherArr;

	$.ajax({
		url: '/admin/weeklySchedule/getTeacherComboByCenter',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"searchCenter": center_no
		},
		success: function(item) {
			teacherArr = item;
		}, error: function() {
			
		} 
	});
	
	// 기수년월 셀렉트박스 초기화
	let teacherSelect = document.getElementById("searchTeacher");
	for (i = 0; i < teacherSelect.options.length; i++) {
		teacherSelect.options[i] = null;
	}
	

	if (teacherArr.length > 0) {
		teacherSelect.options[0] = new Option("-- 선택 --", "");
	} else {
		teacherSelect.options[0] = new Option("-- 선택 --", "");
	}

	if (teacherArr.length > 0) {
		for (i = 1; i <= teacherArr.length; i++) {
			teacherSelect.options[i] = new Option(teacherArr[i - 1].teacher_name, teacherArr[i - 1].user_teacher_no);		// text, value
		}
	}
	
	jcf.replaceAll();

}


function search(cri) {
	let searchTeacher = $("#searchTeacher").val();
	let searchCenter = $("#searchCenter").val();
	
	if(searchTeacher == ''){
		alert("선생님을 선택하세요.");
		return false;		
	}

	let thisDay;
	let today = new Date();
	
	if(cri.thisDay == ""){
		thisDay = getFormatDate(today);	
	} else {
		thisDay = cri.thisDay;
	}
	
	let params = "?searchCenter=" + searchCenter+"&searchTeacher="+searchTeacher+"&thisDay="+thisDay;
	
	window.location.href = "/admin/weeklySchedule" + params;
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
	
	let params = "?searchCenter=" + cri.searchCenter+"&searchTeacher="+cri.searchTeacher+"&thisDay="+prevDay;
	window.location.href = "/admin/weeklySchedule" + params;
}

function nextBtn(cri){
	let thisDay = new Date(cri.thisDay); 
	thisDay.setDate(new Date(cri.thisDay).getDate() + 7);
	
	let nextDay = getFormatDate(thisDay);		// 지난주 날짜
	
	let params = "?searchCenter=" + cri.searchCenter+"&searchTeacher="+cri.searchTeacher+"&thisDay="+nextDay;
	window.location.href = "/admin/weeklySchedule" + params;
}




/*
$( document ).ready(function() {
});
*/

//console.log($('#2_1900').attr('name'));
//let classInfoArr = [];
//classInfoArr = $('#2_1900').attr('onclick');


/*
function goDetail(classList, nodeId, levelTestList) {
	//console.log(classList);	
	//console.log(nodeId);
	//console.log(levelTestList);
	
	if (levelTestList.length > 0 && classList.length > 0) {		
		let a = '';
		a += '<a class="link2" onclick="openModal(' + classList[0].class_no + ',' + classList[0].user_no + ',' + classList[0].teacher_no + ", 'class'" + ' )" >' + classList[0].user_name + ' [c] </a><br>';
		a += '<a style="margin-top:5px;" class="link2" onclick="openModal(' + levelTestList[0].no + ',' + levelTestList[0].user_no + ',' + levelTestList[0].teacher_no + ", 'levelTest'" + ')">' + levelTestList[0].user_name + ' [l] </a>';
		
		document.getElementById(nodeId).innerHTML = a;	
		
	} else if (levelTestList.length > 0) {		
		document.getElementById(nodeId).innerText = levelTestList[0].user_name;
		document.getElementById(nodeId).setAttribute('onclick', 'openModal(' + levelTestList[0].no + ',' + levelTestList[0].user_no + ',' + levelTestList[0].teacher_no + ', "levelTest")');
		
	} else if (classList.length > 0) {
		//console.log(classList[0].user_name); // document.getElementById(nodeId)
		document.getElementById(nodeId).innerText = classList[0].user_name;
		document.getElementById(nodeId).setAttribute('onclick', 'openModal(' + classList[0].class_no + ',' + classList[0].user_no + ',' + classList[0].teacher_no + ', "class")');
	}
}
*/

function goDetail(classList, nodeId, levelTestList) {
	//console.log(classList);	
	//console.log(nodeId);
	//console.log(levelTestList);
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
				
		//document.getElementById(nodeId).innerText = classList[0].student_name;
		//document.getElementById(nodeId).setAttribute('onclick', 'openModal(' + classList[0].class_no + ',' + classList[0].user_no + ',' + ', "class")');
	}
}


function openModal(no, user_student_no, flag) {
	window.open("/userDetail?no="+no+"&user_student_no="+user_student_no+"&flag="+flag, 'classDetailPopup', 'width=1500, height=900 left=200, top=50');
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


//console.log($(child).index());		// tr index
//var thNum = $(child).parent().find("td").index(child);	// td 인덱스 .. 몇번째 td인지 2


/*
let child = document.getElementById('1_1950');
let childIndex = getIndex(child);

let parent = child.parentNode;		//console.log(parent.childNodes);

console.log(childIndex);
if(parent.nextElementSibling != null) {
	let changeTd = parent.nextElementSibling.childNodes[childIndex];
	console.log(changeTd);
	
	changeTd.setAttribute('class', 'inClass');	
}
*/







