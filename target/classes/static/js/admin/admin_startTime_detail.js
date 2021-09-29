function allCheck(){
	let chkList = $(".check");	
	let checkedCnt = 0;
	
	for(let i=0; i<chkList.length; i++ ){			
		if(chkList[i].checked === true){
			checkedCnt++;
		}		
	}
	
	if(checkedCnt == chkList.length) {	// 전체선택중이라면 전체해제
		for(let i=0; i<chkList.length; i++ ){
			chkList[i].checked = false;
		}
	} else {	
		for(let i=0; i<chkList.length; i++ ){
			chkList[i].checked = true;
		}
	}
}


function insertBtn(){
	let language_type = $('#language_type').val();
	let running_time = $('#running_time').val();
	
	if(language_type == "" || running_time == ""){
		alert("언어와 런닝타임을 선택하세요.");
		return false;		
	}
	
	let cnt = $(".check:checked").length;	
	let selectedArr = new Array();
	
	$(".check:checked").each(function() {		
		selectedArr.push($(this).attr('name'));			
	});	
	
	
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
	} else {			
		if(confirm("저장 하시겠습니까?")){
			$.ajax({   			
				url : '/admin/startTime/insert',			
				type : 'post',			
				data: { 'language_type' : language_type
				      , 'running_time' : running_time
					  , 'selectedArr' : selectedArr
				},					
				success : function(data) {	
	                    alert("저장되었습니다.");
	                    window.close();
						opener.parent.location.reload();
				}, error : function() {
						alert("저장 실패");
				} // success
			}); // ajax						
		}			
	} 
	
}



function updateBtn(){
	let study_start_time_no = $('#study_start_time_no').val();	
	
	let cnt = $(".check:checked").length;	
	let selectedArr = new Array();
	
	$(".check:checked").each(function() {		
		selectedArr.push($(this).attr('name'));			
	});		
	
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
	} else {			
		if(confirm("저장 하시겠습니까?")){
			$.ajax({   			
				url : '/admin/startTime/update',			
				type : 'post',			
				data: { 'study_start_time_no' : study_start_time_no
					  , 'selectedArr' : selectedArr
				},					
				success : function(data) {	
	                    alert("저장되었습니다.");
	                    window.close();
						opener.parent.location.reload();
				}, error : function() {
						alert("저장 실패");
				} // success
			}); // ajax						
		}			
	} 
	
}










