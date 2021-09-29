function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}

function textbookSelect(sel){
	$.ajax({
		url : '/userDetail/textbookSelect',
		type : 'post',
		data : {
			'textbook_no': $(sel).val()
		},
		success : function(data) {
			var textbookSel = document.querySelector("#textbook_topic_no");
			textbookSel.options.length = 0;

			console.log(data.length);
			textbookSel.options[0] = new Option("-- 선택 --", 0);
			if(data.length > 0){
				for(var i=1; i<=data.length; i++){
					textbookSel.options[i] = new Option(data[i-1].topic_name, data[i-1].textbook_topic_no);
				}
			}
			
//				jcf.replaceAll();
		}, error : function() {
			alert("studySel 실패");
		}
	}); // ajax
}

function change(){
	var formData = new FormData(document.querySelector('#changeProgressForm'));
	
	$.ajax({
		url : '/userDetail/progressChange',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {
			alert("진도가 변경되었습니다.");
			opener.parent.location.reload();
			self.close();
		}, error : function() {
			alert("서버 에러");
		}
	}); // ajax
}