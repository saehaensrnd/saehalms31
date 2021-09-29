window.onload = function(){
	CKEDITOR.replace( "product_content" );
	CKEDITOR.replace( "product_eng_content" );
	
	idCheck = false;
}

// 목록 버튼 클릭
function goList(cri){
	let params = "?page="+cri.page+"&perPageNum="+cri.perPageNum+"&keyword="+cri.keyword;
	window.location.href = "/admin/productMng"+params;
}

function onlyEnglish(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^A-Za-z]/g, '');
	}
}

function removeChar(e){
	var e = e || window.event, keyCode = (e.which) ? e.which : e.keyCode;

	if (keyCode == 8 || keyCode == 46 || keyCode == 37 || keyCode == 39) {
		return;
	} else {
		e.target.value = e.target.value.replace(/[^0-9]/g, '');
	}
}

function productInsert(){
	if($("input[name='product_name']").val().length < 0 || $("input[name='product_name']").val() == ""){
		$("input[name='product_name']").focus();
		alert("상품명이 비어있습니다.");
		return false;
	}

	$("textarea[name='product_content'").val(CKEDITOR.instances["product_content"].document.getBody().getHtml());
	$("textarea[name='product_eng_content'").val(CKEDITOR.instances["product_eng_content"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#product_form'));
	
	$.ajax({   			
		url : '/admin/productMng/insertProduct',
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("등록되었습니다.");			
			window.location.href = "/admin/productMng";
			 
		}, error : function() {
			alert("등록 실패");
		}
	}); // ajax
}

// 수정 버튼 클릭
function updateBtn(){
	if($("input[name='product_name']").val().length < 0 || $("input[name='product_name']").val() == ""){
		$("input[name='product_name']").focus();
		alert("상품명이 비어있습니다.");
		return false;
	}

	$("textarea[name='product_content'").val(CKEDITOR.instances["product_content"].document.getBody().getHtml());
	$("textarea[name='product_eng_content'").val(CKEDITOR.instances["product_eng_content"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#product_form'));
	
	$.ajax({   			
		url : '/admin/productMng/updateProduct',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function() {						
			alert("수정되었습니다.");
			window.location.href = "/admin/productMng";					
			 
		}, error : function() {
			alert("수정 실패");
		}
	}); // ajax		
	
}


