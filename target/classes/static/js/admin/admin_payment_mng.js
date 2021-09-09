function search(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/paymentMng"+params;
}

function makeSearchParam(cri){	
	let params = "page="+cri.page+"&perPageNum="+cri.perPageNum+"&searchType="+cri.searchType+"&keyword="+cri.keyword;		
	return params;
}

// 한 페이지 당 보여줄 게시글 수 (5/10/20)
$(document).on("change", "#perPageNum", function(){
	let keyword = $("#keyword").val();
	let searchType = $("#searchType").val();
	var perPageNum = $("#perPageNum option:selected").val();	

	let params = "?page=1"+"&perPageNum="+perPageNum+"&searchType="+searchType+"&keyword="+keyword;
	window.location.href = "/admin/paymentMng"+params;
});

function paymentStatus(payment_no, payment_status){
	
	var status = (payment_status == 0)? 1:0;
	
	$.ajax({
		url : '/admin/paymentStatus',
		type : 'post',
		data: {
			'payment_no': payment_no,
			'payment_status': status
		},
		success : function() {
			alert("상태가 변경되었습니다.");
			window.location.reload();
		}, error : function() {
			alert("paymentStatus 오류");
		}
	});
}


