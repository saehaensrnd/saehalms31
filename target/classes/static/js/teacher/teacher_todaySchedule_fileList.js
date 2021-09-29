function playVideo(item){	
	let recordUrl = item.getAttribute('name'); 
	window.open(recordUrl, 'playPopup', 'width=1500, height=900 left=200, top=50');
}

// 해당 녹화파일 삭제
function deleteVideo(item){
	let recordID = item.getAttribute('name');
	console.log(recordID);	
	
	if(confirm("삭제 하시겠습니까?")){
		$.ajax({
			url: '/teacher/todaySchedule/deleteVideoFile',
			type: 'post',
			data: {
				'recordID': recordID
			},
			success: function(data) {
				if("SUCCESS" == data.returnCode){
					alert("삭제되었습니다.");
					//location.reload();
					//window.location.href = "/teacher/todaySchedule/videoFile?class_score_no="+class_score_no;
					
					$.ajax({
						url: '/teacher/todaySchedule/videoFile',
						type: 'post',
						data: {					
							 'flag': flag
						   , 'no' : no						
						},
						success: function(data) {
							console.log(data);
							window.open("/teacher/todaySchedule/fileList?study_date="+data.study_date + "&flag=" + flag + "&no=" + no  + "&urlSplitString=" + data.urlSplitString+ "&recordIdSplitString=" + data.recordIdSplitString, 'fileListPopup', 'width=600, height=500 left=650, top=150');
							
						}, error: function() {
							//alert("서버통신 오류");
						} // success
					}); // ajax
					
					
				} else {
					alert("삭제 실패.");	
				}
							
			}, error: function() {
				alert("서버통신 오류");
			} // success
		}); // ajax

	}
	
	//window.open(recordUrl, 'playPopup', 'width=1500, height=900 left=200, top=50');
}