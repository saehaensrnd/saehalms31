// 등록 버튼
function insertBtn(){
	var formData = new FormData(document.querySelector('#textbookTopic_form'));
	formData.append("textbook_no", $("#textbook").val());
	
	$.ajax({
		type: "POST",
		url: "/admin/textbookTopic/insertTextbookTopic",
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success: function(){
			alert("교재 토픽이 등록되었습니다.");
			window.location.href = "/admin/textbookTopic?study_language="+$("#language").val()+"&study_no="+$("#study").val()+"&textbook_no="+$("#textbook").val();
		},
		error: function(err){
			console.error("insertTextbookTopic", err);
		}
	});
}

// 수정 버튼
function updateBtn(textbook_topic_no){
	console.log(document.querySelector('#update_form'+textbook_topic_no));
	
	var formData = new FormData(document.querySelector('#update_form'+textbook_topic_no));
	formData.append("textbook_topic_no", textbook_topic_no);
	formData.append("unit", $("#unit"+textbook_topic_no).val());
	formData.append("other_url", $("#other_url"+textbook_topic_no).val());
	
	$.ajax({
		type: "POST",
		url: "/admin/textbookTopic/updateTextbookTopic",
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success: function(){
			alert("교재 토픽이 수정되었습니다.");
			window.location.href = "/admin/textbookTopic?study_language="+$("#language").val()+"&study_no="+$("#study").val()+"&textbook_no="+$("#textbook").val();
		},
		error: function(err){
			console.error("updateTextbookTopic", err);
		}
	});
}

// 삭제 버튼
function deleteBtn(textbook_topic_no){
	if(confirm("정말 삭제하시겠습니까?")){
		$.ajax({
			type: "POST",
			url: "/admin/textbookTopic/deleteTextbookTopic",
			data: {
				textbook_topic_no: textbook_topic_no
			},
			success: function(){
				alert("교재 토픽이 삭제되었습니다.");
				window.location.href = "/admin/textbookTopic?study_language="+$("#language").val()+"&study_no="+$("#study").val()+"&textbook_no="+$("#textbook").val();
			},
			error: function(err){
				console.error("deleteTextbookTopic", err);
			}
		});
	}
}