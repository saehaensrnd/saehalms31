// 재생 버튼
function playBtn(textbook_topic_no){	
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