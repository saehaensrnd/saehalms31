// 강사등록 버튼 기능처리
function Regist_to_Sitegroup(){
	
	let teacherCnt = $(".teacher:checked").length;	
	let groupCnt = $(".group:checked").length;
	
	let selectedTeacherArr = new Array();
	let selectedGroupArr = new Array();
	
	$(".teacher:checked").each(function() {		
		selectedTeacherArr.push($(this).attr('value'));
	});		
	
	$(".group:checked").each(function() {		
		selectedGroupArr.push($(this).attr('value'));
	});
	
	
	
	if(teacherCnt == 0){
		alert("등록할 강사를 선택해 주세요.");
	} else if(groupCnt == 0) { 
		alert("등록할 강사그룹을 선택해 주세요.");
	}
	else {	// 티칭센터 강사와 강사그룹 체크박스가 둘 다 선택되어있을 시
				
		$.ajax({   			
			url : '/admin/tutorGroup/insertTeacherGroupMember',			
			type : 'post',			
			data : {
				'selectedTeacherArr' : selectedTeacherArr
			  , 'selectedGroupArr' : selectedGroupArr	
			},
			success : function(data) {
				alert("등록되었습니다.");			
				location.reload();
			
			}, error : function() {
				alert("통신 실패");
				
			} // success
		}); // ajax	
					
	}

}

function Site_RegShow(){//강사그룹등록
	if( $("#TCGReg").css("display") == "none" ){		// 강사그룹등록 modal off 상태라면
		
		$("#TCGReg").css("z-index", "2");
		$("#TCGReg").show();							// 강사그룹등록 modal on
		
		$("#TCGReg").slideDown("normal", function(){			
			$("#SiteName").val("");
			$("#SiteName").focus();
		});
		
		$("#objBtnReg").css("display", "inline-block");		// 강사그룹등록 modal 등록 버튼 보이도록
		$("#objBtnMod").css("display", "none");				// 수정 버튼 hide

	}else{

		$("#TCGReg").hide();

	}
}

function Site_ModShow(teacher_group_no, teacher_group_name){//강사그룹명 수정
	$('#teacherGroupNo').val(teacher_group_no);
	
	//console.log(teacherGroupNo);

	if( $("#TCGReg").css("display") == "none" ){
		
		$("#TCGReg").css("z-index", "2");
		$("#TCGReg").show();
		
		$("#TCGReg").slideDown("normal", function(){			
			$("#SiteName").val(teacher_group_name);
			$("#SiteName").focus();
		});
		
		$("#objBtnReg").css("display", "none");
		$("#objBtnMod").css("display", "inline-block");
	}else{

		$("#TCGReg").hide();

	}
	
}

function site_RegHide(){
	
	if( $("#TCGReg").css("display") == "block" ){
		
		$("#TCGReg").hide();
	}
}




//티칭센터 체크시 해당 강사 모두 체크
function CheckAllMember(obj){
	if(obj.checked){
		let iList = $("input[name=\'"+obj.id+"\']");
		for(let i=0; i<iList.length; i++ ){
			iList[i].checked = true;
		}	
		
		//$("input[name=\'"+obj.id+"\']").attr("checked", true);
	}else{
		let iList = $("input[name=\'"+obj.id+"\']");
		for(let i=0; i<iList.length; i++ ){
			iList[i].checked = false;
		}
		
		//$("input[name=\'"+obj.id+"\']").attr("checked", false);
	}

}


//티칭센터 전체선택/전체선택해제
function CheckAll(bCheck){

	if(bCheck){
		let CpList = $('.CP');	
		for(let i=0; i<CpList.length; i++ ){
			CpList[i].checked = true;
		}	
		
		let tList = $('.teacher');	
		for(let i=0; i<tList.length; i++ ){
			tList[i].checked = true;
		}		
		
		/*		
		$(".CP").attr("checked", true);
		$(".teacher").attr("checked", true);
		*/
	}else{
		let CpList = $('.CP');	
		for(let i=0; i<CpList.length; i++ ){
			CpList[i].checked = false;
		}	
		
		let tList = $('.teacher');	
		for(let i=0; i<tList.length; i++ ){
			tList[i].checked = false;
		}			
		//$(".CP").attr("checked", false);
		//$(".teacher").attr("checked", false);
	}

}


//console.log(centerArr.length);

