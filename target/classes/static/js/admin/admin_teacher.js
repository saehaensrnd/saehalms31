// 등록 버튼
function insertBtn(cri){
	let params = "?page="+cri.page+"&perPageNum="+cri.perPageNum;
	window.location.href = "/admin/teacher/detail"+params;
}

function checkedDelete(){
	let cnt = $(".rowCheck:checked").length;	
	let selectedArr = new Array();
	
	$(".rowCheck:checked").each(function() {		
		selectedArr.push($(this).attr('name'));
		//selectedUserTeacherNoArr.push($(this).attr('name').split('/')[0]);
		//selectedUserNoArr.push($(this).attr('name').split('/')[1]);		
	});	
					
			
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
	} else {			
		if(confirm("삭제 하시겠습니까?")){
			$.ajax({   			
				url : '/admin/teacher/checkedDelete',			
				type : 'post',			
				data: {'selectedArr' : selectedArr
				},					
				success : function(data) {	
	                    alert("삭제되었습니다.");
	                    window.location.href = "/admin/teacher";
				}, error : function() {
						alert("삭제 실패");
				} // success
			}); // ajax						
		}	
		
		
	}  	
	
}



// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let perPageNum = $("#perPageNum option:selected").val();
	let searchCenter = $("#searchCenter").val();	
	let searchTeacherNo = $("#searchTeacherNo").val();
	let searchNation = $("#searchNation").val();		
	let searchClassType = $("#searchClassType").val();		
	let searchStatus = $("#searchStatus").val();
	let searchOpenStatus = $("#searchOpenStatus").val();
	let searchTeacherName = $("#searchTeacherName").val();
	
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchCenter="+searchCenter+"&searchTeacherNo="+searchTeacherNo
	           +"&searchNation="+searchNation+"&searchClassType="+searchClassType+"&searchStatus="+searchStatus
               +"&searchOpenStatus="+searchOpenStatus+"&searchTeacherName="+searchTeacherName;
	window.location.href = "/admin/teacher"+params;						
});


// 검색
function search(cri){	
	let perPageNum = $("#perPageNum option:selected").val();
	let searchCenter = $("#searchCenter").val();	
	let searchTeacherNo = $("#searchTeacherNo").val();
	let searchNation = $("#searchNation").val();		
	let searchClassType = $("#searchClassType").val();		
	let searchStatus = $("#searchStatus").val();
	let searchOpenStatus = $("#searchOpenStatus").val();
	let searchTeacherName = $("#searchTeacherName").val();
		
	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchCenter="+searchCenter+"&searchTeacherNo="+searchTeacherNo
	           +"&searchNation="+searchNation+"&searchClassType="+searchClassType+"&searchStatus="+searchStatus
               +"&searchOpenStatus="+searchOpenStatus+"&searchTeacherName="+searchTeacherName;
	window.location.href = "/admin/teacher"+params;
}


// 전체 선택,선택해제
$(document).on("click", ".allCheck", function(){
	let chkList = $('.rowCheck');		
	
	for(let i=0; i<chkList.length; i++ ){
		chkList[i].checked = this.checked;
	}				
});


// 체크선택 삭제
function deleteBtn(){
	let cnt = $(".rowCheck:checked").length;	
	let selectedArr = new Array();
	
	$(".rowCheck:checked").each(function() {		
		selectedArr.push($(this).attr('name'));		
	});						
			
	if(cnt == 0){
		alert("선택된 내용이 없습니다.");
	} else {			
		console.log(selectedArr);			
	
		
		if(confirm("삭제 하시겠습니까?")){
			$.ajax({   			
				url : '/admin/teacher/checkedItemDelete',			
				type : 'post',			
				data: {'selectedArr' : selectedArr},					
				success : function(data) {				
					if(data != 1) {
	                    alert("삭제 실패");
	                }
	                else{
	                    alert("삭제 성공");
	                    
	                    window.location.href = "/admin/teacher";
	                }
				}, error : function() {
						alert("서버통신 오류");
				} // success
			}); // ajax						
		}		
	}  		
}


function openTimeTable(item){
	console.log(item.user_teacher_no);
	console.log(item.center_no);
	
	window.open("/admin/teacher/timetable?searchCenter="+item.center_no+"&searchTeacher="+item.user_teacher_no, 'timetablePopup', 'width=800, height=840 left=550, top=70');	
}


