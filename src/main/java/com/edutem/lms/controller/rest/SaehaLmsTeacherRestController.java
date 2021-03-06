package com.edutem.lms.controller.rest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.MapUtils;

import com.edutem.lms.component.Common;
import com.edutem.lms.component.S3Wrapper;
import com.edutem.lms.mapper.AdminMapper;
import com.edutem.lms.mapper.TeacherMapper;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.xpath.XPath;

import javax.xml.xpath.XPathConstants;

import javax.xml.xpath.XPathFactory;




@RestController
public class SaehaLmsTeacherRestController {

	@Autowired
	private AdminMapper adminMapper;
	@Autowired
	private TeacherMapper teacherMapper;

	@Autowired
	private S3Wrapper s3Wrapper;
	
    
	//private static final String mainUrl = Conf.mainUrl;
	//private static final String salt = Conf.salt;
	
	private static final long serialVersionUID = 1L;
	private static final String mainUrl = Common.mainUrl;
	private static final String salt = Common.salt;
	
	
	// ?????? BBB API ?????? ?????? - API ????????? ??????
	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	// ?????? BBB API ?????? ?????? - API ????????? ??????
	public static Document parseXml(String xml) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
		return doc;
	}

	// ?????? BBB API ?????? ?????? - API ????????? ??????
	public String checksum(String s) {

		String checksum = "";

		String password = s;
		try {
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(password.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			String sha = sb.toString();
			checksum = sha;
		} catch (Exception e) {

		}

		System.out.println("checksum : " + checksum);

		return checksum;
	}
		
	// ?????? BBB API ?????? ?????? - API ????????? ??????
	public static String postURL(String targetURL, String urlParameters, String contentType)
	{
		URL url;
		HttpURLConnection connection = null;  
		int responseCode = 0;
		try {
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", contentType);
			
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");		
			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (
			connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();

			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
			
		} finally {
			
			if(connection != null) {
				connection.disconnect(); 
			}
		} 
	}
	
	
	
	
	/*
		meetingID : 1_202107061800 

		// ?????? ??????PW    (role 2)
		moderatorPW : 1_202107061800_2
		
		// ?????? ??????PW    (role 3)
		attendeePW: 1_202107061800_3
		
		fullName : eng_name(name)
	*/
	
	
	// Today Schedule - videoClass ??????
	@RequestMapping("/teacher/todaySchedule/videoClass")
   	public Map<String, Object> teacher_todaySchedule_videoClass(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws MalformedURLException {   		 		 		
		Map<String, Object> resultMap = new HashMap<String, Object>();
   		//System.out.println(paramMap); // {flag=class, room_no=1, class_no=1, class_score_no=6, leveltest_no=0, study_date=2021-06-24, class_time=18:00}  		
   		
   		String xmlTxt = "";
   		
   		//Map<String, Object> classInfo = teacherMapper.getVideoClassInfo(paramMap);	// param: class_score_no 
   		Map<String, Object> classInfo = new HashMap<String, Object>(); // flag, no(class_score_no or leveltest_no)
   		if("class".equals(paramMap.get("flag").toString())) {	// ????????? ?????? row??? ???????????? ???????????????
   			paramMap.put("class_score_no", paramMap.get("no"));
   			classInfo = teacherMapper.getVideoClassInfo(paramMap);
   		} else {	// ??????????????? ?????? row??? ???????????? ???????????????
   			paramMap.put("leveltest_no", paramMap.get("no"));
   			classInfo = teacherMapper.getVideoClassLevelInfo(paramMap);
   		}  		
   		
   		String meetingName = classInfo.get("product_name").toString();
   		String meetingID = classInfo.get("room_no").toString() + "_" + classInfo.get("study_date").toString().replace("-", "") 
   				         + classInfo.get("class_time").toString().replace(":", "");
   		
   		String moderatorPW = meetingID + "_2";
   		String attendeePW = meetingID + "_3";
   		String userName = session.getAttribute("login_eng_name").toString() + "(" + session.getAttribute("login_name").toString() + ")";
   		
   		System.out.println(meetingID);
   				
		String createParam = "name=" + urlEncode(meetingName) + "&meetingID=" + urlEncode(meetingID) + "&moderatorPW="
				+ urlEncode(moderatorPW) + "&attendeePW=" + urlEncode(attendeePW) + "&record=true" + "&autoStartRecording=true";
		
		createParam += "&logoutURL=https://install.inetstudy.co.kr:4430/ASPStandard/cookeyenglish27/Netstudy_BBB.asp?pw=-1";
		
		String createCheckSum = checksum("create" + createParam + salt);
		String createUrl = mainUrl + "create?" + createParam + "&checksum=" + createCheckSum;
		
		System.out.println("createURL : " + createUrl);		
		
		// ???????????? textbook ?????? ????????? ??????		
		/*
		if(!"".equals(classInfo.get("textbook_download"))) {
			String fileUrl = "https://saeha.s3.ap-northeast-2.amazonaws.com/textbook/" + classInfo.get("textbook_download").toString();
			
			URL url = new URL(fileUrl);			
			String fileName = FilenameUtils.getName(url.getPath());
			
			fileUrl = fileUrl.replace(fileName, urlEncode(fileName));			
			
			xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'>"
					+ " <document url='"+ fileUrl+"' /> </module></modules>";
   		} else {
   			xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'></module></modules>";					
   		}	
		*/
		// System.out.println("?????? xmlTxt : " + xmlTxt);
		// ?????? xmlTxt : <?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'> <document url='https://saeha.s3.ap-northeast-2.amazonaws.com/textbook/textbook1.pdf' /> </module></modules>
		
		//String fileUrl = "http://install.inetstudy.co.kr/demosite/demo26_kdh/%ED%85%8C%EC%8A%A4%ED%8A%B8.pdf";
		//String fileUrl = "http://root.inetstudy.co.kr/adminclass/uploadFile/TBPage/2158/632_4.jpg";
		
		
		// 0823 - ??????API ?????? ??? ?????? PDF ?????? ?????? ?????? (?????? ?????? ?????? ??????[Unit]?????? PDF??? ????????????. ?????? ???????????? ?????? ????????? ????????? unit 1??? ????????? ?????????)		
		if("class".equals(paramMap.get("flag").toString())) {
			paramMap.put("class_score_no", paramMap.get("no"));
			Map<String, Object> topic = teacherMapper.getTopic(paramMap);			
			// ### {log_topic_no=6, class_topic_no=1, slide_number=4, textbook_no=12, class_log_no=2, class_student_no=1, class_no=1, study_status=1, class_score_no=3}
			
			// ?????? ?????? textbook_topic_no
			String topic_no = "0";
			if(!"0".equals(topic.get("log_topic_no").toString())) {
				topic_no = topic.get("log_topic_no").toString();
			} else {
				topic_no = topic.get("class_topic_no").toString();
			}
			
			
			if(!"0".equals(topic_no)) {
				paramMap.put("topic_no", topic_no);
				Map<String, Object> textbookTopic = teacherMapper.getTextbookTopic(paramMap);			
				
				System.out.println("topic_no : " + topic_no);				
				
				if(!"".equals(textbookTopic.get("pdf_file").toString())) {
					String fileUrl = "https://saeha.s3.ap-northeast-2.amazonaws.com/textbook_pdf/" + textbookTopic.get("pdf_file").toString();
					// https://saeha.s3.ap-northeast-2.amazonaws.com/textbook_pdf/topic_pdf1.pdf
					
					URL url = new URL(fileUrl);			
					String fileName = FilenameUtils.getName(url.getPath());
					
					fileUrl = fileUrl.replace(fileName, urlEncode(fileName));			
					
					xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'>"
							+ " <document url='"+ fileUrl+"' /> </module></modules>";
				} else {
					xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'></module></modules>";
				}
				
				
			} else {
				xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'></module></modules>";
			}
			
		} else {	// ??????????????? (?????? x)
			xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'></module></modules>";
		}		
		
		System.out.println("?????? xmlTxt : " + xmlTxt);
		
		
			
		// ?????? meetingID??? ???????????????????????? ???????????? ????????? create ?????? join???
		// ?????? ????????? ?????? API
   		String getMeetingInfoParams = "meetingID=" + meetingID;
   		String getMeetingInfoCheckSum = checksum("getMeetingInfo" + getMeetingInfoParams + Common.salt);
   		String getMeetingInfoUrl = Common.mainUrl + "getMeetingInfo?" + getMeetingInfoParams + "&checksum=" + getMeetingInfoCheckSum;
   		
   		Document doc = null;
   		String returnCode = "";
		
   		try {
			String params = postURL(getMeetingInfoUrl, xmlTxt, "text/xml");
			System.out.println("paramssss : " + params);
			doc = parseXml(params);
			
			doc.getDocumentElement().normalize();
			// XML ????????? ??? <person> ????????? ????????? ????????????.			
			
			NodeList returncode =  doc.getElementsByTagName("returncode");			
			System.out.println("returnCode : " + returncode.item(0).getTextContent());	
			// SUCCESS
			returnCode = returncode.item(0).getTextContent().trim();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*   		
		if(!"SUCCESS".equals(returnCode)) {
			System.out.println("????????? Create");
			doc = null;			
			try {
				// Attempt to create a meeting using meetingID
				String params = postURL(createUrl, xmlTxt, "text/xml");
				System.out.println("paramssss : " + params);
				doc = parseXml(params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		*/
		
		
		System.out.println("????????? Create");
		doc = null;			
		try {
			// Attempt to create a meeting using meetingID
			String params = postURL(createUrl, xmlTxt, "text/xml");
			System.out.println("paramssss : " + params);
			doc = parseXml(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
   		
		
		String joinUrl = "";
		
		// ??????, ?????? ?????? ??? ?????? ???????????? ?????? - moderatorPW/attendeePW??? ?????? session role??? ?????? (?????? 2 ?????? 3)
		String joinPW = meetingID + "_" + session.getAttribute("role_no");
		System.out.println("joinPW: " + joinPW);
		
		if (doc.getElementsByTagName("returncode").item(0).getTextContent()
				.trim().equals("SUCCESS")) {

			String join_parameters = "meetingID=" + urlEncode(meetingID)
				// + "&fullName=" + urlEncode(userName) + "&password=" + urlEncode(moderatorPW);
				+ "&fullName=" + urlEncode(userName) + "&password=" + urlEncode(joinPW);

			//return base_url_join + join_parameters + "&checksum=" + checksum("join" + join_parameters + salt);
			String joinCheckSum = checksum("join" + join_parameters + salt);
			joinUrl = mainUrl + "join?" + join_parameters + "&checksum=" + joinCheckSum;
		}

		System.out.println("joinUrl :" + joinUrl);
		
		request.getSession().setAttribute("moderatorJoinUrl", joinUrl);
		request.getSession().setAttribute("attendeeJoinUrl", "joinUrl");
		
		
		resultMap.put("createUrl", createUrl);
		resultMap.put("joinUrl", joinUrl);
		
   		return resultMap;
   	}
	
	
	// Today Schedule - File ?????? (?????? API ????????? ??????)
	@RequestMapping("/teacher/todaySchedule/videoFile")
   	public Map<String, Object> teacher_todaySchedule_videoFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws MalformedURLException {   		 		 		
		Map<String, Object> resultMap = new HashMap<String, Object>();		   		
   		System.out.println(paramMap); // {flag=class, room_no=1, class_no=1, class_score_no=6, leveltest_no=0, study_date=2021-06-24, class_time=18:00}   		   		
   		
   		Map<String, Object> classInfo = new HashMap<String, Object>();	
		if("class".equals(paramMap.get("flag").toString())) {
			paramMap.put("class_score_no", paramMap.get("no"));
			classInfo = teacherMapper.getVideoClassInfo(paramMap);		// class_score_no
		} else {
			paramMap.put("leveltest_no", paramMap.get("no"));
			classInfo = teacherMapper.getVideoClassLevelInfo(paramMap);	// leveltest_no
		}
   		
   		
   		String xmlTxt = "";
   		
   		String meetingName = classInfo.get("product_name").toString();
   		String meetingID = classInfo.get("room_no").toString() + "_" + classInfo.get("study_date").toString().replace("-", "") 
   				         + classInfo.get("class_time").toString().replace(":", "");   		
   		String moderatorPW = meetingID + "_2";
   		String attendeePW = meetingID + "_3";
   		String userName = session.getAttribute("login_eng_name").toString() + "(" + session.getAttribute("login_name").toString() + ")";
		
		// ?????? ?????? API meetingID ????????? ?????? ????????? ?????? ?????? ?????? / ??????????????? ?????? ?????? ?????? ??????
		String recordingParam = "";
		
		recordingParam = "meetingID=" + meetingID;
		
		String getRecordingsCheckSum = checksum("getRecordings" + recordingParam + Common.salt);
		String getRecordingsUrl = Common.mainUrl + "getRecordings?" + recordingParam + "&checksum=" + getRecordingsCheckSum;
		
		
		xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'>"
				+ "</module></modules>";
		
		Document doc = null;		
		//List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();	// ?????????
		//Map<String, Object> recordMap = new HashMap<String, Object>();
		String urlSplitString = "";
		String recordIdSplitString = "";
		
		try {
			// Attempt to create a meeting using meetingID
			String params = postURL(getRecordingsUrl, xmlTxt, "text/xml");
			System.out.println("paramssss : " + params);
			doc = parseXml(params);
						
			doc.getDocumentElement().normalize();
			// XML ????????? ??? <person> ????????? ????????? ????????????.			
			
			NodeList urlNodeList =  doc.getElementsByTagName("url");			
			NodeList recordIdNodeList =  doc.getElementsByTagName("recordID");
			
			for(int i = 0; i < urlNodeList.getLength(); i++) {
				//recordMap = new HashMap<String, Object>();
				//recordMap.put("url", urlNodeList.item(i).getTextContent());
				//recordMap.put("recordID", recordIdNodeList.item(i).getTextContent());
				
				//recordList.add(recordMap);
				
				if(i != urlNodeList.getLength() - 1) {
					urlSplitString += urlNodeList.item(i).getTextContent() + "@@";
					recordIdSplitString += recordIdNodeList.item(i).getTextContent() + "@@";
				} else {
					urlSplitString += urlNodeList.item(i).getTextContent();
					recordIdSplitString += recordIdNodeList.item(i).getTextContent();
				}
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//classInfo.put("recordList", recordList);	// ?????????
		classInfo.put("urlSplitString", urlSplitString);
		classInfo.put("recordIdSplitString", recordIdSplitString);
		
		//resultMap.put("urlList", urlList);
		
		System.out.println("getRecordingsUrl : " + getRecordingsUrl);		
		resultMap.put("getRecordingsUrl", getRecordingsUrl);
				
		
   		return classInfo;
   	}
	
	
	
	
	
	// ?????? ???????????? ??????	 (?????? API ????????? ??????)
	@RequestMapping("/teacher/todaySchedule/deleteVideoFile")
   	public Map<String, Object> teacher_todaySchedule_deleteVideoFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws MalformedURLException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String xmlTxt = "";   		
   		
   		String recordID = paramMap.get("recordID").toString();   		
   		System.out.println(recordID);
   		
   		String deleteParam = "recordID=" + recordID; 
   		String getDeleteCheckSum = checksum("deleteRecordings"+deleteParam + Common.salt);
   		String getDeleteUrl = Common.mainUrl + "deleteRecordings?" + deleteParam + "&checksum=" + getDeleteCheckSum;
   		
		
		xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'>"
				+ "</module></modules>";
		
		Document doc = null;
		
		try {
			// Attempt to create a meeting using meetingID
			String params = postURL(getDeleteUrl, xmlTxt, "text/xml");
			System.out.println("paramssss : " + params);
			doc = parseXml(params);
			
			doc.getDocumentElement().normalize();
			
			NodeList returncode =  doc.getElementsByTagName("returncode");			
			System.out.println("returnCode : " + returncode.item(0).getTextContent());	
			// SUCCESS
			resultMap.put("returnCode", returncode.item(0).getTextContent());
			System.out.println("getDeleteUrl : " + getDeleteUrl);
			
		} catch (Exception e) {
			e.printStackTrace();
		}				
		
   		return resultMap;
   	}
	
	
	
	


	// Today Schedule - ????????? ?????? (?????? MeetingId??? ????????? ????????????????????? ?????????) - ?????? ???????????? ?????? ??????????????? ????????????..
	@RequestMapping("/teacher/todaySchedule/videoClassInfo")
   	public Map<String, Object> teacher_todaySchedule_videoClassInfo(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws MalformedURLException {   		 		 		
		Map<String, Object> resultMap = new HashMap<String, Object>();  		   		
   		
   		String xmlTxt = "";   		
   		Map<String, Object> classInfo = teacherMapper.getVideoClassInfo(paramMap);
   		
   		String meetingName = classInfo.get("product_name").toString();
   		String meetingID = classInfo.get("room_no").toString() + "_" + classInfo.get("study_date").toString().replace("-", "") 
   				         + classInfo.get("class_time").toString().replace(":", "");
   		
   		//System.out.println(meetingID);
   		
		// ?????? ????????? ?????? API
   		String getMeetingInfoParams = "meetingID=" + meetingID;
   		String getMeetingInfoCheckSum = checksum("getMeetingInfo" + getMeetingInfoParams + Common.salt);
   		String getMeetingInfoUrl = Common.mainUrl + "getMeetingInfo?" + getMeetingInfoParams + "&checksum=" + getMeetingInfoCheckSum;
		
		xmlTxt = "<?xml version='1.0' encoding='UTF-8'?> <modules>	<module name='presentation'>"
				+ "</module></modules>";
		
		Document doc = null;
		
		try {
			// Attempt to create a meeting using meetingID
			String params = postURL(getMeetingInfoUrl, xmlTxt, "text/xml");
			System.out.println("paramssss : " + params);
			doc = parseXml(params);
			
			doc.getDocumentElement().normalize();
			
			NodeList returncode =  doc.getElementsByTagName("returncode");			
			System.out.println("returnCode : " + returncode.item(0).getTextContent());	
			// SUCCESS
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
   		return resultMap;
   	}
	
	
	
	// Today Schedule - videoClass ?????? ????????? ????????? ???????????? insert
	@RequestMapping("/teacher/todaySchedule/videoTeacherLogInsert")
   	public Map<String, Object> teacher_todaySchedule_videoTeacherLogInsert(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
   		paramMap.put("login_no", session.getAttribute("login_no").toString());
   		System.out.println(paramMap);    		
   		
   		teacherMapper.insertClassVideoTeacherLog(paramMap);   		   		
   		int class_video_teacher_log_no = teacherMapper.selectLastAIClassVideoTeacherLog() - 1;
   		
   		paramMap.put("class_video_teacher_log_no", class_video_teacher_log_no);
   		
   		return paramMap;
   	}
	
	// Today Schedule - videoClass ?????? ?????? ??? ????????? ???????????? update (out_time)
	@RequestMapping("/teacher/todaySchedule/videoTeacherLogUpdate")
   	public void teacher_todaySchedule_videoTeacherLogUpdate(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
   		teacherMapper.updateClassVideoTeacherLog(paramMap);
   	}
	
	
	
	// Today Schedule - ??????????????? videoClass ?????? ????????? ????????? ??????/???????????? insert   (??? leveltest ?????? : class??? ????????? ??????????????? ??????/????????? ???????????? ???????????? ?????????) 
	@RequestMapping("/teacher/todaySchedule/videoLevelLogInsert")
   	public Map<String, Object> teacher_todaySchedule_videoTeacherLevelLogInsert(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
   		paramMap.put("login_no", session.getAttribute("login_no").toString());
   		
   		teacherMapper.insertClassVideoLevelLog(paramMap);   		   		
   		int level_video_log_no = teacherMapper.selectLastAIClassVideoLevelLog() - 1;
   		
   		paramMap.put("level_video_log_no", level_video_log_no);
   		
   		return paramMap;
   	}
	
	// Today Schedule - ??????????????? videoClass ?????? ?????? ??? ????????? ???????????? update (out_time)
	@RequestMapping("/teacher/todaySchedule/videoLevelLogUpdate")
   	public void teacher_todaySchedule_videoLevelLogUpdate(@RequestParam Map<String, Object> paramMap, HttpSession session) throws MalformedURLException {
   		teacherMapper.updateClassVideoLevelLog(paramMap);
   	}
	
	
	// ?????? ?????????????????? ?????? - engName ??????
	@RequestMapping("/teacher/userDetail/engNameModify")
   	public void teacher_userDetail_engNameModify(@RequestParam Map<String, Object> paramMap) {
   		teacherMapper.updateEngNameByTeacherUserDetail(paramMap);
   	}
}
