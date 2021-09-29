function langSel(sel){
	
	console.log($(sel).val());
	if($(sel).val() != 0){
		$.ajax({
			url : '/student/enrolment/langSel',			
			type : 'post',
			data : {
				'site_no': $("#site_no").val(),
				'language': $(sel).val()
			},
			success : function(data) {

				console.log(data);
				selectReset(document.querySelector("#study_no"));
				
				if(data.scheduleClassSite){
					console.log("true");
					document.querySelector("#start_date").innerText = data.scheduleClassSite.class_start_date;

					var studySel = document.querySelector("#study_no");
					studySel.options.length = 0;
					studySel.options[0] = new Option("-- 선택 --", 0);
					if(data.study.length > 0){
						for(var i=1; i<=data.study.length; i++){
							studySel.options[i] = new Option(data.study[i-1].study_name, data.study[i-1].study_no);
						}
					}
				}
				else{
					alert("수강 신청일이 지났습니다.");
					
					Array.prototype.forEach.call(document.querySelectorAll("input[name='product_no']"), function(node){
						node.checked = false;
					});
					
					$("#site_language").find("option:eq(0)").prop("selected", true);
					document.querySelector("#start_date").innerText = "";
					selectReset(document.querySelector("#study_no"));
					document.querySelector("#textbookSel").innerText = "";
					selectReset(document.querySelector("#textbook_no"));
					selectReset(document.querySelector("#user_teacher_no"));
					selectReset(document.querySelector("#timeSel"));

					p_price = 0;
					t_price = 0;
					document.querySelector("#product_price").innerText = "-";
					document.querySelector("#textbook_price").innerText = "-";
					document.querySelector("#total_price").innerText = "-";
					
					jcf.replaceAll();
				}
			}, error : function() {
				alert("studySel 실패");
			}
		}); // ajax
	}
	else{
		Array.prototype.forEach.call(document.querySelectorAll("input[name='product_no']"), function(node){
			node.checked = false;
		});
		
		$("#site_language").find("option:eq(0)").prop("selected", true);
		document.querySelector("#start_date").innerText = "";
		selectReset(document.querySelector("#study_no"));
		document.querySelector("#textbookSel").innerText = "";
		selectReset(document.querySelector("#textbook_no"));
		selectReset(document.querySelector("#user_teacher_no"));
		selectReset(document.querySelector("#timeSel"));

		p_price = 0;
		t_price = 0;
		document.querySelector("#product_price").innerText = "-";
		document.querySelector("#textbook_price").innerText = "-";
		document.querySelector("#total_price").innerText = "-";
		
		jcf.replaceAll();
	}
}

function studySel(sel){
	selectReset(document.querySelector("#textbook_no"));
	document.querySelector("#textbookSel").innerText = "";
	jcf.replaceAll();

	document.querySelector("#textbook_price").innerText = "-";
	t_price = 0;
	totalMoney(true);
	
	if($(sel).val() != 0){
		$.ajax({
			url : '/userDetail/studySelect',			
			type : 'post',
			data : {
				'study_no': $(sel).val()
			},
			success : function(data) {
				var textbookSel = document.querySelector("#textbook_no");
				textbookSel.options.length = 0;

				console.log(data.length);
				textbookSel.options[0] = new Option("-- 선택 --", 0);
				if(data.length > 0){
					for(var i=1; i<=data.length; i++){
						textbookSel.options[i] = new Option(data[i-1].textbook_name, data[i-1].textbook_no);
					}
				}
			}, error : function() {
				alert("studySel 실패");
			}
		}); // ajax
	}
}

function textbookSel(sel){
	if($(sel).val() != 0){
		$.ajax({
			url : '/student/enrolment/textbookSelect',			
			type : 'post',
			data : {
				'textbook_no': $(sel).val()
			},
			success : function(textbook_price) {
				document.querySelector("#textbook_price").innerText = textbook_price.toLocaleString('ko-KR');
				t_price = textbook_price;
				totalMoney(true);
			}, error : function() {
				alert("textbookSel 실패");
			}
		}); // ajax
		
		document.querySelector("#textbookSel").innerText = sel[sel.selectedIndex].innerText;
	}
}

function buyTextbook(chk){
	if(chk.checked){
		document.querySelector("#textbook_price").innerText = "-";
		totalMoney(false);
	}
	else{
		document.querySelector("#textbook_price").innerText = t_price.toLocaleString('ko-KR');
		totalMoney(true);
	}
}

function searchTeacher(radio){
	console.log("searchTeacher");
	if(document.querySelector("#start_date").innerText == ""){
		alert("수강 언어를 선택해주세요.");
		radio.checked = false;
		return;
	}
	else{
		$.ajax({
			url : '/student/enrolment/searchTeacher',			
			type : 'post',
			data : {
				'site_no': $("#site_no").val(),
				'language': $("#site_language").val(),
				'start_date': document.querySelector("#start_date").innerText,
				'product_no': document.querySelector("input[name='product_no']:checked").value
			},
			success : function(data) {
				console.log("able_teachers");
				console.log(data.able_teachers);
				
				var teacherkSel = document.querySelector("#user_teacher_no");
				teacherkSel.options.length = 0;

				teacherkSel.options[0] = new Option("-- 선택 --", 0);
				if(data.able_teachers.length > 0){
					for(var i=1; i<=data.able_teachers.length; i++){
						teacherkSel.options[i] = new Option(data.able_teachers[i-1].teacher_name, data.able_teachers[i-1].user_teacher_no);
					}
				}

				document.querySelector("#product_price").innerText = data.product.product_price.toLocaleString('ko-KR');
				p_price = data.product.product_price;
				totalMoney(true);
				
			}, error : function() {
				alert("searchTeacher 실패");
			}
		}); // ajax
	}
}

