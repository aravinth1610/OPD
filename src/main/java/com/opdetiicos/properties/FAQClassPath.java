package com.opdetiicos.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Configuration
@Data
@PropertySource(value="file:C:\\Users\\administrator.WIN-QI429VHMN3B\\faq.properties",ignoreResourceNotFound = true)
public class FAQClassPath {

	
	@Value("${free.count}")
	private Integer FreeCount;
	
	// ------------- Questions --------------
	
	@Value("${question1}")
	private String Question1;
	
	@Value("${question2}")
	private String Question2;
	
	@Value("${question3}")
	private String Question3;
	
	@Value("${question4}")
	private String Question4;
	
	@Value("${question5}")
	private String Question5;
	
	@Value("${question6}")
	private String Question6;
	
	@Value("${question7}")
	private String Question7;
	
	@Value("${question8}")
	private String Question8;
	
	@Value("${question9}")
	private String Question9;
	
	@Value("${question10}")
	private String Question10;
	
	@Value("${question11}")
	private String Question11;
	
	@Value("${question12}")
	private String Question12;
	
	@Value("${question13}")
	private String Question13;
	
	@Value("${question14}")
	private String Question14;
	
	@Value("${question15}")
	private String Question15;
	
	// ------------- Answer --------------
	
	@Value("${answer1}")
	private String answer1;
	
	@Value("${answer2}")
	private String answer2;
	
	@Value("${answer3}")
	private String answer3;
	
	@Value("${answer4}")
	private String answer4;
	
	@Value("${answer5}")
	private String answer5;
	
	@Value("${answer6}")
	private String answer6;
	
	@Value("${answer7}")
	private String answer7;
	
	@Value("${answer8}")
	private String answer8;
	
	@Value("${answer9}")
	private String answer9;
	
	@Value("${answer10}")
	private String answer10;
	
	@Value("${answer11}")
	private String Answer11;
	
	@Value("${answer12}")
	private String Answer12;
	
	@Value("${answer13}")
	private String Answer13;
	
	@Value("${answer14}")
	private String Answer14;
	
	@Value("${answer15}")
	private String answer15;
	
	
	
}
