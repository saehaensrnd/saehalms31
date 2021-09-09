// 등록 버튼
function insertBtn(){	
	window.location.href = "/admin/classSchedule/detail";
}

function goDetail(item){	
	let param = "?schedule_class_no="+item.schedule_class_no;
	window.location.href = "/admin/classSchedule/detail"+param;
}



// 전체 선택,선택해제
$(document).on("click", ".allCheck", function(){
	let chkList = $('.rowCheck');		
	
	for(let i=0; i<chkList.length; i++ ){
		chkList[i].checked = this.checked;
	}				
});

/*
// 체크선택 삭제
function checkedDelete(){
	let cnt = $(".rowCheck:checked").length;	
	let selectedArr = new Array();
	
	$(".rowCheck:checked").each(function() {		
		selectedArr.push($(this).attr('name'));		
	});						
			
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
	} else {
		if(confirm("삭제 하시겠습니까?")){
			$.ajax({   			
				url : '/admin/holiday/checkedDelete',			
				type : 'post',			
				data: {'selectedArr' : selectedArr
				},					
				success : function(data) {	
	                    alert("삭제되었습니다.");
	                    window.location.href = "/admin/holiday";
				}, error : function() {
						alert("삭제 실패");
				} // success
			}); // ajax							
		}		
	}  		
}
*/

// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchLanguage = $("#searchLanguage").val();
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchLanguage="+searchLanguage;
	window.location.href = "/admin/classSchedule"+params;						
});

// 검색
function search(){	
	let perPageNum = $("#perPageNum option:selected").val();
	let searchLanguage = $("#searchLanguage").val();	
		
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchLanguage="+searchLanguage;
	window.location.href = "/admin/classSchedule"+params;
}