/*
for(let i=0; i<tempArr.length; i++) {
	str="<input type='checkbox' id='"+tempArr[i]+"' onclick='CheckAllMember(this);' class='CP'/>";
	str=str +"<b>"+tempArr[i]+"</b>";
	d.add(cnt,level,str);
	cnt++;
}


for(let i=0; i<tempArr_TC.length; i++) {
	str="<input type='checkbox' name='"+tempArr[level-1]+"' id='"+tempArr_TC[i]+"' value='"+cnt+"' class='teacher'/>";
	str=str +"<b>"+tempArr_TC[i]+"</b>";
	d.add(cnt,level+1,str);
	cnt++;
}

console.log(d);


let leftbox_item = document.getElementById('leftbox_item');
leftbox_item.innerHTML=d;
*/



/*********************     티칭센터 dTree      *********************/

//d트리사용법
//dtree.add("id", "pid", "NodeName", "url", "title", "target", "img_url");
let d= new dTree('d');
d.add(0,-1,'티칭센터','','','','../img/tree/base.gif');
var cnt=1;
var str="";
//var level=0;
var x=0;

var centerParentId = 0;

for(let i=0; i<centerArr.length; i++) {
	centerParentId = cnt;	
	//console.log("pid: " +centerParentId);
	
	str="<input type='checkbox' id='"+centerArr[i].center_name+"' onclick='CheckAllMember(this);' class='CP'/>";
	str=str +"<b>"+centerArr[i].center_name+"</b>";
	d.add(cnt, 0, str);
	
	cnt++;	
	
	
	$.ajax({   			
		url : '/admin/tutorGroup/getTeacherByCenterNo',			
		type : 'post',
		async: false,
		data : {
			'center_no' : centerArr[i].center_no
		},
		success : function(data) {
			for(k=0; k<data.length; k++){
				str="<input type='checkbox' name='"+centerArr[i].center_name+"' id='"+data[k].teacher_name+"' value='"+data[k].user_teacher_no+"' class='teacher'/>";
				str=str +"<b>"+data[k].teacher_name+"</b>";
				str=str +"<span style='margin-left: 10px;'>("+data[k].teacher_nickname+")</span>";
				//d.add(cnt,level+1,str);
				
				// 사용,중지
				let teacher_status = data[k].teacher_status == '1' ? '사용' : '중지';
				str=str +"<span style='margin-left: 5px;'>["+teacher_status+"]</span>";
				
				// 노출됨,노출안됨
				let open_status = data[k].teacher_open_status == '1' ? '노출됨' : '노출안됨';
				str=str +"<span style='margin-left: 5px;'>["+open_status+"]</span>";
				
				
				d.add(cnt, centerParentId, str);
				cnt++;
			}	 
		
		}, error : function() {
			alert("통신 실패");
			
		} // success
	}); // ajax
}
//console.log(d);

let leftbox_item = document.getElementById('leftbox_item');
leftbox_item.innerHTML=d;






/*********************     강사그룹 dTree      *********************/
let d_group=new dTree('d_group');
d_group.add(0,-1,' 강사그룹','','','','../img/tree/base.gif');
var cnt_right=1;
var str="";
//var level_right=0;
var x=0;

var groupParentId = 0;


