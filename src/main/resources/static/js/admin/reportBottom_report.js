function leveltest_registerBtn(){
	let present;					// 수강신청시 레벨테스트 정보 필수
				   	
   	if($('#present_chk').is(':checked') === true){
		present= '1';		
	} else {
		present = '2';
	}
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#reportForm");
	let data = new FormData(form);
	
	data.append("present", present);
	
	
	$.ajax({   			
		url : '/reportPopup/report/levelRegister',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("저장되었습니다.");
			location.reload();
			//window.location.href = "/admin/center";
		}, error : function() {
			alert("저장 실패");
			//console.log("실패");
		} // success
	}); // ajax

}

function registerBtn(){
	if($('#class_score_no').val() == '0'){
		alert("선택된 학생이 없습니다.");
		return false;
	}
	
	let present;					// 수강신청시 레벨테스트 정보 필수
				   	
   	if($('#present_chk').is(':checked') === true){
		present= '1';		
	} else {
		present = '2';
	}	
	
	//console.log(class_score_no);	
	//console.log(present);
	
	
	// 유효성 검사 통과 	
	let form = document.querySelector("#reportForm");
	let data = new FormData(form);
	
	data.append("present", present);
	
	
	$.ajax({   			
		url : '/reportPopup/report/register',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {							
			alert("저장되었습니다.");
			location.reload();
			//window.location.href = "/admin/center";
		}, error : function() {
			alert("저장 실패");
			//console.log("실패");
		} // success
	}); // ajax
	
}



function classScoreNoChange(e) {	
	let classScoreNo = e.value;
	
	if(classScoreNo == '0'){
		classScoreNo = class_score_no;
	}
	
	window.location.href = "/reportPopup/report?flag=class&class_score_no=" + classScoreNo;
}



function studyChange(e) {
	console.log(e.value);
		
	let studyNo = e.value;

	// level 셀렉트박스 초기화
	let levelSelect = document.getElementById("study_level_no");
	for (i = 0; i < levelSelect.options.length; i++) {
		levelSelect.options[i] = null;
	}

	// 셀렉트박스 세팅
	let studyLevelArr;

	$.ajax({
		url: '/reportPopup/report/selectStudyLevelCombo',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"study_no": studyNo
		},
		success: function(item) {
			studyLevelArr = item;
			console.log(item);
		}, error: function() {
			//console.log("실패");
		} 
	});

	levelSelect.options[0] = new Option("-- 선택 --", "");

	if (studyLevelArr.length > 0) {
		for (i = 1; i <= studyLevelArr.length; i++) {
			levelSelect.options[i] = new Option(studyLevelArr[i - 1].level_name, studyLevelArr[i - 1].study_level_no);		// text, value
		}
	}
	
	jcf.replaceAll();
	
}






function completeBtn(classLogNo){
	//console.log(class_log_no);		
	
	if(confirm("Do you want to completed class?")){
	
		$.ajax({   			
			url : '/reportPopup/report/complete',			
			type : 'post',
			data : {
				'class_log_no' : classLogNo
			},				
			success : function(data) {
				if(data.completeFlag == '1'){				
					alert("Successful");	
				} else {
					alert("Input the Report - Register first !");
				}
				location.reload();
				
			}, error : function() {
				alert("통신 실패");
				//console.log("실패");
			} // success
		}); // ajax
	}
}



function leveltest_completeBtn(leveltestNo){
	//console.log(leveltest_no);		
	
	if(confirm("Do you want to completed class?")){
	
		$.ajax({   			
			url : '/reportPopup/report/levelTestComplete',			
			type : 'post',
			data : {
				'leveltest_no' : leveltestNo
			},				
			success : function(data) {
				if(data.completeFlag == '1'){				
					alert("Successful");	
				} else {
					alert("Input the Report - Register first !");
				}
				location.reload();
				
			}, error : function() {
				alert("통신 실패");
				//console.log("실패");
			} // success
		}); // ajax
	}
}



function makeUpBtn(class_score_no, class_log_no){
	//console.log(class_score_no);	
	
	if(confirm("makeup?")){
	
		$.ajax({   			
			url : '/reportPopup/report/makeup',			
			type : 'post',
			data : {
				'class_score_no' : class_score_no
			  , 'class_log_no': class_log_no
			},				
			success : function(data) {
				alert("Successful");
				location.reload();
				
			}, error : function() {
				alert("통신 실패");
				//console.log("실패");
			} // success
		}); // ajax
	}
	
}

