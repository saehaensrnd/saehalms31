// 등록 버튼
function insertBtn(){
	$.ajax({
		type: "POST",
		url: "/admin/studyLevel/insertStudyLevel",
		data: {
			study_no: $("#study").val(),
			level_name: $("#level_name").val(),
			level_eng_name: $("#level_eng_name").val()
		},
		success: function(){
			alert("수강과정 레벨이 등록되었습니다.");
			window.location.href = "/admin/studyLevel?study_language="+$("#language").val()+"&study_level_status="+$("#study_level_status").val()+"&study_no="+$("#study").val();
		},
		error: function(err){
			console.error("insertStudyLevel", err);
		}
	});
}

// 수정 버튼
function updateBtn(study_level_no){
	$.ajax({
		type: "POST",
		url: "/admin/studyLevel/updateStudyLevel",
		data: {
			study_level_no: study_level_no,
			level: $("#level"+study_level_no).val(),
			level_name: $("#level_name"+study_level_no).val(),
			level_eng_name: $("#level_eng_name"+study_level_no).val(),
			study_level_status: $("input[name='study_level_status"+study_level_no+"']:checked").val()
		},
		success: function(){
			alert("수강과정 레벨이 수정되었습니다.");
			window.location.href = "/admin/studyLevel?study_language="+$("#language").val()+"&study_level_status="+$("#study_level_status").val()+"&study_no="+$("#study").val();
		},
		error: function(err){
			console.error("updateStudyLevel", err);
		}
	});
}

// 삭제 버튼
function deleteBtn(study_level_no){
	if(confirm("정말 삭제하시겠습니까?")){
		$.ajax({
			type: "POST",
			url: "/admin/studyLevel/deleteStudyLevel",
			data: {
				study_level_no: study_level_no
			},
			success: function(){
				alert("수강과정 레벨이 삭제되었습니다.");
				window.location.href = "/admin/studyLevel?study_language="+$("#language").val()+"&study_level_status="+$("#study_level_status").val()+"&study_no="+$("#study").val();
			},
			error: function(err){
				console.error("deleteStudyLevel", err);
			}
		});
	}
}