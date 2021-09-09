let onOffFlag = '1';

function textbookSubmitOnOff(){
	if(onOffFlag == '1'){
		//display: none;
		document.getElementById("textbookPageForm").style.display = "none";		
		onOffFlag = '0';
		onOffBtn.innerHTML = "Open";
	} else {
		document.getElementById("textbookPageForm").style.display = "block";		
		onOffFlag = '1';
		onOffBtn.innerHTML = "Close";
	}
}

// textbook change 이벤트 발생 시 - Topic 콤보박스 셀렉트박스 Setting
function textbookChange(e) {	
	let changeValue = e.value;

	// topic 셀렉트박스 초기화
	let topicSelect = document.getElementById("textbook_topic_no");	
	topicSelect.options.length = 0;	
	topicSelect.options[0] = new Option("-- 선택 --", "");

	// 셀렉트박스 세팅
	let topicArr;

	$.ajax({
		url: '/reportPopup/getTopicCombo',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"textbook_no": changeValue
		},
		success: function(item) {
			topicArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});
	

	if (topicArr.length > 0) {
		for (i = 1; i <= topicArr.length; i++) {
			topicSelect.options[i] = new Option(topicArr[i - 1].topic_name, topicArr[i - 1].textbook_topic_no);		// text, value
		}
	}

	jcf.replaceAll();
}


// topic change 이벤트 발생 시 - Page 콤보박스 셀렉트박스 Setting
function topicChange(e) {	
	let changeValue = e.value;


	/*
	// page 셀렉트박스 초기화	
	let selectBox = document.getElementById("textbook_page_no");
	selectBox.options.length = 0;

	// 셀렉트박스 세팅
	let itemArr;

	$.ajax({
		url: '/reportPopup/getPageCombo',
		type: 'post',
		async: false,	// ajax함수 실행을 기다리고 다음 소스코드를 읽게된다.
		data: {
			"textbook_topic_no": changeValue
		},
		success: function(item) {
			itemArr = item;
		}, error: function() {
			//console.log("실패");
		} 
	});

	selectBox.options[0] = new Option("-- 선택 --", "");
	if (itemArr.length > 0) {
		for (i = 1; i <= itemArr.length; i++) {
			selectBox.options[i] = new Option(itemArr[i - 1].page_name, itemArr[i - 1].textbook_page_no);		// text, value
		}
	}

	jcf.replaceAll();
	*/
}


function textbookSubmit() {
	let textbook_topic_no = $('#textbook_topic_no').val();
	
	if(textbook_topic_no == ''){
		alert('Unit을 확인하세요.');
		return false;
	}
	
	let form = document.querySelector("#textbookPageForm");
	let data = new FormData(form);	
	
	$.ajax({   			
		url : '/reportPopup/updateClassLogPage',			
		type : 'post',
		data : data,
		processData: false, // ajax로 formData 전송시 필요
        contentType: false, // ajax로 formData 전송시 필요		
		success : function() {			
			alert("수정되었습니다.");
			location.reload();			
			//window.location.href = "/center/teacher";								
			 
		}, error : function() {
			alert("등록 실패");
			//console.log("실패");
		} // success
	}); // ajax	
}


function smsSendBtn(item, flag) {	
	let msg = flag == 'sound' ? "No Sound" : "No Show";
	
	if(confirm("메시지를 전송하시겠습니까? [ " + msg + " ]")){ 
		$.ajax({   			
			url : '/reportPopup/smsSend',			
			type : 'post',
			data : {
				'user_student_no' : item.user_student_no
			  , 'student_phone' : item.student_phone
			  , 'flag' : flag
			},					
			success : function() {			
				alert("전송되었습니다.");
			}, error : function() {
				alert("전송 실패");
			} // success
		}); // ajax
	}
	
}


function rBtn(item, flag){
	// item.class_no, item.study_date, item.class_log_no, item.class_student_no	
	//document.getElementById("iframe").src = '/reportPopup/report?class_log_no='+ item.class_log_no +"&class_student_no=" +item.class_student_no;
	if(flag == "class"){
		document.getElementById("reportIframe").src = '/reportPopup/report?flag='+flag+'&class_score_no='+ item.class_score_no;	
	} else {
		document.getElementById("reportIframe").src = '/reportPopup/report?flag='+flag+'&leveltest_no='+ item.leveltest_no;
	}
}



