function classChange(e) {
	window.location.href = "/student/myClass?selectedNo="+e.value;
}

// BBB 화상 강의실 생성
function videoClassBtn(item) {
	//console.log(item);	
	
	let class_video_student_log_no = 0;	

	$.ajax({
		url: '/teacher/todaySchedule/videoClass',
		type: 'post',
		data: {						
			'flag': 'class'
		  , 'no': item.class_score_no
		},
		success: function(data) {
			console.log(data);			
			var newWindow = window.open(data.joinUrl, 'viewClassPopup', 'width=1500, height=900 left=200, top=50');
			
			// 강의실 생성이 제대로 됐다면 class_video_student_log - Insert						
			$.ajax({
				url: '/student/myClass/videoStudentLogInsert',
				type: 'post',
				data: {
					'class_score_no': item.class_score_no
				},
				success: function(map) {
					class_video_student_log_no = map.class_video_student_log_no
					console.log("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
					console.log(class_video_student_log_no);
					
					let timer = setInterval(function() { 
			           if(newWindow.closed) {
						    clearInterval(timer);
			                //alert("팝업창 닫힘");		
							videoStudentLogUpdate(class_video_student_log_no);	// out_time update				
			           }
			        }, 1000);
					
				}, error: function() {
					alert("서버통신 오류");
					
					let timer = setInterval(function() { 
			           if(newWindow.closed) {	// 팝업창 닫힘
						    clearInterval(timer);	
			           }
			        }, 1000);
				} // success
			}); // ajax
			
		}, error: function() {
			//alert("서버통신 오류");
		} // success
	}); // ajax
	
}


function videoStudentLogUpdate(class_video_student_log_no){
	//console.log("class_video_teacher_log_no : " + class_video_teacher_log_no);
	
	$.ajax({
		url: '/student/myClass/videoStudentLogUpdate',
		type: 'post',
		data: {
			'class_video_student_log_no': class_video_student_log_no
		},
		success: function(data) {
			//alert("videoTeacherLogUpdate out_time update 완료");
					
		}, error: function() {
			//alert("서버통신 오류");
		} // success
	}); // ajax
}


function fileBtn(item) {
	//console.log(item);
	
	$.ajax({
		url: '/teacher/todaySchedule/videoFile',
		type: 'post',
		data: {
			'flag': 'class',
			'no': item.class_score_no
		},
		success: function(data) {
			console.log(data);
			window.open("/teacher/todaySchedule/fileList?flag=class&no=" +item.class_score_no + "&study_date="+data.study_date  + "&urlSplitString=" + data.urlSplitString+ "&recordIdSplitString=" + data.recordIdSplitString, 'fileListPopup', 'width=600, height=500 left=650, top=150');
			
		}, error: function() {
			//alert("서버통신 오류");
		} // success
	}); // ajax
}


function viewTextbook(textbook_no){
	window.open("/student/myClass/onlineBook?textbook_no="+textbook_no, 'viewClassPopup', 'width=1500, height=950 left=200, top=20');
	
}
/*
function viewBtnTest(){
	window.open("/student/myClass/print", 'printPopup', 'width=600, height=800 left=650, top=100');
}
*/

function viewBtn(classInfo){
	console.log(classInfo);
	$('#student_name').html(classInfo.student_name);
	$('#student_name2').html(classInfo.student_name);
	
	let type = "";
	type = classInfo.product_type.replace(/Video/gi, "화상").replace(/Phone/gi, "음성");
	
	if(classInfo.product_language == 'English'){
		type += '영어';
	} else {
		type += '중국어';
	}
	
	$('#type').html(type);
	$('#product_name').html(classInfo.product_name);
	$('#product_price').html(classInfo.product_price);
	$('#class_date').html(classInfo.start_date+'~'+classInfo.end_date);
	$('#teacher_name').html(classInfo.teacher_name);
	$('#totalCnt').html(classInfo.totalCnt);
	$('#presentCnt').html(classInfo.presentCnt);
	$('#absentCnt').html(classInfo.absentCnt);
	$('#remainCnt').html(classInfo.remainCnt);
	
	let presentPercent = 0;	
	if(classInfo.presentCnt > 0){
		presentPercent = Math.round(classInfo.presentCnt / classInfo.totalCnt * 100); // Math.round 소수점에서 반올림 정수표기
	} else {
		presentPercent = 0;
	}
	$('#presentPercent').html(presentPercent);	
	
	let today = new Date();   
	let year = today.getFullYear(); // 년도
	let month = today.getMonth() + 1;  // 월
	let date = today.getDate();  // 날짜
	
	$('#printToday').html(year + '.' + month + '.' + date);
	
	
	
    $('.myModal').css('display','block');
	$('body').css("overflow", "hidden");		
}

// 재부여 Modal Close
$(document).on("click", ".close", function(){   
	$('.myModal').css('display','none');
	$('body').css("overflow", "scroll");   
});

function closeBtn(){
	$('.myModal').css('display','none');
	$('body').css("overflow", "scroll");
}


var printDiv;
var initBody;

function printBtn(){
	printDiv = document.getElementById('printDiv');
	
	window.onbeforeprint = beforePrint();
	window.onafterprint = afterPrint();
	
	setTimeout(function() {		
		window.print();
		$('#btnDiv').show();
		$('.myModal-header').show();
		$('header').show();
		//window.close();
	}, 250);
}

function beforePrint(){	
	$('#btnDiv').hide();
	$('.myModal-header').hide();	
	$('header').hide();
	
	initBody = document.body.innerHTML;
	document.body.innerHTML = printDiv.innerHTML;
}

function afterPrint(){	
	//$('.btnDiv').show();
	//$('.btnDiv').css('display', 'block');
	document.body.innerHTML = initBody;	
	
}



/*
function printBtn() {		
	
	var initBody = document.body.innerHTML;
	window.onbeforeprint = function () {
		document.body.innerHTML = document.getElementById("printDiv").innerHTML;
	}
	window.onafterprint = function () {
		document.body.innerHTML = initBody;
	}
	
	setTimeout(function() {
		window.print();
		window.close();
	}, 500);
}
*/

/*
function printBtn() {
	setTimeout(function(){
		console.log("sdsfsdf");
		var initBody = document.body.innerHTML;
		window.onbeforeprint = function () {
			document.body.innerHTML = document.getElementById("printDiv").innerHTML;
		}
		window.onafterprint = function () {
			document.body.innerHTML = initBody;
		}
		window.print(); 
		window.close();
		}, 1000
	);
	
	setTimeout(function() {
		window.print();
		window.close();
	}, 300);
}
*/


function reportBtn(item){
	console.log(item);
	window.open("/student/myClass/report?class_score_no=" + item.class_score_no, 'reportPopup', 'width=800, height=850 left=550, top=30');
}