for(let i=0; i<groupArr.length; i++) {
	groupParentId = cnt_right;	
	//console.log("pid: " +centerParentId);
	
	str="<input type='checkbox' id='"+groupArr[i].teacher_group_name+"' value='"+groupArr[i].teacher_group_no+"' onclick='CheckAllMember(this);' class='group'/>";
	str += "<b>"+groupArr[i].teacher_group_name+"</b>";
	//1) str += '<a style="color: green;" class="groupNameTag" onclick="Site_ModShow(\''+groupArr[i].teacher_group_name+'\')" >수정</a>';
	str += '<a style="color: green;" class="groupNameTag" onclick="Site_ModShow('+groupArr[i].teacher_group_no+',\''+groupArr[i].teacher_group_name+'\')" >수정</a>';
	str += "<a style='color: red;' class='groupNameTag' onclick='deleteTeacherGroup("+groupArr[i].teacher_group_no+")' >삭제</a>";
	d_group.add(cnt_right, 0, str);
	
	cnt_right++;	
	
	
	$.ajax({   			
		url : '/admin/tutorGroup/getTeacherByGroupNo',			
		type : 'post',
		async: false,
		data : {
			'teacher_group_no' : groupArr[i].teacher_group_no
		},
		success : function(data) {
			for(k=0; k<data.length; k++){
				str="<input type='hidden' name='"+groupArr[i].teacher_group_name+"' id='"+data[k].teacher_name+"' value='"+data[k].teacher_group_member_no+"' class='temp'/>";				
				str += "<b>"+data[k].teacher_name+"</b>";
				
				str += "<span style='margin-left: 10px;'>("+data[k].teacher_nickname+")</span>";
				//d.add(cnt,level+1,str);
				
				// 사용,중지
				let teacher_status2 = data[k].teacher_status == '1' ? '사용' : '중지';
				str += "<span style='margin-left: 5px;'>["+teacher_status2+"]</span>";
				
				// 노출됨,노출안됨
				let open_status2 = data[k].teacher_open_status == '1' ? '노출됨' : '노출안됨';
				str += "<span style='margin-left: 5px;'>["+open_status2+"]</span>";
				
				str += "<a style='margin-left: 10px; color: blue; cursor: pointer;' class='groupMember' onclick='deleteGroupMember("+data[k].teacher_group_member_no+")' >X</a>";
				
				d_group.add(cnt_right, groupParentId, str);
				cnt_right++;
			}	 
		
		}, error : function() {
			alert("통신 실패");
			
		} // success
	}); // ajax
}
//console.log(d);

let rightbox_item = document.getElementById('rightbox_item');
rightbox_item.innerHTML=d_group;




function deleteGroupMember(teacher_group_member_no){
	if(confirm("삭제 하시겠습니까?")){
		$.ajax({   			
			url : '/admin/tutorGroup/deleteTeacherGroupMember',			
			type : 'post',
			async: false,
			data : {
				'teacher_group_member_no' : teacher_group_member_no
			},
			success : function(data) {
				location.reload(); 
			
			}, error : function() {
				alert("통신 실패");
				
			} // success
		}); // ajax
	}	
}


function site_Reg(){
	let teacher_group_name = $('#SiteName').val().trim();
	
	if(teacher_group_name == ""){
		alert("강사 그룹명을 입력하세요.");
		return
	} 
	
	$.ajax({   			
		url : '/admin/tutorGroup/insertTeacherGroup',			
		type : 'post',		
		data : {
			'teacher_group_name' : teacher_group_name
		},
		success : function(data) {
			if(data.result){
				alert("등록되었습니다.");
				location.reload(); 
			} else{
				alert("이미 등록되어 있는 그룹 이름입니다.");
			}
		}, error : function() {
			alert("통신 실패");
			
		} // success
	}); // ajax
}

// 강사그룹명 수정
function site_Mod(){
	let teacherGroupNo = $('#teacherGroupNo').val();	
	let teacher_group_name = $('#SiteName').val().trim();
	
	if(teacher_group_name == ""){
		alert("수정할 강사 그룹명을 입력하세요.");
		return
	} 
	
	$.ajax({   			
		url : '/admin/tutorGroup/updateTeacherGroup',			
		type : 'post',		
		data : {
			'teacher_group_no' : teacherGroupNo
		  , 'teacher_group_name' : teacher_group_name
		},
		success : function(data) {
			if(data.result){
				alert("수정되었습니다.");
				location.reload(); 
			} else{
				alert("이미 등록되어 있는 그룹 이름입니다.");
			}
			
		}, error : function() {
			alert("통신 실패");
			
		} // success
	}); // ajax
	
}

// 강사그룹 삭제
function deleteTeacherGroup(teacher_group_no){
	console.log(teacher_group_no);
	
	if(confirm("강사 그룹을 삭제하시겠습니까?\n(※주의: 그룹에 등록된 모든 강사도 함께 삭제됩니다.)")){
		$.ajax({   			
			url : '/admin/tutorGroup/deleteTeacherGroup',			
			type : 'post',
			data : {
				'teacher_group_no' : teacher_group_no
			},
			success : function(data) {
				if(data.result){
					alert('삭제되었습니다.');
					location.reload(); 
				} else{
					alert(data.resultMsg);
				}
				
				
				location.reload(); 
			
			}, error : function() {
				alert("통신 실패");
				
			} // success
		}); // ajax
	}	
	
}