// TIME ALERT (러닝타임 타이머)
var tid;
var play = false;

var SetTime = running_time * 60;      // 최초 설정 시간(기본 : 초)

function msg_time() {   // 1초씩 카운트      
    //m = Math.floor(SetTime / 60) + "분 " + (SetTime % 60) + "초"; // 남은 시간 계산        
    //var msg = "현재 남은 시간은 <font color='red'>" + m + "</font> 입니다.";

	let minutes = Math.floor(SetTime / 60);
	let seconds = (SetTime % 60);

	m = (minutes ? (minutes > 9 ? minutes : "0" + minutes) : "00") + " : " + (seconds > 9 ? seconds : "0" + seconds); // 남은 시간 계산
	var msg = m;
  
    document.all.ViewTimer.innerHTML = msg;     // div 영역에 보여줌                  
    SetTime--;                  // 1초씩 감소
    if (SetTime < 0) {          // 시간이 종료 되었으면..        
        clearInterval(tid);     // 타이머 해제
        //alert("시간이 종료되었습니다.");
    }       
}


function TimerStart(){
	//console.log("TimerStart()");
    tid=setInterval('msg_time()',1000)
};

function timer_start(){
	//console.log("start");
   	if(!play) TimerStart();
	play = true;
}

function timer_stop(){
	//console.log("stop");
	if(play) clearInterval(tid);
	play = false;
}

function timer_clear(){
	//console.log("clear");	
   	clearInterval(tid);
   	SetTime = running_time * 60;

	let minutes = Math.floor(SetTime / 60);
	let seconds = (SetTime % 60);
	document.all.ViewTimer.innerHTML = (minutes ? (minutes > 9 ? minutes : "0" + minutes) : "00") + " : " + (seconds > 9 ? seconds : "0" + seconds);

	play = false;
}










// 스탑워치 타이머
/*
window.onload = function()
{
	d_timer();
}

window.onunload = function()
{
	clearTimeout(timer);
}

var	timer		=	setInterval(function() {
	d_timer()
	}, 1000);

var	hour	=	0;   // {$time['H']};    //    초기치(시간)

var	minute	= 10;	// {$time['i']};    //    초기치(분)

var	second	= 0;	// {$time['s']};    //    초기치(초)



function d_timer()
{
	second--;
	if ( 0 > second )
	{
		if ( 0 >= minute )
		{
			if ( 0 >= hour )
				clearTimeout(timer);
			else
			{
				hour--;
				minute	=	59;
				second	=	59;
			}
		} else
		{
			minute--;
			second	=	59;
		}
	}
	

	if ( 0 > second )
		second	=	0;	

	html_hour	=	document.getElementById('hour');
	
	if ( 0 != hour )
		html_hour.innerHTML	=	"<span>" + hour + "</span><span>시간</span>";

	html_minute	=	document.getElementById('minute');

	if ( 0 != second )
		html_minute.innerHTML	=	"<span>" + minute + "</span><span>분</span>";

	html_second	=	document.getElementById('second');

	html_second.innerHTML	=	"<span>" + second + "</span><span>초</span>";
}
*/


/*
var h1 = document.getElementsByTagName('h1')[0],
    start = document.getElementById('start'),
    stop = document.getElementById('stop'),
    clear = document.getElementById('clear'),
    milli = 0, seconds = 0, minutes = 0, hours = 0,
    t;

function add() {
    milli += 10;
    if (milli >= 1000) {
        milli = 0;
        seconds++;
        if (seconds >= 60) {
            seconds = 0;
            minutes++;
            if (minutes >= 60) {
                minutes = 0;
                hours++;
            }
        }
    }
    
    h1.textContent = (hours ? (hours > 9 ? hours : "0" + hours) : "00") + ":" + (minutes ? (minutes > 9 ? minutes : "0" + minutes) : "00") + ":" + (seconds > 9 ? seconds : "0" + seconds) + "." + (milli > 90 ? milli : "0" + milli);

    timer();
}
function timer() {
    t = setTimeout(add, 10);
}
//timer();


// Start button
start.onclick = function() {
    if(!t) timer();
}

// Stop button
stop.onclick = function() {
    clearTimeout(t);
    t = null;
}

// Clear button
clear.onclick = function() {
    h1.textContent = "00:00:00.000";
    milli = 0; seconds = 0; minutes = 0; hours = 0;
}
*/






