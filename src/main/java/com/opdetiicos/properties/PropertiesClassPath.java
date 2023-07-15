package com.opdetiicos.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


import lombok.Data;

@Configuration
@Component
@Data
@PropertySource(value ="Classpath:application-production.properties",ignoreResourceNotFound = true)
public class PropertiesClassPath {
	
	
	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${database.mysql.host}")
	private String host;
	
	@Value("${database.mysql.port}")
	private String port;
	
	@Value("${spring.mail.username}")
	private String gmail;
	
	
}	