function teacherSel(sel){
	selectReset(document.querySelector("#timeSel"));
	jcf.replaceAll();
	
	if($(sel).val() != 0){
		$.ajax({
			url : '/student/enrolment/teacherSelect',			
			type : 'post',
			data : {
				'user_teacher_no': $(sel).val(),
				'start_date': document.querySelector("#start_date").innerText,
				'product_no': document.querySelector("input[name='product_no']:checked").value
			},
			success : function(able_times) {
				console.log(able_times);
				
				if(able_times.length > 0){
					var time_sel = document.querySelector("#timeSel");
					time_sel.options.length = 0;
					
					time_sel.options[0] = new Option("-- 선택 --", 0);

					for(var i=1; i<=able_times.length; i++){
						time_sel.options[i] = new Option(addTimeCollon(able_times[i-1].start_time)+"~"+addTimeCollon(able_times[i-1].end_time), addTimeCollon(able_times[i-1].start_time));
					}
				}
				else{
					alert("해당 강사의 조건에 맞는 시간대가 없습니다.");
					
					var time_sel = document.querySelector("#timeSel");
					time_sel.options.length = 0;
					time_sel.options[0] = new Option("-- 선택 --", 0);
					
					jcf.replaceAll();
				}
			}, error : function() {
				alert("teacherSel 실패");
			}
		}); // ajax
	}
}

var p_price = 0;
var t_price = 0;
function totalMoney(flag){
	if(flag){
		var sum = p_price + t_price;
		document.querySelector("#total_price").innerText = sum.toLocaleString('ko-KR');
	}
	else{
		document.querySelector("#total_price").innerText = p_price.toLocaleString('ko-KR');
	}
}

function selectReset(sel){
	sel.options.length = 0;
	sel.options[0] = new Option("-- 선택 --", 0);
}

function addTimeCollon(time){
	return [time.slice(0,2), ":", time.slice(2)].join("");
}

///////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////

var clientKey = 'test_ck_Kma60RZblrqaZNk7wZWrwzYWBn14';
var tossPayments = TossPayments(clientKey);

function payment(){
	if($("#study_no").val() == 0){
		alert("수강과정을 선택해주세요.");
		return;
	}
	if($("#textbook_no").val() == 0){
		alert("교재진도를 선택해주세요.");
		return;
	}
	if($("input[name='product_no']:checked").length <= 0){
		alert("결제할 상품을 선택해주세요.");
		return;
	}
	if($("#user_teacher_no").val() == 0){
		alert("강사를 선택해주세요.");
		return;
	}
	if($("#timeSel").val() == 0){
		alert("수업시간을 선택해주세요.");
		return;
	}
	if(!$("#bookCheck").is(":checked")){
		if($("#zip_code").val() == "" || $("#address").val() == "" || $("#address_detail").val() == ""){
			alert("교재 배송지를 입력해주세요.");
			return;
		}
	}
	
	var price = document.querySelector("#total_price").innerText.replaceAll(",","");
	var today = new Date();
	var id = "saeha" + today.getFullYear() + (today.getMonth()+1) + today.getDate() + today.getHours() + today.getMinutes() + today.getSeconds() + today.getMilliseconds();
	var order = document.querySelector("input[name='product_no']:checked").getAttribute("product");
	var name = document.querySelector("#student_name").value;
	
//	alert(Number(price)+"\n"+id+"\n"+order+"\n"+name);
	var payType = $("#payType").val();
	
	tossPayments.requestPayment(payType, {
	  amount: Number(price),
	  orderId: id,
	  orderName: order,
	  customerName: name,
	  successUrl: window.location + '/success?user_teacher_no='+$("#user_teacher_no").val()+'&product_no='+$("input[name='product_no']:checked").val()
	  								+'&study_no='+$("#study_no").val()+'&textbook_no='+$("#textbook_no").val()+'&payment_date='+$("#start_date").text()
	  								+'&zip_code='+$("#zip_code").val()+'&address='+$("#address").val()+'&address_detail='+$("#address_detail").val()+'&bookCheck='+$("#bookCheck").is(":checked")
	  								+'&payment_time='+$("#timeSel").val()+'&user_student_no='+$("#user_student_no").val()+'&payment_phone='+$("#student_phone").val()+'&price='+price,
	  failUrl: window.location + '/fail',
	});
}

function daumPostCode(){
	new daum.Postcode({
		oncomplete: function(data){
			var addr = '';
			
			if(data.userSelectedType === 'R'){
				addr = data.roadAddress;
			}
			else{
				addr = data.jibunAddress;
			}
			
			document.querySelector("#zip_code").value = data.zonecode;
			document.querySelector("#address").value = addr;
			document.querySelector("#address_detail").focus();
		}
	}).open();
}