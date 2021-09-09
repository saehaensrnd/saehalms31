// 등록 버튼
function insertBtn(){
	var formData = new FormData(document.querySelector('#textbook_page_form'));

	console.log(formData.get("page_name"));
	console.log(formData.get("file_url").size);
	
	if(formData.get("page_name") == "" || formData.get("page_name") == null){
		alert("페이지 이름을 입력하세요");
		$("#page_name").focus();
	}
	else if(formData.get("file_url").size == 0){
		alert("페이지 파일(이미지)을 선택하세요");
	}
	else{
		$.ajax({
			type: "POST",
			url: "/admin/textbookTopic/pageDetail/insertTextbookPage",
			data : formData,
			processData: false, // ajax로 formData 전송시 필요
			contentType: false, // ajax로 formData 전송시 필요
			success: function(){
				alert("페이지가 등록되었습니다.");
				window.location.reload();
			},
			error: function(err){
				console.error("insertTextbookPage", err);
			}
		});
	}
}

// 수정 버튼
function updateBtn(textbook_page_no){
	console.log(document.querySelector('#update_form'+textbook_page_no));
	
	var formData = new FormData(document.querySelector('#update_form'+textbook_page_no));
	formData.append("textbook_page_no", textbook_page_no);
	formData.append("page", $("#page"+textbook_page_no).val());
	formData.append("page_name", $("#page_name"+textbook_page_no).val());
	
	$.ajax({
		type: "POST",
		url: "/admin/textbookTopic/pageDetail/updateTextbookPage",
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success: function(){
			alert("페이지가 수정되었습니다.");
			window.location.reload();
		},
		error: function(err){
			console.error("updateTextbookPage", err);
		}
	});
}

// 삭제 버튼
function deleteBtn(textbook_page_no){
	if(confirm("정말 삭제하시겠습니까?")){
		$.ajax({
			type: "POST",
			url: "/admin/textbookTopic/pageDetail/deleteTextbookPage",
			data: {
				textbook_page_no: textbook_page_no
			},
			success: function(){
				alert("페이지가 삭제되었습니다.");
				window.location.reload();
			},
			error: function(err){
				console.error("deleteTextbookPage", err);
			}
		});
	}
}