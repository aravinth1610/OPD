package com.opdetiicos.services;



import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.opdetiicos.OPDCustomeDB.DBOperator;
import com.opdetiicos.customDTO.ForgotPasswordDataObject;
import com.opdetiicos.customDTO.NurseRequestValues;
import com.opdetiicos.customDTO.PaymentVerifyMail;
import com.opdetiicos.customDTO.RegisterDataObject;
import com.opdetiicos.customDTO.TransationRazorpayDetails;
import com.opdetiicos.customDTO.UpdateCallRequestDataObject;
import com.opdetiicos.customDTO.UpdateWard;
import com.opdetiicos.customDTO.UserDetailsDataObject;
import com.opdetiicos.customEntity.OPDCustomEntity;
import com.opdetiicos.entity.OPDAllRequestCalls;
import com.opdetiicos.entity.OPDRegisteredEntity;
import com.opdetiicos.entity.RazorpayPaymentDetails;
import com.opdetiicos.localException.LocalException;
import com.opdetiicos.mailGeneration.MailGenerator;
import com.opdetiicos.opdcontroller.ViewController;
import com.opdetiicos.payment.PaymentServices;
import com.opdetiicos.properties.FAQClassPath;
import com.opdetiicos.properties.PropertiesClassPath;
import com.opdetiicos.repository.OPDEtiicosCallRequest;
import com.opdetiicos.repository.OPDEtiicosRepository;
import com.opdetiicos.repository.RazorpayPaymentDetailsRepository;
import com.opdetiicos.repository.RecipientOTPRepository;
import com.opdetiicos.securityController.CustomeUserDetails;

@Service
public class OPDEtiicosServices  {
	
	
	@Autowired
	private OPDEtiicosRepository registrationRepository;
	 
	@Autowired
	private OPDEtiicosCallRequest callRequestRepository;
	
	@Autowired
	private RecipientOTPRepository recipientOTPVerify;
	
	@Autowired
	private PropertiesClassPath propertiesFiles;
	
	@Autowired
	private MailGenerator generator;
	
	@Autowired
	private PaymentServices paymentGenerator;
	
	@Autowired
	private RazorpayPaymentDetailsRepository razorpayPaymentDetailsrepo;
	
	@Autowired
	private FAQClassPath faqlassPathPropertiesFiles;
	
