package com.edutem.lms.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Common {
	// BBB 화상 API
	// BBB 화상 API
	//public static final String mainUrl = "https://23rnd.saehaens.com/bigbluebutton/api/";
	public static final String mainUrl = "https://netstudy.saehaens.com/bigbluebutton/api/";
	//public static final String salt = "PLrzEeP1S9oEnINQPEss7fWcTyyLdyIIBp1TjujDWk";
	public static final String  salt = "3sEMifRf3JGg7jgYrlEjpEbPwp81LJVSpx9URn9BhY";
	///////////////////////////////////////////////////////////////////////////////////////
	
	private final static String COOLSMS_API_KEY = "NCS619TEQZOPH24F";
	private final static String COOLSMS_API_SECRET = "HQOZY73J7SECV6HCYXQ85EB2VGIAAPMD";
	private final static String EDUTEM_PHONE_NUMBER = "070-4082-4063";//회사 전화번호
	public final static String EDUTEM_CEO_PHONE = "010-9213-2854";//수신번호 대표
	public final static String EDUTEM_TEAM_LEADER_PHONE = "010-9214-1609";//수신번호 팀장
	public final static String EDUTEM_DIRECTION_PHONE = "010-3974-2155";//수신번호 이사

	public static void coolSMSSend(Map<String, Object> paramMap, String type){
		switch (type) {
			case "sms":
				coolSMSSend(paramMap);
				break;
			case "lms":
				coolLMSSend(paramMap);
				break;
			case "mms":
				coolMMSSend(paramMap);
				break;
			default:
				break;
		}
	}
	
	public static Map<String, Object> coolSMSSend2(Map<String, Object> paramMap, String type){
		switch (type) {
			case "sms":
				return coolSMSSend2(paramMap);	
			case "lms":
				return coolLMSSend2(paramMap);	
			default:
				break;
		}		
		return paramMap;
	}
	
	public static Map<String, Object> coolSMSSend2(Map<String, Object> paramMap){
		Coolsms coolsms = new Coolsms(COOLSMS_API_KEY, COOLSMS_API_SECRET);
		
		/*
		 * Parameters
		 * 관련정보 : http://www.coolsms.co.kr/SDK_Java_API_Reference_ko#toc-0
		 */
		String to = String.valueOf(paramMap.get("receipt_number")).replaceAll("-", "");
		
		HashMap<String, String> set = new HashMap<String, String>();
		set.put("to", to);//수신번호

		//* 10월 16일 이후로 발신번호 사전등록제로 인해 등록된 발신번호로만 문자를 보내실 수 있습니다. 바로가기
		//set.put("from", EDUTEM_PHONE_NUMBER);//발신번호
		String from = String.valueOf(paramMap.get("send_number")).replaceAll("-", "");
		set.put("from", from);//발신번호
		
		set.put("text", (paramMap.containsKey("text")) ? String.valueOf(paramMap.get("text")) : "");//문자내용
		set.put("type", "sms");//문자 타입
		
		String reserve_time = "";	// 20210601123000	yyyyMMddHHmmss
		
		// 예약발송 +
		if ("1".equals(paramMap.get("reserve_yn").toString())) {	// 예약발송 체크상태라면
			//set.put("datetime", "201401151230"); // 예약전송시 날짜 설정		
			reserve_time = paramMap.get("reserve_dt").toString() + paramMap.get("reserve_hour").toString() + paramMap.get("reserve_min").toString()+"00";
					
			set.put("datetime", reserve_time); // 예약전송시 날짜 설정
		}
		
		
		
		Map<String, Object> result = coolsms.send(set);// 보내기&전송결과받기
		System.out.println("문자 전송 data : " + String.valueOf(set));
		System.out.println("문자 전송 결과 : " + result.toString());
		
		
		// sms_log table insert
		System.out.println(paramMap);
		
		Map<String, Object> logMap = new HashMap<String, Object>(); 
		logMap.put("user_student_no", paramMap.get("user_student_no"));
		logMap.put("content", paramMap.get("text"));
		logMap.put("send_date", paramMap.get("send_date"));					// 발송일
		logMap.put("send_time", paramMap.get("send_time"));					// 발송시간
		
		logMap.put("send_number", from);									// 발신번호	
		logMap.put("receipt_number", paramMap.get("receipt_number"));		// 수신자번호
		logMap.put("type", paramMap.get("type"));							// 전송방식 SMS,LMS
		
		logMap.put("reserve_yn", paramMap.get("reserve_yn").toString());	// 예약 발송 체크 여부
		logMap.put("reserve_time", reserve_time);							// 예약 발송 시간
		
		
		// 전송 실패라면..
		if(result.containsKey("errorMessage")) {
			logMap.put("status", "0");
			logMap.put("result_msg", result.get("errorMessage"));
		} else {
			logMap.put("status", "1");
			logMap.put("result_msg", "성공");
		}
		
		return logMap;
	}
	
	/**
	 * LMS발송
	 * set.put("type") 을 LMS로 바꿔주시기만하면됩니다.
	 * @param paramMap
	 */
	public static Map<String, Object> coolLMSSend2(Map<String, Object> paramMap){
		Coolsms coolsms = new Coolsms(COOLSMS_API_KEY, COOLSMS_API_SECRET);
		
		/*
		 * Parameters
		 * 관련정보 : http://www.coolsms.co.kr/SDK_Java_API_Reference_ko#toc-0
		 */
		String to = String.valueOf(paramMap.get("receipt_number")).replaceAll("-", "");
		HashMap<String, String> set = new HashMap<String, String>();
		set.put("to", to);//수신번호

		//* 10월 16일 이후로 발신번호 사전등록제로 인해 등록된 발신번호로만 문자를 보내실 수 있습니다. 바로가기
		String from = String.valueOf(paramMap.get("send_number")).replaceAll("-", "");
		set.put("from", from);//발신번호		
	
		set.put("subject", (paramMap.containsKey("title")) ? String.valueOf(paramMap.get("title")) : "");//LMS, MMS 일때 제목	// 현재 미정
		
		set.put("text", (paramMap.containsKey("text")) ? String.valueOf(paramMap.get("text")) : "");//문자내용
		set.put("type", "lms");//문자 타입
		
		
		String reserve_time = "";	// 20210601123000	yyyyMMddHHmmss
		
		// 예약발송 +
		if ("1".equals(paramMap.get("reserve_yn").toString())) {	// 예약발송 체크상태라면
			//set.put("datetime", "201401151230"); // 예약전송시 날짜 설정		
			reserve_time = paramMap.get("reserve_dt").toString() + paramMap.get("reserve_hour").toString() + paramMap.get("reserve_min").toString()+"00";
					
			set.put("datetime", reserve_time); // 예약전송시 날짜 설정
		}
		
		
		Map<String, Object> result = coolsms.send(set);// 보내기&전송결과받기
		System.out.println("문자 전송 data : " + String.valueOf(set));
		System.out.println("문자 전송 결과 : " + result.toString());
		
		
		// sms_log table insert
		Map<String, Object> logMap = new HashMap<String, Object>(); 
		logMap.put("user_student_no", paramMap.get("user_student_no"));
		logMap.put("content", paramMap.get("text"));
		logMap.put("send_date", paramMap.get("send_date"));
		logMap.put("send_time", paramMap.get("send_time"));
		
		logMap.put("send_number", from);								// 발신번호	
		logMap.put("receipt_number", paramMap.get("receipt_number"));	// 수신자번호
		logMap.put("type", paramMap.get("type"));
		
		logMap.put("reserve_yn", paramMap.get("reserve_yn").toString());
		logMap.put("reserve_time", reserve_time);
		
		
		// 전송 실패라면..
		if(result.containsKey("errorMessage")) {
			logMap.put("status", "0");
			logMap.put("result_msg", result.get("errorMessage"));
		} else {
			logMap.put("status", "1");
			logMap.put("result_msg", "성공");
		}
		
		return logMap;
	}
	
	
	
	
	
	/**
	 * SMS발송
	 * HashMap set에 받는사람번호, 보내는사람번호, 문자내용 등 을 저장한뒤 Coolsms클래스의 send를 이용해 보냅니다.
	 * @param paramMap
	 * @return
	 */
	public static void coolSMSSend(Map<String, Object> paramMap){
		Coolsms coolsms = new Coolsms(COOLSMS_API_KEY, COOLSMS_API_SECRET);
		
		/*
		 * Parameters
		 * 관련정보 : http://www.coolsms.co.kr/SDK_Java_API_Reference_ko#toc-0
		 */
		String to = String.valueOf(paramMap.get("receipt_number")).replaceAll("-", "");
		
		HashMap<String, String> set = new HashMap<String, String>();
		set.put("to", to);//수신번호

		//* 10월 16일 이후로 발신번호 사전등록제로 인해 등록된 발신번호로만 문자를 보내실 수 있습니다. 바로가기
		set.put("from", EDUTEM_PHONE_NUMBER);//발신번호
		set.put("text", (paramMap.containsKey("text")) ? String.valueOf(paramMap.get("text")) : "");//문자내용
		set.put("type", "sms");//문자 타입
		
		Map<String, Object> result = coolsms.send(set);// 보내기&전송결과받기
		System.out.println("문자 전송 data : " + String.valueOf(set));
		System.out.println("문자 전송 결과 : " + result.toString());
	}
	
	/**
	 * LMS발송
	 * set.put("type") 을 LMS로 바꿔주시기만하면됩니다.
	 * @param paramMap
	 */
	public static void coolLMSSend(Map<String, Object> paramMap){
		Coolsms coolsms = new Coolsms(COOLSMS_API_KEY, COOLSMS_API_SECRET);
		
		/*
		 * Parameters
		 * 관련정보 : http://www.coolsms.co.kr/SDK_Java_API_Reference_ko#toc-0
		 */
		String to = String.valueOf(paramMap.get("receipt_number")).replaceAll("-", "");
		HashMap<String, String> set = new HashMap<String, String>();
		set.put("to", to);//수신번호

		//* 10월 16일 이후로 발신번호 사전등록제로 인해 등록된 발신번호로만 문자를 보내실 수 있습니다. 바로가기
		set.put("from", EDUTEM_PHONE_NUMBER);//발신번호
		set.put("subject", (paramMap.containsKey("title")) ? String.valueOf(paramMap.get("title")) : "보라구 중국어");//LMS, MMS 일때 제목
		set.put("text", (paramMap.containsKey("text")) ? String.valueOf(paramMap.get("text")) : "");//문자내용
		set.put("type", "lms");//문자 타입
		
		Map<String, Object> result = coolsms.send(set);// 보내기&전송결과받기
		System.out.println("문자 전송 data : " + String.valueOf(set));
		System.out.println("문자 전송 결과 : " + result.toString());
	}
	
	/**
	 * MMS발송
	 * type을 MMS로 바꿔준뒤 전송할 파일정보를 입력합니다.
	 * @param paramMap
	 */
	public static void coolMMSSend(Map<String, Object> paramMap){
		Coolsms coolsms = new Coolsms(COOLSMS_API_KEY, COOLSMS_API_SECRET);
		/*
		 * Parameters
		 * 관련정보 : http://www.coolsms.co.kr/SDK_Java_API_Reference_ko#toc-0
		 */
		String to = String.valueOf(paramMap.get("receipt_number")).replaceAll("-", "");
		HashMap<String, String> set = new HashMap<String, String>();
		set.put("to", to);//수신번호
		
		//* 10월 16일 이후로 발신번호 사전등록제로 인해 등록된 발신번호로만 문자를 보내실 수 있습니다. 바로가기
		set.put("from", EDUTEM_PHONE_NUMBER);//발신번호
		set.put("subject", (paramMap.containsKey("title")) ? String.valueOf(paramMap.get("title")) : "보라구 중국어");//LMS, MMS 일때 제목
		set.put("text", (paramMap.containsKey("text")) ? String.valueOf(paramMap.get("text")) : "");//문자내용
		set.put("type", "mms"); // 문자 타입

		set.put("image_path", "../images/"); // image file path 이미지 파일 경로 설정 (기본 "./")
		set.put("image", "test.jpg"); // image file (지원형식 : 200KB 이하의 JPEG)
		
		Map<String, Object> result = coolsms.send(set);// 보내기&전송결과받기
		System.out.println("문자 전송 data : " + String.valueOf(set));
		System.out.println("문자 전송 결과 : " + result.toString());
	}
	
}
