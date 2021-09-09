function contentDetail(item){		
	$('#smsContent').text(item.content);
	
	$('#smsContent').css('display','block');
	$('#statusContent').css('display','none');
	
	// modal open
    $('.modal').css('display','block');
	$('body').css("overflow", "hidden");	
}


function statusDetail(item){	
	//console.log(item.result_msg);	// 결과메시지 - 성공 or 에러메시지 내용
	$('#statusContent').text(item.result_msg);
	
	$('#smsContent').css('display','none');
	$('#statusContent').css('display','block');
	
	// modal open
    $('.modal').css('display','block');
	$('body').css("overflow", "hidden");	
}



// modal 닫기
$(document).on("click", ".close", function(){  
   	$('.modal').css('display','none');
	$('body').css("overflow", "scroll"); 
});


function modalClose(){	
	$('.modal').css('display','none');
	$('body').css("overflow", "scroll");	
}