	public OPDCustomEntity perDoctorAmount(HttpSession session)
	{
		PreparedStatement dbStatement = null;
 		PreparedStatement dbCreateStatement = null;
 		Connection OPDDBConnections = null;
 		Statement OPDDBStatement = null;
		
		 try {
	           	String OPDDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
				OPDDBConnections = DriverManager.getConnection(OPDDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());
				OPDDBStatement = OPDDBConnections.createStatement();

           String PerDoctorAmountQuery = "select price from per_doctor_price";
				
		    DBOperator OPDCustomeDataBase = new DBOperator(propertiesFiles.getHost(),propertiesFiles.getPort(),propertiesFiles.getUsername(),propertiesFiles.getPassword()); 
			
            ResultSet perDoctorAmountValue =  OPDCustomeDataBase.excecuteOPDQuery(OPDDBConnections,OPDDBStatement,dbStatement,dbCreateStatement,"etiicosaggregatedspace",PerDoctorAmountQuery);
           
            perDoctorAmountValue.next();
			 
            String amount = perDoctorAmountValue.getString("price");
            
            session.setAttribute("peramount", amount);
            
            Integer doctorAmount = Integer.parseInt((String) session.getAttribute("peramount"));
            
			return new OPDCustomEntity(200,doctorAmount,"success");
			
         }
          catch(SQLSyntaxErrorException syntaxException)
			{
				if(syntaxException.getMessage().contains("already exists"))
				{
					throw new LocalException("EXISTING_TABLE");
				}
				return new OPDCustomEntity(400,"non-success");
			}
		    catch(CommunicationsException communicationException)
			{
				communicationException.printStackTrace();
				throw new LocalException("INVALID_URL");
			}
		   catch(SQLException sqlException)
			{
				if(sqlException.getMessage().contains("Access denied"))
				{
					throw new LocalException("INVALID_CREDENTIALS");
				}
				return new OPDCustomEntity(400,"non-success");
			}
		 catch (Exception e) {
			 System.out.println("--- Getting per Doctor Amount Error ---");
			 return new OPDCustomEntity(400,"non-success");
		}
    	 finally 
 		{
 		    if (OPDDBConnections != null) {
 		        try {
 		        	OPDDBConnections.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
 		    
 		  if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (dbCreateStatement != null) {
		        try {
		        	dbCreateStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
 		    
 		}
	}
	
	public OPDCustomEntity gmailPaymentVerify(String gmail)
	{
		try
		{
	
		if(callRequestRepository.existsByGmail(gmail))
		{
			Boolean generatorRegisteredVerified = generator.paymentOTPGenerator(gmail);
			if(generatorRegisteredVerified)
			{
				String OTPhtml="<div class=\"mb-3\">\r\n"
						+ "                  <label for=\"username\" class=\"form-label\">One Time Password</label>\r\n"
						+ "                  <input type=\"text\" class=\"form-control\" id=\"otppyamentregister\" name=\"username\" placeholder=\"Enter your One Time Password\" autofocus maxlength=\"4\" autocomplete=\"off\" pattern=\"\\d{4}\" />\r\n"
						+ "                </div>\r\n"
						+ "                <button  onclick=\"verifypayment()\" id='otpbutton' class=\"btn btn-primary d-grid w-100\">Confirm</button>";
				return new OPDCustomEntity(200, OTPhtml, "success");
    		}
			else
			{
				return new OPDCustomEntity(101,"payment-OTP-nonsuccess");
			}
		}
		else
		{
			return new OPDCustomEntity(102,"payment-exists-nonsuccess");
		}
		
		}catch (Exception e) {
			System.out.println("---- payment registered OTP Error ----");
			return new OPDCustomEntity(400,"non-success");
		}
		
	}
	
	public OPDCustomEntity paymentVerifyMailID(PaymentVerifyMail paymentVerify)
	{
		
		try {
			
			if(paymentVerify.getOtp().equals(recipientOTPVerify.findByRecipientUser(paymentVerify.getGmail())))
			{
				String paymentTag ="<div class=\"mb-3\">\r\n"
						+ "                  <label for=\"sub\" class=\"form-label\">Per Doctor</label>\r\n"
						+ "                 <div><span><input style=\"display:inherit;width: 50%\" type=\"number\" class=\"form-control\" onkeyup=\"perDoctorCount()\" id=\"percount\" /></span><span id=\"countvalue\" class=\"text-dark\" style=\"float: right;font-weight: 700;padding: 7px;letter-spacing: 1px;\"></span></div>\r\n"
						+ "                 \r\n"
						+ "                \r\n"
						+ "                </div>\r\n"
						+ "                <button type=\"submit\" onclick='paymentconfrim()' class=\"btn btn-primary d-grid w-100\">Pay</button>";
				
				return new OPDCustomEntity(200, paymentTag, "success");
			}
			else
			{
				return new OPDCustomEntity(400, "not-validOTP-success");
			}
			
			
		}
		catch (Exception e) {
			System.out.println("---- payment gmail Verify Error ----");
			return new OPDCustomEntity(400,"non-success");
		}
	}
	
	
	public OPDCustomEntity createTransation(String amount)
	{
		
		return paymentGenerator.createTransation(amount);
		
	}
	
	public OPDCustomEntity successFullTransation(TransationRazorpayDetails transationDetails)
	{
		try {
			
			RazorpayPaymentDetails razorpayDetails = new RazorpayPaymentDetails();
			
			razorpayDetails.setGmailID(transationDetails.getGmail());
			razorpayDetails.setOrderID(transationDetails.getOrderID());
			razorpayDetails.setPaymentID(transationDetails.getPaymentID());
			razorpayDetails.setSignature(transationDetails.getSignature());
			razorpayDetails.setCount(transationDetails.getCount());
			
			razorpayPaymentDetailsrepo.save(razorpayDetails);
			
			  final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy");
			  String currentDate = format.format(new Date());
			  
			  
			  String[] dateSplit = currentDate.split("/");
			  
			  String monthValue = dateSplit[1];
				  
			  int month = Integer.parseInt(monthValue)+1;
			  
			  String nextMonthValue = Integer.toString(month);
			  
			  String nextMonth;
			  
			  if(nextMonthValue.length()==1)
			  {
			   nextMonth = currentDate.replace(monthValue,"0"+nextMonthValue);
			  }
			  else
			  {
			  nextMonth = currentDate.replace(monthValue,nextMonthValue);  
			  }
			  
			Integer isupdatedPayment = registrationRepository.updatePaymentDetails(nextMonth,"paid", transationDetails.getGmail());
			  
			if(isupdatedPayment == 1)
			{
				 return new OPDCustomEntity(200,"/opdetiicos/login","success");
			}
			else
			{
				return new OPDCustomEntity(400,"non-success");
			}
			
		}catch (Exception e) {
			System.out.println("---- payment successfull Trasation Error ----");
			return new OPDCustomEntity(400,"non-success");
		}
		
	}
	
	
   /*
    * It is used to register the data in table opd_all_call_request,
    * Register Successfully Redirect to Login page
    * </br>
    * @param  UserDetailsDataObject (hospital,gmail,password)
    * @param  HttpServletResponse 
    * </br>
    */
	public OPDCustomEntity registrationData(UserDetailsDataObject registeredSession,HttpServletResponse response,Model model)
	{
			
		DBOperator OPDCustomeDataBase = new DBOperator(propertiesFiles.getHost(),propertiesFiles.getPort(),propertiesFiles.getUsername(),propertiesFiles.getPassword()); 
		
		OPDAllRequestCalls requestCall = new OPDAllRequestCalls(); 
		
		String requestID = UUID.randomUUID().toString().substring(0,6);
		SimpleDateFormat dateTime=new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
	    String registeredTime=dateTime.format(new Date());
	    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    String Bcrypted=encoder.encode(registeredSession.getPassword());
	    try 
	    {
	    Boolean isGmailExists = callRequestRepository.existsByGmail(registeredSession.getGmail());
	    
	    if(!isGmailExists)
	    {
	 
	    	String hospitaNameContent=registeredSession.getHospital();
	    	
	    	String[] splitHospitalName=hospitaNameContent.split(" ");
	    	String hospitalName="";
	    	String hospitalIDName="";
	    	for(String hospitalnamelist : splitHospitalName)
	    	{
	    		String st=hospitalnamelist.substring(0,1);
	    	
	    		hospitalName+=st.toUpperCase()+hospitalnamelist.substring(1);
	    		hospitalName+=" ";
	    	}
	    
	    	for(String hospitalnamelist : splitHospitalName)
	    	{
	    		String st=hospitalnamelist.substring(0,1);
	    	
	    		hospitalIDName+=st.toUpperCase()+hospitalnamelist.substring(1);
	    		hospitalIDName+="$";
	    	}
	    	requestCall.setUser_id(requestID);
	    	requestCall.setGmail(registeredSession.getGmail());
	    	requestCall.setHospital(hospitalName);
	    	requestCall.setHospital_id(hospitalIDName.concat("$").concat(requestID));
	    	requestCall.setPassword(Bcrypted);
	    	requestCall.setRequest_time(registeredTime);
	    	requestCall.setState(registeredSession.getState());
	    	requestCall.setCity(registeredSession.getCity());
	    	requestCall.setRequest(false);
	    	
	    	
	    	List<String> opdTableData = new ArrayList<String>();
	    	opdTableData.add("user_id");
	    	opdTableData.add("gmail");
	    	opdTableData.add("hospital");
	    	opdTableData.add("hospital_id");
	    	opdTableData.add("password");
	    	opdTableData.add("request");
	    	opdTableData.add("request_time");
	    	opdTableData.add("state");
	    	opdTableData.add("city");
	    	opdTableData.add("approved_time");
	    	opdTableData.add("payment_status");
	    	opdTableData.add("next_payment_date");
	    	
	        Integer registerFirsFivetCount =	callRequestRepository.registerFirstUsers();
	        Boolean OPDRequestData;
	         
	        if(registerFirsFivetCount <= faqlassPathPropertiesFiles.getFreeCount())
	        {
	    	OPDRequestData = OPDCustomeDataBase.insertTableValues("etiicosaggregatedspace","opd_pending_approval_request",opdTableData,requestID,registeredSession.getGmail(),hospitalName,hospitalIDName.concat("$").concat(requestID),Bcrypted,false,registeredTime,registeredSession.getState(),registeredSession.getCity()," ","free","free");
	    	callRequestRepository.save(requestCall);
	        }
	        else
	        {
	        	OPDRequestData = OPDCustomeDataBase.insertTableValues("etiicosaggregatedspace","opd_pending_approval_request",opdTableData,requestID,registeredSession.getGmail(),hospitalName,hospitalIDName.concat("$").concat(requestID),Bcrypted,false,registeredTime,registeredSession.getState(),registeredSession.getCity()," ","unpaid"," ");
		    	callRequestRepository.save(requestCall);
	        }
	    	if(OPDRequestData)
	    	{
	    	return new OPDCustomEntity(200,"/opdetiicos/login","success");
	    	}else {return new OPDCustomEntity(400,"non-success");}
	    }
	    else
	    {
	    	return new OPDCustomEntity(400,"non-success");
	    	
	    }
	    }catch (Exception e) {
	    	System.out.println("---- Registered Error Occure ----");
	    	return new OPDCustomEntity(400,"non-success");
		}
    	
	}
	
	public OPDCustomEntity registerdGmailOTP(String gmail)
	{
		try
		{	
		if(!callRequestRepository.existsByGmail(gmail))
		{
			Boolean generatorRegisteredVerified = generator.reigsteredOTPGenerator(gmail);
			if(generatorRegisteredVerified)
			{
				String signupOTPHTML = "<label for=\"inputState\">One Time Password</label>\r\n"
						+ "              <div class=\"form-group\">\r\n"
						+ "		      			<input type=\"text\" autofocus maxlength=\"4\" class=\"form-control\" placeholder=\"One Time Password\"  id=\"pin\" pattern=\"\\d{4}\" required autocomplete=\"off\" />\r\n"
						+ "		      		</div>\r\n"
						+ "             <br>"
						+ "	          <div class=\"form-group\">\r\n"
						+ "	            	<button  onclick=validateSignup() class=\"form-control btn btn-primary submit px-3\">CONFIRM</button>\r\n"
						+ "	            </div>";
				
				return new OPDCustomEntity(200,signupOTPHTML,"success");
			}
			else
			{
				return new OPDCustomEntity(101,"OTP-nonsuccess");
			}
		}
		else
		{
			return new OPDCustomEntity(102,"exists-nonsuccess");
		}
		
		}catch (Exception e) {
			System.out.println("---- registered OTP Error ----");
			return new OPDCustomEntity(400,"non-success");
		}
		
	}
	
	public OPDCustomEntity signUpAuthentationVerify(RegisterDataObject registerDetails)
	{
		try
		{
		if(registerDetails.getOtp().equals(recipientOTPVerify.findByRecipientUser(registerDetails.getRegisterGmail()))) 
		{
			String signupVerifiedHTML="<label for=\"inputState\">Hospital</label>\r\n"
					+ "                <div class=\"form-group\">\r\n"
					+ "		      		<input type=\"text\" id=\"hospital\" autofocus=\"autofocus\" class=\"form-control\" placeholder=\"Hospital Name\" required> \r\n"
					+ "		      		</div>\r\n<br>"  
					+ "                <div class=\"form-group mb-3\">\r\n"
					+ "                  <label for=\"inputState\">State</label>\r\n"
					+ "                  <select class=\"form-control\" onchange=\"stateSelected()\" id=\"inputState\">\r\n"
					+ "                                      <option value=\"\">Select State</option>\r\n"
					+ "                                      <option value=\"Andra Pradesh\">Andra Pradesh</option>\r\n"
					+ "                                      <option value=\"Arunachal Pradesh\">Arunachal Pradesh</option>\r\n"
					+ "                                      <option value=\"Assam\">Assam</option>\r\n"
					+ "                                      <option value=\"Bihar\">Bihar</option>\r\n"
					+ "                                      <option value=\"Chhattisgarh\">Chhattisgarh</option>\r\n"
					+ "                                      <option value=\"Goa\">Goa</option>\r\n"
					+ "                                      <option value=\"Gujarat\">Gujarat</option>\r\n"
					+ "                                      <option value=\"Haryana\">Haryana</option>\r\n"
					+ "                                      <option value=\"Himachal Pradesh\">Himachal Pradesh</option>\r\n"
					+ "                                      <option value=\"Jammu and Kashmir\">Jammu and Kashmir</option>\r\n"
					+ "                                      <option value=\"Jharkhand\">Jharkhand</option>\r\n"
					+ "                                      <option value=\"Karnataka\">Karnataka</option>\r\n"
					+ "                                      <option value=\"Kerala\">Kerala</option>\r\n"
					+ "                                      <option value=\"Madya Pradesh\">Madya Pradesh</option>\r\n"
					+ "                                      <option value=\"Maharashtra\">Maharashtra</option>\r\n"
					+ "                                      <option value=\"Manipur\">Manipur</option>\r\n"
					+ "                                      <option value=\"Meghalaya\">Meghalaya</option>\r\n"
					+ "                                      <option value=\"Mizoram\">Mizoram</option>\r\n"
					+ "                                      <option value=\"Nagaland\">Nagaland</option>\r\n"
					+ "                                      <option value=\"Orissa\">Orissa</option>\r\n"
					+ "                                      <option value=\"Punjab\">Punjab</option>\r\n"
					+ "                                      <option value=\"Rajasthan\">Rajasthan</option>\r\n"
					+ "                                      <option value=\"Sikkim\">Sikkim</option>\r\n"
					+ "                                      <option value=\"Tamil Nadu\">Tamil Nadu</option>\r\n"
					+ "                                      <option value=\"Telangana\">Telangana</option>\r\n"
					+ "                                      <option value=\"Tripura\">Tripura</option>\r\n"
					+ "                                      <option value=\"Uttaranchal\">Uttaranchal</option>\r\n"
					+ "                                      <option value=\"Uttar Pradesh\">Uttar Pradesh</option>\r\n"
					+ "                                      <option value=\"West Bengal\">West Bengal</option>\r\n"
					+ "                                      <option disabled style=\"background-color:#aaa; color:#fff\">UNION Territories</option>\r\n"
					+ "                                      <option value=\"Andaman and Nicobar Islands\">Andaman and Nicobar Islands</option>\r\n"
					+ "                                      <option value=\"Chandigarh\">Chandigarh</option>\r\n"
					+ "                                      <option value=\"Dadar and Nagar Haveli\">Dadar and Nagar Haveli</option>\r\n"
					+ "                                      <option value=\"Daman and Diu\">Daman and Diu</option>\r\n"
					+ "                                      <option value=\"Delhi\">Delhi</option>\r\n"
					+ "                                      <option value=\"Lakshadeep\">Lakshadeep</option>\r\n"
					+ "                                      <option value=\"Pondicherry\">Pondicherry</option>\r\n"
					+ "                                    </select>\r\n"
					+ "                </div>\r\n"
					+ "                <div class=\"form-group mb-3\">\r\n"
					+ "                  <label for=\"inputDistrict\">City</label>\r\n"
					+ "                  <select class=\"form-control\" id=\"inputDistrict\">\r\n"
					+ "                      <option value=\"\">-- select City -- </option>\r\n"
					+ "                  </select>\r\n"
					+ "                </div>"
					+ "	                <div class=\"form-group\">\r\n"
					+ "                  <label for=\"inputState\">Password</label>\r\n"
					+ "	               <input id=\"password-field\" type=\"password\"  autofocus=\"autofocus\" class=\"form-control\" placeholder=\"Password\" required> \r\n"
					+ "	              <span onclick=\"togglePasword()\" id=\"togglepassword\" toggle=\"#password-field\" class=\"fa fa-fw fa-eye field-icon toggle-pasword\"></span>\r\n"
					+ "	            </div>\r\n"
					+ "              <br>"
					+ "	            <div class=\"form-group\">\r\n"
					+ "	            	<button  onclick=signupsubmit() class=\"form-control btn btn-primary submit px-3\">Sign Up</button>\r\n"
					+ "	            </div>";
			
			return new OPDCustomEntity(200,signupVerifiedHTML,"success");
		}
		else
		{
			return new OPDCustomEntity(400,"non-success");
		}
		}catch (Exception e) {
			System.out.println("---- signup Authenticatin Error ----");
			return new OPDCustomEntity(400,"non-success");
	}
	}
	
	
	public OPDCustomEntity forgotPasswordOTP(String gmail)
	{
	
		try
		{
		if(registrationRepository.existsByGmail(gmail))
		{
			Boolean generatorVerified = generator.otpMailGenerator(gmail);
			if(generatorVerified)
			{
				return new OPDCustomEntity(200,"success");
			}
			else
			{
				return new OPDCustomEntity(400,"non-success");
			}
		}
		else
		{
			return new OPDCustomEntity(400,"non-success");
		}
		}catch (Exception e) {
			System.out.println("---- Forgot OTP Error ----");
			return new OPDCustomEntity(400,"non-success");
		}
	}
	
	public Boolean updatingForgotPassword(ForgotPasswordDataObject forgotpasswordData)
	{
		try
		{
		if(forgotpasswordData.getPin().equals(recipientOTPVerify.findByRecipientUser(forgotpasswordData.getGmailaddress()))) 
		{
			String gmail = forgotpasswordData.getGmailaddress();
			String forgotPassword = forgotpasswordData.getForgotpassword();
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			registrationRepository.updatePassword(encoder.encode(forgotPassword),gmail);
			return true;
		}
		else
		{
			return false;
		}
		}catch (Exception e) {
			System.out.println("---- Updateforgot OTP Error ------");
			return false;
		}
	}
     
     public Boolean UpdateDoctorStauts(UpdateCallRequestDataObject UpdatestatusCheck)
     {
    	 
    	PreparedStatement dbStatement = null;
 		PreparedStatement dbCreateStatement = null;
 		Connection OPDDBConnections = null;
 		Statement OPDDBStatement = null;
 		 String startTime = "";
 		 Boolean isTimeElapsedExists = false;
    	 Boolean statusUpdated=false;
    	 String total;
    	 try {
    		 
    		 DBOperator OPDCustomeDataBase = new DBOperator(propertiesFiles.getHost(),propertiesFiles.getPort(),propertiesFiles.getUsername(),propertiesFiles.getPassword()); 
    	    	
    		 if(UpdatestatusCheck.getStatus())
    		 {
    			 
    		Boolean statusUpdatedTrue = OPDCustomeDataBase.updateTableValues(UpdatestatusCheck.getDataBaseName(), UpdatestatusCheck.getDataBaseName().concat("_record"),"callno",UpdatestatusCheck.getCallno(),"status",UpdatestatusCheck.getStatus());  //"starttime","","endtime","","duration","","endview",""
    		if(statusUpdatedTrue)
    		{
    			Boolean insertedCallLog = OPDCustomeDataBase.insertTableValues(UpdatestatusCheck.getDataBaseName(),UpdatestatusCheck.getDataBaseName().concat("_calllog"),Arrays.asList("callno","doctordata","starttime","endtime","duration","endview","doctor_name"),UpdatestatusCheck.getCallno(),UpdatestatusCheck.getDataTyped(),UpdatestatusCheck.getStartTime(),"","","",UpdatestatusCheck.getDoctorName());//,"starttime","endtime","duration","endview"  ,"","","",""
    	
         if(insertedCallLog){statusUpdated=true;}else {statusUpdated=false;}
         
    		}
    		else{statusUpdated=false;}
    		
    		}
    		 else
    		 {
    		Boolean	 statusUpdatedFalse = OPDCustomeDataBase.updateTableValues(UpdatestatusCheck.getDataBaseName(), UpdatestatusCheck.getDataBaseName().concat("_record"),"callno",UpdatestatusCheck.getCallno(),"status",UpdatestatusCheck.getStatus());  //"endtime",UpdatestatusCheck.getTime(),"duration",UpdatestatusCheck.getDuration(),"endview",UpdatestatusCheck.getEndView()
   
     			 if(statusUpdatedFalse)
     			 {
                String checkTimeElapsedQuery = "select endtime from "+UpdatestatusCheck.getDataBaseName().concat("_calllog")+" where callno='"+UpdatestatusCheck.getCallno()+"' and endtime='';";
 	            String StartTimeQuery ="select starttime from "+UpdatestatusCheck.getDataBaseName().concat("_calllog")+" where callno='"+UpdatestatusCheck.getCallno()+"' and endtime='';";
 	            try {
 	           	String OPDDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
 				OPDDBConnections = DriverManager.getConnection(OPDDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());
 				OPDDBStatement = OPDDBConnections.createStatement();
 	            	
                 ResultSet StartTimeValue =  OPDCustomeDataBase.excecuteOPDQuery(OPDDBConnections,OPDDBStatement,dbStatement,dbCreateStatement,UpdatestatusCheck.getDataBaseName(),StartTimeQuery);
 	             
 	             StartTimeValue.next();
 	             
 	             startTime = StartTimeValue.getString("starttime");

 				
 	            }
 	           catch(SQLSyntaxErrorException syntaxException)
 				{
 					if(syntaxException.getMessage().contains("already exists"))
 					{
 						throw new LocalException("EXISTING_TABLE");
 					}
 				}
 			    catch(CommunicationsException communicationException)
 				{
 				throw new LocalException("INVALID_URL");
 				}
 			   catch(SQLException sqlException)
 				{
 					if(sqlException.getMessage().contains("Access denied"))
 					{
 						throw new LocalException("INVALID_CREDENTIALS");
 					}
 				}
 	             	             
 	             if(!startTime.equals(null) && !startTime.isEmpty())
 	             {
 	    		 SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy hh:mm:ss:SSS a");
    			 
    			 Date startTimeFormat =formater.parse(startTime);
 	             Date endTimeFormat = formater.parse(UpdatestatusCheck.getEndTime()); 
 	            
 	           
 	            	long difference = endTimeFormat.getTime() - startTimeFormat.getTime();
 	                long diffSeconds = difference / 1000 % 60;
 	                long diffMinutes = difference / (60 * 1000) % 60;
 	                long diffHours = difference / (60 * 60 * 1000) % 24;
 	                long diffDays = difference / (24 * 60 * 60 * 1000);

 	              String hourDifference = String.valueOf(diffHours);
 	              String minDifference = String.valueOf(diffMinutes);
 	              String  secDifference = String.valueOf(diffSeconds);
 	              String  daysDifference = String.valueOf(diffDays);
 	                
 	                if(diffSeconds<10)
 	                {
 	                	secDifference = "0"+diffSeconds;
 	                }
 	                if(diffMinutes<10)
 	                {
 	                	minDifference = "0"+diffMinutes;
 	                }
 	                if(diffHours<10)
 	                {
 	                	hourDifference = "0"+diffHours;
 	                }
 	                if(diffDays>=1)
 	                {
 	                	int noOfDays = Integer.parseInt(daysDifference);
 	                    int noOfHours = Integer.parseInt(hourDifference);
 	                    int totDayHours = noOfDays*24;
 	                    int totalHours = totDayHours + noOfHours;
 	                    hourDifference = Integer.toString(totalHours);
 	                }
 	                
 	                 total = hourDifference+":"+minDifference+":"+secDifference;
 	                 
 	                 
 	                 
 	                try {
 	    	           	String OPDDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
 	    				OPDDBConnections = DriverManager.getConnection(OPDDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());
 	    				OPDDBStatement = OPDDBConnections.createStatement();

 	                 
 	                 
 	                  ResultSet isEmptyTables =  OPDCustomeDataBase.excecuteOPDQuery(OPDDBConnections,OPDDBStatement,dbStatement,dbCreateStatement,UpdatestatusCheck.getDataBaseName(),checkTimeElapsedQuery);
 	 	            
 	    			  isEmptyTables.next();
 	    			 
 	    			  isTimeElapsedExists = isEmptyTables.getString("endtime").isEmpty();
 	    			
 	               }
 	 	           catch(SQLSyntaxErrorException syntaxException)
 	 				{
 	 					if(syntaxException.getMessage().contains("already exists"))
 	 					{
 	 						throw new LocalException("EXISTING_TABLE");
 	 					}
 	 				}
 	 			    catch(CommunicationsException communicationException)
 	 				{
 	 					communicationException.printStackTrace();
 	 					throw new LocalException("INVALID_URL");
 	 				}
 	 			   catch(SQLException sqlException)
 	 				{
 	 					if(sqlException.getMessage().contains("Access denied"))
 	 					{
 	 						throw new LocalException("INVALID_CREDENTIALS");
 	 					}
 	 					sqlException.printStackTrace();
 	 				}
 	          	 finally 
 	       		{
 	       		    if (OPDDBConnections != null) {
 	       		        try {
 	       		        	OPDDBConnections.close();
 	       		        } catch (SQLException e) { /* Ignored */}
 	       		    }
 	       		    
 	       		  if (dbStatement != null) {
 	     		        try {
 	     		        	dbStatement.close();
 	     		        } catch (SQLException e) { /* Ignored */}
 	     		    }
 	     		    if (dbCreateStatement != null) {
 	     		        try {
 	     		        	dbCreateStatement.close();
 	     		        } catch (SQLException e) { /* Ignored */}
 	     		    }
 	     		    if (OPDDBStatement != null) 
 	     		    {
 	     		        try 
 	     		        {
 	     		        	OPDDBStatement.close();
 	     		        } catch (SQLException e) { /* Ignored */}
 	     		    }
 	       		    
 	       		}
 	                 
 	      		
 	    			 Boolean updateEndValues=false;
 	             if(isTimeElapsedExists)
 	             {
 	            	updateEndValues  = OPDCustomeDataBase.updateTableValues(UpdatestatusCheck.getDataBaseName(), UpdatestatusCheck.getDataBaseName().concat("_calllog"),"starttime",startTime,"endtime",UpdatestatusCheck.getEndTime(),"duration",total,"endview",UpdatestatusCheck.getEndView());
 	             }
 	             else{statusUpdated=false;}
 	             
 	             if(updateEndValues) {statusUpdated=true;}else{statusUpdated=false;}
 	             
 	             }else{statusUpdated=false;}
     			 }else{statusUpdated=false;}
 	             } 
    		 
		} 
    	 
    	  catch (Exception e) {
			System.out.println("---- Update Doctor Log Error ----");
			return false;
		}
    	
    	 return statusUpdated;
     }
     
     public OPDCustomEntity recipientFullDatas(String DatabaseName)
     {
    	 PreparedStatement dbStatement = null;
  		PreparedStatement selectTableStatement = null;
  		Connection OPDDBConnections = null;
  		Statement OPDDBStatement = null;

    	 
    	 try {
    		 DBOperator OPDCustomeDataBase = new DBOperator(); 
    	    
    		 String OPDDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
    		 OPDDBConnections = DriverManager.getConnection(OPDDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());
    	
    		 
    	ResultSet gettingTableValus = OPDCustomeDataBase.getTableVaues(OPDDBConnections,OPDDBStatement,dbStatement,selectTableStatement,DatabaseName,DatabaseName.concat("_record"),"","","*");
    	
    	List<String> callNO = new ArrayList<String>();
    	List<String> content = new ArrayList<String>();
    	List<String> status = new ArrayList<String>();
    	List<String> doctorName = new ArrayList<String>();
    	List<String> wardName = new ArrayList<String>();
    	List<String> audioStatus = new ArrayList<String>();
    	
			while(gettingTableValus.next())
			{
				callNO.add(gettingTableValus.getString("callno"));
				content.add(gettingTableValus.getString("doctordata"));
				status.add(gettingTableValus.getString("status"));
				doctorName.add(gettingTableValus.getString("doctor_name"));
				wardName.add(gettingTableValus.getString("doctor_ward"));
				audioStatus.add(gettingTableValus.getString("audio_status"));
			}
	 	
    	Map<String, Object> recipientDatas = new LinkedHashMap<String, Object>();
    	recipientDatas.put("callno", callNO);
    	recipientDatas.put("doctordata", content);
    	recipientDatas.put("statusdata", status);
    	recipientDatas.put("doctorname", doctorName);
    	recipientDatas.put("wardname", wardName);
    	recipientDatas.put("audiostatus", audioStatus);
    	return new OPDCustomEntity(200,recipientDatas,"success");
    	
    	} 
    	catch(SQLSyntaxErrorException syntaxException)
  		{
  			if(syntaxException.getMessage().contains("already exists"))
  			{
  				throw new LocalException("EXISTING_TABLE");
  			}
  			return new OPDCustomEntity(400,"non-success");
  		}
  		catch(CommunicationsException communicationException)
  		{
  			communicationException.printStackTrace();
  			throw new LocalException("INVALID_URL");
  		}
  		catch(SQLException sqlException)
  		{
  			if(sqlException.getMessage().contains("Unknown database"))
  			{
  				throw new LocalException("FALSE_DATABASE");
  			}
  			else if(sqlException.getMessage().contains("Access denied"))
  			{
  				throw new LocalException("INVALID_CREDENTIALS");
  			}
  			sqlException.printStackTrace();
  			return new OPDCustomEntity(400,"non-success");
  		}
    	  catch (Exception e) {
    		System.out.println("---- Recipient Full Data Error ----");
    		return new OPDCustomEntity(400,"non-success");
		}
     	finally 
   		{
   		    if (OPDDBConnections != null) {
   		        try {
   		        	OPDDBConnections.close();
   		        } catch (SQLException e) { /* Ignored */}
   		    }
   		    
   		  if (dbStatement != null) {
 		        try {
 		        	dbStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
 		    if (selectTableStatement != null) {
 		        try {
 		        	selectTableStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
 		    if (OPDDBStatement != null) 
 		    {
 		        try 
 		        {
 		        	OPDDBStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
   		    
   		}
     }
  
     public OPDCustomEntity recipitentCallLog(String databaseName)
     {
    	 
    	  	List<String> recipientcallLogNO = new ArrayList<String>();
    	  	List<String> recipientDoctorName = new ArrayList<String>();
            List<String> recipientcallLogContent = new ArrayList<String>();
         	List<String> recipientcallLogStartTime = new ArrayList<String>();
         	List<String> recipientcallLogEndTime = new ArrayList<String>();
         	List<String> recipientcallLogduration = new ArrayList<String>();
         	List<String> recipientEndViewcallLog = new ArrayList<String>();
         	List<String> doctorRoomNo = new ArrayList<String>();
        	List<String> doctorDepartment = new ArrayList<String>();
     
    	 
    	PreparedStatement dbStatement = null;
  		PreparedStatement selectTableStatement = null;
  		Connection OPDDBConnections = null;
  		Statement OPDDBStatement = null;
   
  		try {
    		 DBOperator OPDCustomeDataBase = new DBOperator();
    		 String OPDDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
    		 OPDDBConnections = DriverManager.getConnection(OPDDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());
    	
    		 ResultSet gettingRecipitentTableValusCount = OPDCustomeDataBase.getTableVaues(OPDDBConnections,OPDDBStatement,dbStatement,selectTableStatement,databaseName, databaseName.concat("_calllog"),"endtime","","count(*)");

    		 gettingRecipitentTableValusCount.next();
    		 
    		 int callLogCount = gettingRecipitentTableValusCount.getInt("count(*)");
    		 
    		 
    		 if(callLogCount > 90)
    		 {
    			 System.out.println("--- Greater Then 90 ---");
    			 String deleteCallLog = "delete from "+databaseName+"_calllog limit 90";
    			 
    			 OPDCustomeDataBase.excecuteOPDBooleanQuery(databaseName, deleteCallLog);
    		 }
    		 
    		 
    		 ResultSet gettingRecipitentTableValus = OPDCustomeDataBase.getTableVaues(OPDDBConnections,OPDDBStatement,dbStatement,selectTableStatement,databaseName, databaseName.concat("_calllog"),"endtime","","*");
    	
    	     while(gettingRecipitentTableValus.next())
  			{
    	    	
  				recipientcallLogNO.add(gettingRecipitentTableValus.getString("callno"));
  				recipientDoctorName.add(gettingRecipitentTableValus.getString("doctor_name"));
  				recipientcallLogContent.add(gettingRecipitentTableValus.getString("doctordata"));
  				recipientcallLogStartTime.add(gettingRecipitentTableValus.getString("starttime"));
  				recipientcallLogEndTime.add(gettingRecipitentTableValus.getString("endtime"));
  				recipientcallLogduration.add(gettingRecipitentTableValus.getString("duration"));
  				recipientEndViewcallLog.add(gettingRecipitentTableValus.getString("endview"));
  				doctorRoomNo.add(gettingRecipitentTableValus.getString("doctor_roomno"));
  				doctorDepartment.add(gettingRecipitentTableValus.getString("doctor_department"));
  			}
    		 }
    	     catch(SQLSyntaxErrorException syntaxException)
    	  		{
    	  			if(syntaxException.getMessage().contains("already exists"))
    	  			{
    	  				throw new LocalException("EXISTING_TABLE");
    	  			}
    	  			return new OPDCustomEntity(400,"non-success");
    	  		}
    	  		catch(CommunicationsException communicationException)
    	  		{
    	  			communicationException.printStackTrace();
    	  			throw new LocalException("INVALID_URL");
    	  		}
    	  		catch(SQLException sqlException)
    	  		{
    	  			if(sqlException.getMessage().contains("Unknown database"))
    	  			{
    	  				throw new LocalException("FALSE_DATABASE");
    	  			}
    	  			else if(sqlException.getMessage().contains("Access denied"))
    	  			{
    	  				throw new LocalException("INVALID_CREDENTIALS");
    	  			}
    	  			sqlException.printStackTrace();
    	  			
    	  			return new OPDCustomEntity(400,"non-success");
    	  		}
    	    	 catch (Exception e) {
    			
    				System.out.println("---- NurseActive Status Check Error ----");
    				return new OPDCustomEntity(400,"non-success");
    				
    			}
    	 finally 
   		{
   		    if (OPDDBConnections != null) {
   		        try {
   		        	OPDDBConnections.close();
   		        } catch (SQLException e) { /* Ignored */}
   		    }
   		    
   		  if (dbStatement != null) {
 		        try {
 		        	dbStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
 		    if (selectTableStatement != null) {
 		        try {
 		        	selectTableStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
 		    if (OPDDBStatement != null) 
 		    {
 		        try 
 		        {
 		        	OPDDBStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
   		    
   		}
    	 
    	 try
    	 {
    		 
    		 DBOperator OPDCustomeDataBase = new DBOperator();
    		 String OPDDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
    		 OPDDBConnections = DriverManager.getConnection(OPDDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());

    		 
    		 ResultSet gettingRecipitentLastTableValus = OPDCustomeDataBase.getTableVaues(OPDDBConnections,OPDDBStatement,dbStatement,selectTableStatement,databaseName, databaseName.concat("_calllog"),"endview","Nurse","*");
    	  
    	     while(gettingRecipitentLastTableValus.next())
  			{
  				recipientcallLogNO.add(gettingRecipitentLastTableValus.getString("callno"));
  				recipientDoctorName.add(gettingRecipitentLastTableValus.getString("doctor_name"));
  				recipientcallLogContent.add(gettingRecipitentLastTableValus.getString("doctordata"));
  				recipientcallLogStartTime.add(gettingRecipitentLastTableValus.getString("starttime"));
  				recipientcallLogEndTime.add(gettingRecipitentLastTableValus.getString("endtime"));
  				recipientcallLogduration.add(gettingRecipitentLastTableValus.getString("duration"));
  				recipientEndViewcallLog.add(gettingRecipitentLastTableValus.getString("endview"));
  				doctorRoomNo.add(gettingRecipitentLastTableValus.getString("doctor_roomno"));
  				doctorDepartment.add(gettingRecipitentLastTableValus.getString("doctor_department"));
  				
  				
  			}
    	     
    	     
    	 }
    	 catch(SQLSyntaxErrorException syntaxException)
	  		{
	  			if(syntaxException.getMessage().contains("already exists"))
	  			{
	  				throw new LocalException("EXISTING_TABLE");
	  			}
	  			return new OPDCustomEntity(400,"non-success");
	  		}
	  		catch(CommunicationsException communicationException)
	  		{
	  			communicationException.printStackTrace();
	  			throw new LocalException("INVALID_URL");
	  		}
	  		catch(SQLException sqlException)
	  		{
	  			if(sqlException.getMessage().contains("Unknown database"))
	  			{
	  				throw new LocalException("FALSE_DATABASE");
	  			}
	  			else if(sqlException.getMessage().contains("Access denied"))
	  			{
	  				throw new LocalException("INVALID_CREDENTIALS");
	  			}
	  			sqlException.printStackTrace();
	  			
	  			return new OPDCustomEntity(400,"non-success");
	  		}
	    	 catch (Exception e) {
			
				System.out.println("---- NurseActive Status Check Error ----");
				return new OPDCustomEntity(400,"non-success");
				
			}
    	 finally 
    		{
    		    if (OPDDBConnections != null) {
    		        try {
    		        	OPDDBConnections.close();
    		        } catch (SQLException e) { /* Ignored */}
    		    }
    		    
    		  if (dbStatement != null) {
  		        try {
  		        	dbStatement.close();
  		        } catch (SQLException e) { /* Ignored */}
  		    }
  		    if (selectTableStatement != null) {
  		        try {
  		        	selectTableStatement.close();
  		        } catch (SQLException e) { /* Ignored */}
  		    }
  		    if (OPDDBStatement != null) 
  		    {
  		        try 
  		        {
  		        	OPDDBStatement.close();
  		        } catch (SQLException e) { /* Ignored */}
  		    }
    		    
    		}
    	 
    	 try {
    	 
    	 Map<String, Object>  recipientLogDatas = new LinkedHashMap<String, Object>();
    	 recipientLogDatas.put("recipientlogcallno", recipientcallLogNO);
    	 recipientLogDatas.put("recipientdoctorname", recipientDoctorName);
    	 recipientLogDatas.put("recipientlogdata", recipientcallLogContent);
    	 recipientLogDatas.put("recipientlogstarttime", recipientcallLogStartTime);
    	 recipientLogDatas.put("recipientlogendtime", recipientcallLogEndTime);
    	 recipientLogDatas.put("recipientlogduration", recipientcallLogduration);
    	 recipientLogDatas.put("recipientlogendview", recipientEndViewcallLog);
    	 recipientLogDatas.put("recipientroomno", doctorRoomNo);
    	 recipientLogDatas.put("recipientdepartment", doctorDepartment);
    	 
     	 return new OPDCustomEntity(200,recipientLogDatas,"success");
    	 
    	 }catch (Exception e) {
    		 System.out.println("---- Recipient Call Log Error ----");
    		 return new OPDCustomEntity(400,"non-success");
     		
 		}
    	 
     }
     
     public void pauseAudioStatus(NurseRequestValues nurseAudioPause)
     {
    	 DBOperator OPDCustomeDataBase = new DBOperator(propertiesFiles.getHost(),propertiesFiles.getPort(),propertiesFiles.getUsername(),propertiesFiles.getPassword()); 
    	 
    OPDCustomeDataBase.updateTableValues(nurseAudioPause.getDataBaseName(),nurseAudioPause.getDataBaseName().concat("_record"),"callno",nurseAudioPause.getTableName(),"audio_status","pause");
     	 
     } 
     
     public OPDCustomEntity recipitentDwnCallLog(String databaseName)
     {
    	 
    	  	List<String> recipientcallLogNO = new ArrayList<String>();
    	  	List<String> recipientDoctorName = new ArrayList<String>();
            List<String> recipientcallLogContent = new ArrayList<String>();
         	List<String> recipientcallLogStartTime = new ArrayList<String>();
         	List<String> recipientcallLogEndTime = new ArrayList<String>();
         	List<String> recipientcallLogduration = new ArrayList<String>();
         	List<String> recipientEndViewcallLog = new ArrayList<String>();
         	List<String> doctorRoomNo = new ArrayList<String>();
        	List<String> doctorDepartment = new ArrayList<String>();
     
    	 
    	PreparedStatement dbStatement = null;
  		PreparedStatement selectTableStatement = null;
  		Connection OPDDBConnections = null;
  		Statement OPDDBStatement = null;
   
  		try {
    		 DBOperator OPDCustomeDataBase = new DBOperator();
    		 String OPDDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
    		 OPDDBConnections = DriverManager.getConnection(OPDDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());
    	
    		 ResultSet gettingRecipitentTableValus = OPDCustomeDataBase.getTableVaues(OPDDBConnections,OPDDBStatement,dbStatement,selectTableStatement,databaseName, databaseName.concat("_calllog"),"endtime","","*");
    	
    	     while(gettingRecipitentTableValus.next())
  			{
    	    	
  				recipientcallLogNO.add(gettingRecipitentTableValus.getString("callno"));
  				recipientDoctorName.add(gettingRecipitentTableValus.getString("doctor_name"));
  				recipientcallLogContent.add(gettingRecipitentTableValus.getString("doctordata"));
  				recipientcallLogStartTime.add(gettingRecipitentTableValus.getString("starttime"));
  				recipientcallLogEndTime.add(gettingRecipitentTableValus.getString("endtime"));
  				recipientcallLogduration.add(gettingRecipitentTableValus.getString("duration"));
  				recipientEndViewcallLog.add(gettingRecipitentTableValus.getString("endview"));
  				doctorRoomNo.add(gettingRecipitentTableValus.getString("doctor_roomno"));
  				doctorDepartment.add(gettingRecipitentTableValus.getString("doctor_department"));
  			}
    		 }
    	     catch(SQLSyntaxErrorException syntaxException)
    	  		{
    	  			if(syntaxException.getMessage().contains("already exists"))
    	  			{
    	  				throw new LocalException("EXISTING_TABLE");
    	  			}
    	  			return new OPDCustomEntity(400,"non-success");
    	  		}
    	  		catch(CommunicationsException communicationException)
    	  		{
    	  			communicationException.printStackTrace();
    	  			throw new LocalException("INVALID_URL");
    	  		}
    	  		catch(SQLException sqlException)
    	  		{
    	  			if(sqlException.getMessage().contains("Unknown database"))
    	  			{
    	  				throw new LocalException("FALSE_DATABASE");
    	  			}
    	  			else if(sqlException.getMessage().contains("Access denied"))
    	  			{
    	  				throw new LocalException("INVALID_CREDENTIALS");
    	  			}
    	  			sqlException.printStackTrace();
    	  			
    	  			return new OPDCustomEntity(400,"non-success");
    	  		}
    	    	 catch (Exception e) {
    			
    				System.out.println("---- Excel Dwn NurseActive Status Check Error ----");
    				return new OPDCustomEntity(400,"non-success");
    				
    			}
    	 finally 
   		{
   		    if (OPDDBConnections != null) {
   		        try {
   		        	OPDDBConnections.close();
   		        } catch (SQLException e) { /* Ignored */}
   		    }
   		    
   		  if (dbStatement != null) {
 		        try {
 		        	dbStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
 		    if (selectTableStatement != null) {
 		        try {
 		        	selectTableStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
 		    if (OPDDBStatement != null) 
 		    {
 		        try 
 		        {
 		        	OPDDBStatement.close();
 		        } catch (SQLException e) { /* Ignored */}
 		    }
   		    
   		}
    	 
    	 try
    	 {
    		 
    		 DBOperator OPDCustomeDataBase = new DBOperator();
    		 String OPDDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
    		 OPDDBConnections = DriverManager.getConnection(OPDDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());

    		 
    		 ResultSet gettingRecipitentLastTableValus = OPDCustomeDataBase.getTableVaues(OPDDBConnections,OPDDBStatement,dbStatement,selectTableStatement,databaseName, databaseName.concat("_calllog"),"endview","Nurse","*");
    	  
    	     while(gettingRecipitentLastTableValus.next())
  			{
  				recipientcallLogNO.add(gettingRecipitentLastTableValus.getString("callno"));
  				recipientDoctorName.add(gettingRecipitentLastTableValus.getString("doctor_name"));
  				recipientcallLogContent.add(gettingRecipitentLastTableValus.getString("doctordata"));
  				recipientcallLogStartTime.add(gettingRecipitentLastTableValus.getString("starttime"));
  				recipientcallLogEndTime.add(gettingRecipitentLastTableValus.getString("endtime"));
  				recipientcallLogduration.add(gettingRecipitentLastTableValus.getString("duration"));
  				recipientEndViewcallLog.add(gettingRecipitentLastTableValus.getString("endview"));
  				doctorRoomNo.add(gettingRecipitentLastTableValus.getString("doctor_roomno"));
  				doctorDepartment.add(gettingRecipitentLastTableValus.getString("doctor_department"));
  			}
    	     
    	     
    	 }
    	 catch(SQLSyntaxErrorException syntaxException)
	  		{
	  			if(syntaxException.getMessage().contains("already exists"))
	  			{
	  				throw new LocalException("EXISTING_TABLE");
	  			}
	  			return new OPDCustomEntity(400,"non-success");
	  		}
	  		catch(CommunicationsException communicationException)
	  		{
	  			communicationException.printStackTrace();
	  			throw new LocalException("INVALID_URL");
	  		}
	  		catch(SQLException sqlException)
	  		{
	  			if(sqlException.getMessage().contains("Unknown database"))
	  			{
	  				throw new LocalException("FALSE_DATABASE");
	  			}
	  			else if(sqlException.getMessage().contains("Access denied"))
	  			{
	  				throw new LocalException("INVALID_CREDENTIALS");
	  			}
	  			sqlException.printStackTrace();
	  			
	  			return new OPDCustomEntity(400,"non-success");
	  		}
	    	 catch (Exception e) {
			
				System.out.println("---- Excel Dwn NurseActive Status Check Error ----");
				return new OPDCustomEntity(400,"non-success");
				
			}
    	 finally 
    		{
    		    if (OPDDBConnections != null) {
    		        try {
    		        	OPDDBConnections.close();
    		        } catch (SQLException e) { /* Ignored */}
    		    }
    		    
    		  if (dbStatement != null) {
  		        try {
  		        	dbStatement.close();
  		        } catch (SQLException e) { /* Ignored */}
  		    }
  		    if (selectTableStatement != null) {
  		        try {
  		        	selectTableStatement.close();
  		        } catch (SQLException e) { /* Ignored */}
  		    }
  		    if (OPDDBStatement != null) 
  		    {
  		        try 
  		        {
  		        	OPDDBStatement.close();
  		        } catch (SQLException e) { /* Ignored */}
  		    }
    		    
    		}
    	 
    	 try {
    	 
    		 List<ArrayList<Object>> nurseCallLog
             = new ArrayList<ArrayList<Object>>();
    		 
    		 
    		 nurseCallLog.add(new ArrayList<Object>());
          	 
    		 nurseCallLog.get(0).addAll(Arrays.asList("Sno","Doctor Name","Content","Department","Room No","Start Time","End Time","Duration","End View"));

    		 for(int i=0;i<recipientcallLogNO.size();i++)
    		 {
    			 for(int j=0;j<recipientDoctorName.size();j++)
    			 {
    				 if(i==j)
    				 {
    					 for(int k=0;k<recipientcallLogContent.size();k++)
    					 {
    						 if(i==k)
    						 {
    							 for(int p=0;p<doctorDepartment.size();p++)
    	    					 {
    								 if(i==p)
    								 {
    								for(int q=0;q<doctorRoomNo.size();q++)
    	    	    			    {
    								if(i==q)
       								 {
    							 for(int l=0;l<recipientcallLogStartTime.size();l++)
    							 {
    								 if(i==l)
    								 {
    									 for(int m=0;m<recipientcallLogEndTime.size();m++)
    									 {
    										 if(i==m)
    										 {
    											 for(int n=0;n<recipientcallLogduration.size();n++)
    											 {
    												 if(i==n)
    												 {
    													 for(int o=0;o<recipientEndViewcallLog.size();o++)
    													 {
    														 if(i==o)
    														 {
    														 nurseCallLog.add(new ArrayList<Object>());
    														 nurseCallLog.get(i+1).addAll(Arrays.asList(i+1,recipientDoctorName.get(j)
    					                    						 ,recipientcallLogContent.get(k),doctorDepartment.get(p),doctorRoomNo.get(q),recipientcallLogStartTime.get(l),recipientcallLogEndTime.get(m)
    					                    						 ,recipientcallLogduration.get(n),recipientEndViewcallLog.get(o)));
    														 }
    													 }
    												 }
    											 }
    										 }
    									 }
    								 }
    							 }
    	    	    			    }
    							  }
    	    					 }
    						   }
    						 }
    					 }
    				 }
    			 }
    		 }
             
             
    	
     	 return new OPDCustomEntity(200,nurseCallLog,"success");
    	 
    	 }catch (Exception e) {
    		 System.out.println("---- Recipient Excel Dwn Call Log Error ----");
    		 return new OPDCustomEntity(400,"non-success");
     		
 		}
    	 
     }
     
     public OPDCustomEntity FAQQueries(HttpSession session)
     {
    	 try
    	 {
    		 Map<String,Object> faq = new LinkedHashMap<String, Object>();
    		 
    		 List<String> questions = new ArrayList<String>();
    		 questions.add(faqlassPathPropertiesFiles.getQuestion1());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion2());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion3());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion4());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion5());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion6());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion7());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion8());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion9());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion10());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion11());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion12());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion13());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion14());
    		 questions.add(faqlassPathPropertiesFiles.getQuestion15());
    		
    		 
    		 
    		 List<String> answer = new ArrayList<String>();
    		 answer.add(faqlassPathPropertiesFiles.getAnswer1());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer2());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer3());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer4());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer5());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer6());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer7());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer8());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer9());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer10());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer11());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer12());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer13());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer14());
    		 answer.add(faqlassPathPropertiesFiles.getAnswer15());
    		 
    		 
    		 faq.put("faqanswer", answer);
    		 faq.put("faqquestions", questions);
    		 
    		session.setAttribute("faqquery", faq);
    	
    		return new OPDCustomEntity(200,session.getAttribute("faqquery"),"success");
    		 
     }catch (Exception e) {
		 System.out.println("---- FAQ Error ----");
		 return new OPDCustomEntity(400,"non-success");
 		}
     }
     
}
