window.onload = function(){
	CKEDITOR.replace( "textbook_content" );
	
	$("#study_no").change(function(){
		$.ajax({   			
			url : '/admin/textbookMng/selectStudyLevel',			
			type : 'POST',
			data : {
				study_no: $(this).val()
			},
			success : function(data) {
				$("#study_level_no").empty();
				Array.prototype.forEach.call(data, function(sl, i){
					$("#study_level_no").append("<option value='"+sl.study_level_no+"'>"+sl.level_name+"</option>");
				});
				jcf.getInstance($("#study_level_no")).refresh();
				 
			}, error : function(err) {
				console.error("selectStudyLevel", err);
			}
		}); // ajax
	});
}

// 목록 버튼 클릭
function goList(cri){
	let params = "?page="+cri.page+"&perPageNum="+cri.perPageNum+"&keyword="+cri.keyword;
	params += "&study_language="+$("#language").val()+"&study_no="+$("#study_no").val();
	window.location.href = "/admin/textbookMng"+params;
}

function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}

function textbookInsert(){
	if($("input[name='textbook_name']").val().length < 0 || $("input[name='textbook_name']").val() == ""){
		$("input[name='textbook_name']").focus();
		alert("교재명이 비어있습니다.");
		return false;
	}

	$("textarea[name='textbook_content']").val(CKEDITOR.instances["textbook_content"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#textbook_form'));

	$.ajax({   			
		url : '/admin/textbookMng/insertTextbook',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("등록되었습니다.");			
			window.location.href = "/admin/textbookMng";								
			 
		}, error : function() {
			alert("등록 실패");
		}
	}); // ajax
}

// 수정 버튼 클릭
function updateBtn(){
	if($("input[name='textbook_name']").val().length < 0 || $("input[name='textbook_name']").val() == ""){
		$("input[name='textbook_name']").focus();
		alert("교재명이 비어있습니다.");
		return false;
	}

	$("textarea[name='textbook_content']").val(CKEDITOR.instances["textbook_content"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#textbook_form'));

	$.ajax({   			
		url : '/admin/textbookMng/updateTextbook',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("수정되었습니다.");			
			window.location.href = "/admin/textbookMng";								
			 
		}, error : function() {
			alert("수정 실패");
		}
	}); // ajax	
	
}

function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}
