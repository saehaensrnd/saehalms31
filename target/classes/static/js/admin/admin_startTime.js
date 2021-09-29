// 등록 버튼
function insertBtn(){
	window.open("/admin/startTime/detail", 'startTimePopup', 'width=800, height=840 left=550, top=70');
}

// 시작시간 수정
function openStartTime(item){
	window.open("/admin/startTime/detail?study_start_time_no="+item.study_start_time_no, 'startTimePopup', 'width=800, height=840 left=550, top=70');
}



// 전체 선택,선택해제
$(document).on("click", ".allCheck", function(){
	let chkList = $('.rowCheck');		
	
	for(let i=0; i<chkList.length; i++ ){
		chkList[i].checked = this.checked;
	}				
});


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
				url : '/admin/startTime/checkedDelete',			
				type : 'post',			
				data: {'selectedArr' : selectedArr
				},					
				success : function(data) {	
	                    alert("삭제되었습니다.");
	                    window.location.href = "/admin/startTime";
				}, error : function() {
						alert("삭제 실패");
				} // success
			}); // ajax							
		}		
	}  		
}


function openTimeTable(item){
	window.open("/admin/teacher/timetable?searchCenter="+item.center_no+"&searchTeacher="+item.user_teacher_no, 'timetablePopup', 'width=800, height=840 left=550, top=70');	
}


// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchLanguage = $("#searchLanguage").val();	
	let searchRunningTime = $("#searchRunningTime").val();	
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchLanguage="+searchLanguage+"&searchRunningTime="+searchRunningTime
	window.location.href = "/admin/startTime"+params;						
});


// 검색
function search(){	
	let perPageNum = $("#perPageNum option:selected").val();
	let searchLanguage = $("#searchLanguage").val();	
	let searchRunningTime = $("#searchRunningTime").val();
		
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchLanguage="+searchLanguage+"&searchRunningTime="+searchRunningTime
	window.location.href = "/admin/startTime"+params;
}





