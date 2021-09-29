window.onload = function(){
	CKEDITOR.replace( "class_student_content" );
}

function selfClose(){
	self.close();
}

function makeProductSel(site_no, product_language, product_type, product_personnel){
	$("#start_date").val("");
	
	$.ajax({
		url: '/userDetail/getProductSel',
		type: 'post',
		data: {
			'site_no': site_no,
			'product_language': product_language,
			'product_type': product_type,
			'product_personnel': product_personnel
		},
		success : function(data) {
			var productSel = document.querySelector("#product_no");
			productSel.options.length = 0;

			console.log(data.length);
			productSel.options[0] = new Option("-- 선택 --", "");
			if(data.length > 0){
				for(var i=1; i<=data.length; i++){
					console.log(data[i-1].product_name);
					productSel.options[i] = new Option(data[i-1].product_name, data[i-1].product_no);
				}
			}
			
			jcf.replaceAll();
		},
		error : function() {
			alert("서버 에러");
		}
	});
}

function makeClassSel(site_no, product_language, product_type, product_personnel, start_date){
	if(product_personnel != "1:1"){
		$.ajax({
			url: '/userDetail/getClassSel',
			type: 'post',
			data: {
				'site_no': site_no,
				'product_language': product_language,
				'product_type': product_type,
				'product_personnel': product_personnel,
				'start_date': start_date
			},
			success: function(data){
				var classSel = document.querySelector("#class_no");
				classSel.options.length = 0;

				classSel.options[0] = new Option("-- 선택 --", "");
				if(data.length > 0){
					for(var i=1; i<=data.length; i++){
						console.log(data[i-1].product_name);
						classSel.options[i] = new Option(data[i-1].product_name, data[i-1].class_no);
					}
				}
				
				jcf.replaceAll();
			},
			error : function() {
				alert("서버 에러");
			}
		});
	}
}

function insertClass(){
	if($("#start_date").val() == "" || $("#start_date").val() == null){
		$("#start_date").focus();
		alert("수업시작일을 선택하세요.");
		return false;
	}
	if($("#product_no").val() == "" || $("#product_no").val() == null){
		$("#start_date").focus();
		alert("수강상품을 선택하세요.");
		return false;
	}
	
	$("textarea[name='class_student_content'").val(CKEDITOR.instances["class_student_content"].document.getBody().getHtml());
	
	var formData = new FormData(document.querySelector('#class_form'));
	
	$.ajax({
		url : '/userDetail/insertClass',			
		type : 'post',
		data : formData,
		processData: false, // ajax로 formData 전송시 필요
		contentType: false, // ajax로 formData 전송시 필요
		success : function(result) {
			alert("등록되었습니다.");
			opener.parent.location.reload();
			self.close();
		}, error : function() {
			alert("등록 실패");
		}
	}); // ajax
}