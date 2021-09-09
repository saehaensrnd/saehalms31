// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function() {
	let perPageNum = $("#perPageNum option:selected").val();
	let searchDate = $("#searchDate").val();
	let searchType = $("#searchType").val();
	let keyword = $("#keyword").val();
	
	let searchStartHour = $("#searchStartHour").val();
	let searchEndHour = $("#searchEndHour").val();

	let params = "?page=1" + "&perPageNum=" + perPageNum + "&searchType=" + searchType + "&keyword=" + keyword
		+ "&searchDate=" + searchDate + "&searchStartHour=" + searchStartHour + "&searchEndHour=" + searchEndHour;
	window.location.href = "/admin/todaySchedule" + params;
});


// 검색
function search() {
	let keyword = $("#keyword").val();
	let searchDate = $("#searchDate").val();
	let searchType = $("#searchType").val();
	let perPageNum = $("#perPageNum option:selected").val();
	
	let searchStartHour = $("#searchStartHour").val();
	let searchEndHour = $("#searchEndHour").val();

	let params = "?page=1" + "&perPageNum=" + perPageNum + "&searchType=" + searchType + "&keyword=" + keyword
		+ "&searchDate=" + searchDate + "&searchStartHour=" + searchStartHour + "&searchEndHour=" + searchEndHour;
	window.location.href = "/admin/todaySchedule" + params;
}


// 전체 선택,선택해제
$(document).on("click", ".allCheck", function() {
	let chkList = $('.rowCheck');

	for (let i = 0; i < chkList.length; i++) {
		chkList[i].checked = this.checked;
	}
});


function openUserDetail(user_student_no) {
	let flag = "normal";
	window.open("/userDetail?user_student_no=" + user_student_no + "&flag=" + flag + "&no=", 'userInfoPopup', 'width=1500, height=900 left=200, top=50');
}

function reportBtn(item) {
	let no = item.class_score_no;

	if (item.flag == "leveltest") {
		no = item.leveltest_no;
	}

	window.open("/reportPopup?flag=" + item.flag + "&no=" + no, 'reportPopup', 'width=1500, height=900 left=200, top=50');
}



function excelBtn(cri) {	
	
	$.ajax({
		url : '/todaySchedule/excelDownload',
		type : 'post',
		data : {
			'searchType' : cri.searchType
		  , 'keyword' : cri.keyword	
		  , 'searchDate' : cri.searchDate
		  , 'searchStartHour' : cri.searchStartHour
		  , 'searchEndHour' : cri.searchEndHour
		  , 'flag' : 'admin'
		},
		success : function(downloadURL) {
			window.location.href = downloadURL;
			alert("엑셀 다운로드가 완료되었습니다.");
		}, error : function() {
			alert("서버통신 오류");
		} // success
	}); // ajax
	
}

// BBB 화상 강의실 생성
function videoClassBtn(item) {
	console.log(item);
	let class_video_teacher_log_no = 0;
	// flag, room_no, class_no, class_score_no, leveltest_no, study_date, class_time
	// flag, room_no, study_date, class_time 

	$.ajax({
		url: '/teacher/todaySchedule/videoClass',
		type: 'post',
		data: {
			'flag': item.flag
			, 'room_no': item.room_no
			, 'class_no': item.class_no
			, 'class_log_no': item.class_log_no
			, 'class_score_no': item.class_score_no
			, 'leveltest_no': item.leveltest_no
			, 'study_date': item.study_date
			, 'class_time': item.class_time
		},
		success: function(data) {
			console.log(data);			
			var newWindow = window.open(data.joinUrl, 'viewClassPopup', 'width=1500, height=900 left=200, top=50');
			
			// 강의실 생성이 제대로 됐다면 class_video_teacher_log - Insert		// 슈퍼관리자는 log처리 x		
			/*
			$.ajax({
				url: '/teacher/todaySchedule/videoTeacherLogInsert',
				type: 'post',
				data: {
					'class_log_no': item.class_log_no
				},
				success: function(map) {
					class_video_teacher_log_no = map.class_video_teacher_log_no
					console.log("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
					console.log(class_video_teacher_log_no);
					
					let timer = setInterval(function() { 
			           if(newWindow.closed) {
						    clearInterval(timer);
			                //alert("팝업창 닫힘");		
							videoTeacherLogUpdate(class_video_teacher_log_no);	// out_time update				
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
			*/
		}, error: function() {
			//alert("서버통신 오류");
		} // success
	}); // ajax
}

function videoTeacherLogUpdate(class_video_teacher_log_no){
	//console.log("class_video_teacher_log_no : " + class_video_teacher_log_no);
	
	$.ajax({
		url: '/teacher/todaySchedule/videoTeacherLogUpdate',
		type: 'post',
		data: {
			'class_video_teacher_log_no': class_video_teacher_log_no
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
			'class_score_no': item.class_score_no
			/*, 'flag': item.flag
			, 'room_no': item.room_no
			, 'class_no': item.class_no			
			, 'leveltest_no': item.leveltest_no
			, 'study_date': item.study_date
			, 'class_time': item.class_time*/
		},
		success: function(data) {
			console.log(data);
			window.open("/teacher/todaySchedule/fileList?study_date="+data.study_date + "&class_score_no=" + data.class_score_no  + "&urlSplitString=" + data.urlSplitString+ "&recordIdSplitString=" + data.recordIdSplitString, 'fileListPopup', 'width=600, height=500 left=650, top=150');
			
		}, error: function() {
			//alert("서버통신 오류");
		} // success
	}); // ajax


}




// 강의실 정보 (해당 MeetingId로 강의실 생성되어있는지 확인용)
function videoClassInfoBtn(item) {

	$.ajax({
		url: '/teacher/todaySchedule/videoClassInfo',
		type: 'post',
		data: {
			'flag': item.flag
			, 'room_no': item.room_no
			, 'class_no': item.class_no
			, 'class_score_no': item.class_score_no
			, 'leveltest_no': item.leveltest_no
			, 'study_date': item.study_date
			, 'class_time': item.class_time
		},
		success: function(data) {
			
			//window.open("/teacher/todaySchedule/fileList?study_date="+data.study_date + "&urlSplitString=" + data.urlSplitString, 'fileListPopup', 'width=600, height=500 left=650, top=150');
			
		}, error: function() {
			//alert("서버통신 오류");
		} // success
	}); // ajax


}






function videoLogBtn(item) {
	console.log(item);	
	let class_score_no = item.class_score_no;	
	
	window.open("/teacher/todaySchedule/videoLogList?class_score_no="+class_score_no, 'fileListPopup', 'width=700, height=500 left=600, top=150');	
}








