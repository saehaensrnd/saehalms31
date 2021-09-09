package com.edutem.lms.component;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Wrapper {

	@Autowired
	private AmazonS3 amazonS3Client;
	
	private String bucket = "saeha";
	
	public void upload(MultipartFile file, String folderName, String storedFileName) throws IOException {
		ObjectMetadata omd = new ObjectMetadata();
		omd.setContentType(file.getContentType());
		omd.setContentLength(file.getSize());
		//omd.setHeader("filename", file.getOriginalFilename());
		omd.setHeader("filename", storedFileName);
		amazonS3Client.putObject(new PutObjectRequest(bucket + "/" + folderName, storedFileName, file.getInputStream(), omd));
	}
	
	public void uploadTest(Long contentLength, InputStream input, String folderName, String storedFileName) throws IOException {	
		ObjectMetadata omd = new ObjectMetadata();
		//omd.setContentType("application/vnd.ms-excel"); // xls
		omd.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // xlsx
		omd.setContentLength(contentLength);
		omd.setHeader("filename", storedFileName);		
		amazonS3Client.putObject(new PutObjectRequest(bucket + "/"+folderName, storedFileName, input, omd));
	}
	
}
