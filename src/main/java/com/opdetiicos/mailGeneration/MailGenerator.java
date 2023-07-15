package com.opdetiicos.mailGeneration;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.opdetiicos.entity.RecipientOTP;
import com.opdetiicos.repository.RecipientOTPRepository;

@Service	
public class MailGenerator {
	 
	@Autowired
	private JavaMailSender sendingMailProcessor;
	
     @Autowired
     private RecipientOTPRepository recipientOTPRepository;
     
	@Value("${spring.mail.username}")
	private String user;
	
	
	
	private int randomNo;
	private int registeredRandomNo;
    
	public boolean otpMailGenerator(String gmailID)
	{
		 this.randomNo =((int)(Math.random()*9000) + 1000);
	     SimpleMailMessage messager=new SimpleMailMessage();
	     
	       RecipientOTP forgotOTPVerify = new RecipientOTP();
	       forgotOTPVerify.setRecipientUser(gmailID);
	       forgotOTPVerify.setOTPUpdatedUser(this.randomNo);
	     
	try
    {
 	messager.setFrom(user);
	messager.setTo(gmailID);
	messager.setSubject("Outpatient Department OTP");
	messager.setText("Dear OPD Etiicos User, Enter OTP for Forgot Password to Login Website.OTP is "+this.randomNo+" Kindly take a look");
	recipientOTPRepository.save(forgotOTPVerify);
	this.sendingMailProcessor.send(messager);
	
	return true;
    }catch (Exception e) {
    	e.printStackTrace();
    	return false;
	}
	}
	
	public boolean reigsteredOTPGenerator(String gmailID)
	{
		this.registeredRandomNo =((int)(Math.random()*9000) + 1000);
	    SimpleMailMessage messager=new SimpleMailMessage();
	    
	   RecipientOTP registerVerify = new RecipientOTP();
	   registerVerify.setRecipientUser(gmailID);
	   registerVerify.setOTPUpdatedUser(this.registeredRandomNo);
	    
	try
    {
 	messager.setFrom(user);
	messager.setTo(gmailID);
	messager.setSubject("Outpatient Department OTP");
	messager.setText("Dear OPD Etiicos User, Enter OTP to Register Website. OTP is "+this.registeredRandomNo+" Kindly take a look");
	this.sendingMailProcessor.send(messager);
	recipientOTPRepository.save(registerVerify);
	return true;
    }catch (Exception e) {
    	e.printStackTrace();
    	return false;
	}
	}
	
	public boolean paymentOTPGenerator(String gmailID)
	{
		this.registeredRandomNo =((int)(Math.random()*9000) + 1000);
	    SimpleMailMessage messager=new SimpleMailMessage();
	    
	   RecipientOTP registerVerify = new RecipientOTP();
	   registerVerify.setRecipientUser(gmailID);
	   registerVerify.setOTPUpdatedUser(this.registeredRandomNo);
	    
	try
    {
 	messager.setFrom(user);
	messager.setTo(gmailID);
	messager.setSubject("Payment Outpatient Department OTP");
	messager.setText("Dear OPD Etiicos User, Enter OTP for payment register gmail ID verify Website. OTP is "+this.registeredRandomNo+" Kindly take a look");
	this.sendingMailProcessor.send(messager);
	recipientOTPRepository.save(registerVerify);
	return true;
    }catch (Exception e) {
    	e.printStackTrace();
    	return false;
	}
	}
	
}
