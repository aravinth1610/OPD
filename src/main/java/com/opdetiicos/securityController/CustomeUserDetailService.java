package com.opdetiicos.securityController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.opdetiicos.OPDCustomeDB.DBOperator;
import com.opdetiicos.entity.OPDLoginEntity;
import com.opdetiicos.entity.OPDRegisteredEntity;
import com.opdetiicos.localException.LocalException;
import com.opdetiicos.properties.PropertiesClassPath;
import com.opdetiicos.repository.OPDEtiicosCallRequest;
import com.opdetiicos.repository.OPDEtiicosLoginRepository;
import com.opdetiicos.repository.OPDEtiicosRepository;

@Component
public class CustomeUserDetailService implements UserDetailsService {

	@Autowired
	private OPDEtiicosRepository opdRegistered;
	
	@Autowired
	private OPDEtiicosCallRequest opdEtiicosCallRequestRepo;
	
	@Autowired
	private PropertiesClassPath propertiesFiles;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		OPDRegisteredEntity register =opdRegistered.findByGmail(username);
		
	   String hospitalID = opdRegistered.findByHospitalID(username);
		 
		 if(hospitalID == null)
		 {
			 hospitalID=" ";
		 }
		 

		 if((!hospitalID.equals(isHospitalBlocked(username))) && isHospitalBlocked(username).isEmpty())
		    {
		   	 System.out.println(opdRegistered.existsByGmail(username)+" "+opdEtiicosCallRequestRepo.existsByGmail(username));
		  
		    if(opdRegistered.existsByGmail(username) && opdEtiicosCallRequestRepo.existsByGmail(username))
			{
		    	
				 String isPaidStatus = ispaid(register.getNextPaymentDate(),register.getPaymentStatus(),register.getGmail());
				 
				 String[] isPaidStatusSplit = isPaidStatus.split(",");
				 
			     String userName = isPaidStatusSplit[0];
			     
			     String paymentStatus = opdRegistered.findPaymentStatus(register.getGmail());
			     
			     if(paymentStatus.equals("free"))
			     {
			    	 return new CustomeUserDetails(register); 
			     }
			     else if(paymentStatus.equals("paid"))
			 	 {
		 		if(register != null)
				{
					
					return new CustomeUserDetails(register);
					}
				else
				{
				
					throw new UsernameNotFoundException("User not present");
				}
				 }
				 else
				 {
					 throw new UsernameNotFoundException("payment,"+userName);
				 }
			}
			else if((!opdRegistered.existsByGmail(username)) && (!opdEtiicosCallRequestRepo.existsByGmail(username)))
			{
					throw new UsernameNotFoundException("User not present");
			}
			else
			{
				
				throw new UsernameNotFoundException("Wating for Approval");
			}
		    }
		    else
		    {
		    	throw new UsernameNotFoundException("User is Blocked");
		    }

	}
	
	public final String isHospitalBlocked(String gmail)
	{
		PreparedStatement dbStatement = null;
 		PreparedStatement selectTableStatement = null;
 		Connection tokenDBConnections = null;
 		Statement tokenDBStatement = null;
    	 
    	 try {
    	     DBOperator tokenCustomeDataBaseResult = new DBOperator();
    		 String tokenDBURL = "jdbc:mysql://".concat(propertiesFiles.getHost()).concat(":").concat(propertiesFiles.getPort()).concat("/?enabledTLSProtocols=TLSv1.2");
    		 tokenDBConnections = DriverManager.getConnection(tokenDBURL, propertiesFiles.getUsername(), propertiesFiles.getPassword());
    	
    	     ResultSet tableResultValue = tokenCustomeDataBaseResult.getTableVaues(tokenDBConnections,tokenDBStatement,dbStatement,selectTableStatement,"etiicosaggregatedspace","opd_reject_request","gmail",gmail,"hospital_id");

    	     tableResultValue.next();
    	      
    	     String hospitalID = tableResultValue.getString("hospital_id");
    	     
    	     return hospitalID;

        	} 
    	 
    	catch(SQLSyntaxErrorException syntaxException)
 		{
 			if(syntaxException.getMessage().contains("already exists"))
 			{
 				throw new LocalException("EXISTING_TABLE");
 			}
 			return "";
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
 			return "";
 		}
 		 catch (Exception e) {
 				System.out.println("---- Hospital ID Blocked Error ----");
        		return "";
		}
    	finally 
  		{
  		    if (tokenDBConnections != null) {
  		        try {
  		        	tokenDBConnections.close();
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
		    if (tokenDBStatement != null) 
		    {
		        try 
		        {
		        	tokenDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
  		    
  		}
	}

	public String ispaid(String paidDate,String paidStatus,String registerGmail)
	{
		
		SimpleDateFormat dateFormater=new SimpleDateFormat("dd/MM/yyy");
		String date = dateFormater.format(new Date());
		try {
			
			if(paidStatus.equals("free"))
			{
				return registerGmail+","+paidStatus;
			}
			else if(!paidDate.isEmpty() && !paidStatus.equals("unpaid"))
			{
				Date currentDate = dateFormater.parse(date);
				
				Date expiredDate = dateFormater.parse(paidDate);
				
			if(currentDate.before(expiredDate))
			{
				return registerGmail+","+paidStatus;
			}
			else
			{
				opdRegistered.isNotUpdatePaymentDetails("unpaid", registerGmail);
				return registerGmail+","+paidStatus;
			}
			}else
			{
				return registerGmail+","+paidStatus;
			}
			
		}catch (Exception e) {
			return registerGmail+","+paidStatus;
		}
	}
	
}
