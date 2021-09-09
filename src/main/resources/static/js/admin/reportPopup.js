// 레벨테스트 제외
if(textbookParam != null){
	if(textbookParam.textbook_topic_no != 0 && textbookParam.pdf_file != "") {
		$('#onlinebook_detail').attr('src', 'https://saeha.s3.ap-northeast-2.amazonaws.com/textbook_pdf/'+textbookParam.pdf_file);
		
		console.log(textbookParam.pdf_file);
	}	
}


// textbook change 이벤트 발생 시 - Topic 콤보박스 셀렉트박스 Setting
function textbookChange2(e) {	
	let changeValue = e.value;

	// topic 셀렉트박스 초기화
	let topicSelect = document.getElementById("textbook_topic_no2");	
	topicSelect.options.length = 0;	
	topicSelect.options[0] = new Option("-- 선택 --", "");

	// 셀렉트박스 세팅
	let topicArr;

	$.ajax({
		url: '/reportPopup/getTopicCombo',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"textbook_no": changeValue
		},
		success: function(item) {
			topicArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});
	

	if (topicArr.length > 0) {
		for (i = 1; i <= topicArr.length; i++) {
			topicSelect.options[i] = new Option(topicArr[i - 1].topic_name, topicArr[i - 1].textbook_topic_no);		// text, value
		}
	}

	jcf.replaceAll();
}


// topic change 이벤트 발생 시 - online book 변경
function topicChange2(e) {	
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



// 재생 버튼
function playBtn(){
	let textbook_topic_no = $('#textbook_topic_no2').val().trim();
	
	if(textbook_topic_no != ""){
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
}






