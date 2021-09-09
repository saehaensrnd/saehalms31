package com.edutem.lms.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSConfiguration {

	private String accessKey = "AKIAV7CZSOIXO2RUWZBI";
	private String secretKey = "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";
	
	@Bean
	public AWSCredentials awsCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
	
	@Bean
	public AmazonS3 amazonS3Client(AWSCredentials awsCredentials) {
		AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_NORTHEAST_2).build();
		return amazonS3Client;
	}
	
}
