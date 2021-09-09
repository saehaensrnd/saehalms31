window.onload = function(){
	CKEDITOR.replace( "postpone_feedback" );
}

// 목록 버튼 클릭
function goList(cri){
	let params = "?page="+cri.page+"&perPageNum="+cri.perPageNum+"&keyword="+cri.keyword;
	window.location.href = "/admin/postponeMng"+params;
}

function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}

// 수정 버튼 클릭
function updateBtn(){
	$("textarea[name='postpone_feedback'").val(CKEDITOR.instances["postpone_feedback"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#postpone_form'));
	
	$.ajax({   			
		url : '/admin/postponeMng/updatePostpone',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("처리되었습니다.");
			window.location.href = "/admin/postponeMng";
		}, error : function() {
			alert("처리 실패");
		}
	}); // ajax		
	
}


