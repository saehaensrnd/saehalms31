window.onload = function(){
	CKEDITOR.replace( "study_target_explain" );
	CKEDITOR.replace( "study_purpose" );
	CKEDITOR.replace( "study_point" );
}

// 목록 버튼 클릭
function goList(cri){
	let params = "?page="+cri.page+"&perPageNum="+cri.perPageNum+"&keyword="+cri.keyword;
	window.location.href = "/admin/studyMng"+params;
}

function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}

function studyInsert(){
	if($("input[name='study_name']").val().length < 0 || $("input[name='study_name']").val() == ""){
		$("input[name='study_name']").focus();
		alert("교육과정명이 비어있습니다.");
		return false;
	}

	$("textarea[name='study_target_explain']").val(CKEDITOR.instances["study_target_explain"].document.getBody().getHtml());
	$("textarea[name='study_purpose']").val(CKEDITOR.instances["study_purpose"].document.getBody().getHtml());
	$("textarea[name='study_point']").val(CKEDITOR.instances["study_point"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#study_form'));
	
	var study_target = "";
	var arr_target = $("input[name='arr_target']:checked");
	if(arr_target.length > 0){
		arr_target.each(function(index){
			study_target += $(this).val();
			if(arr_target.length-1 != index){
				study_target += "/";
			}
		});
		formData.append("study_target", study_target);
	}
	
	var study_not_used_site = "";
	var arr_site = $("input[name='arr_site']:checked");
	if(arr_site.length > 0){
		arr_site.each(function(index){
			study_not_used_site += $(this).val();
			if(arr_site.length-1 != index){
				study_not_used_site += "/";
			}
		});
		formData.append("study_not_used_site", study_not_used_site);
	}
	
	var study_type = "";
	var arr_type = $("input[name='arr_type']:checked");
	if(arr_type.length > 0){
		arr_type.each(function(index){
			study_type += $(this).val();
			if(arr_type.length-1 != index){
				study_type += "/";
			}
		});
		formData.append("study_type", study_type);
	}

	$.ajax({   			
		url : '/admin/studyMng/insertStudy',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("등록되었습니다.");			
			window.location.href = "/admin/studyMng";								
			 
		}, error : function() {
			alert("등록 실패");
		}
	}); // ajax
}

// 수정 버튼 클릭
function updateBtn(){
	if($("input[name='study_name']").val().length < 0 || $("input[name='study_name']").val() == ""){
		$("input[name='study_name']").focus();
		alert("교육과정명이 비어있습니다.");
		return false;
	}

	$("textarea[name='study_target_explain']").val(CKEDITOR.instances["study_target_explain"].document.getBody().getHtml());
	$("textarea[name='study_purpose']").val(CKEDITOR.instances["study_purpose"].document.getBody().getHtml());
	$("textarea[name='study_point']").val(CKEDITOR.instances["study_point"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#study_form'));
	
	var study_target = "";
	var arr_target = $("input[name='arr_target']:checked");
	if(arr_target.length > 0){
		arr_target.each(function(index){
			study_target += $(this).val();
			if(arr_target.length-1 != index){
				study_target += "/";
			}
		});
		formData.append("study_target", study_target);
	}
	
	var study_not_used_site = "";
	var arr_site = $("input[name='arr_site']:checked");
	if(arr_site.length > 0){
		arr_site.each(function(index){
			study_not_used_site += $(this).val();
			if(arr_site.length-1 != index){
				study_not_used_site += "/";
			}
		});
		formData.append("study_not_used_site", study_not_used_site);
	}
	
	var study_type = "";
	var arr_type = $("input[name='arr_type']:checked");
	if(arr_type.length > 0){
		arr_type.each(function(index){
			study_type += $(this).val();
			if(arr_type.length-1 != index){
				study_type += "/";
			}
		});
		formData.append("study_type", study_type);
	}
	
	$.ajax({   			
		url : '/admin/studyMng/updateStudy',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("수정되었습니다.");
			window.location.href = "/admin/studyMng";					
			 
		}, error : function() {
			alert("수정 실패");
		}
	}); // ajax		
	
}


