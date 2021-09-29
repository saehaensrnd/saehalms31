// 재생 버튼
function playBtn(){
	let textbook_topic_no = $('#textbook_topic_no').val().trim();	
		
	$.ajax({
		type: "POST",
		url: "/admin/onlineBook/playOnlineBook",
		data : {
			textbook_topic_no: textbook_topic_no
		},
		success: function(data){
			var playUrl = "https://saeha.s3.ap-northeast-2.amazonaws.com/textbook_audio/";
			playUrl += data.audio_file;
			
			var audio = new Audio(playUrl);
			audio.play();
		},
		error: function(err){
			console.error("playOnlineBook", err);
		}
	});
}


// topic change 이벤트 발생 시 - online book 변경
function topicChange(e) {	
	let topicNo = e.value;	
	if(topicNo == "") return false;
	
	$.ajax({
		type: "POST",
		url: "/reportPopup/getTopicPdf",		
		data : {
			textbook_topic_no: topicNo
		},
		success: function(data){
			//console.log(data.pdf_file);						
			let onlinebookDetail = $('#onlinebook_detail');
			if(data.pdf_file != ""){
				onlinebookDetail.attr('src', 'https://saeha.s3.ap-northeast-2.amazonaws.com/textbook_pdf/'+data.pdf_file);	
			} else {
				onlinebookDetail.attr('src', '');
			}
			//let onlinebookDetail = $('#onlinebook_detail');	
			//onlinebookDetail.attr('src', '/admin/onlineBookDetail?textbook_topic_no='+topicNo);			
		},
		error: function(){
			console.log("통신 실패");
		}
	});
}