function goDetail(item){	
	let param = "?postpone_no="+item.postpone_no;
	window.location.href = "/student/postpone/detail"+param;
}

function insertBtn(){	
	window.location.href = "/student/postpone/detail";
}