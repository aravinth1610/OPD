package com.opdetiicos.opdcontroller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.opdetiicos.customDTO.UserDetailsDataObject;
import com.opdetiicos.repository.OPDEtiicosRepository;
import com.opdetiicos.securityController.CustomeUserDetails;
import com.opdetiicos.services.OPDEtiicosServices;


@Controller
public class ViewController {

	@Autowired
	private OPDEtiicosRepository OPDEtiicosRepo;
	
		
	@RequestMapping("/faq")
	public String recipetentFAQ()
	{
		return "faq";
	}
	
	@RequestMapping("/opdtutorial")
	public String recipetentOPDDemoVideo()
	{
		return "OPDdemoVideo";
	}
	
	@RequestMapping("/tokentutorial")
	public String recipetentTokenDemoVideo()
	{
		return "tokendemoVideo";
	}
	
	@RequestMapping("/whatapptutorial")
	public String recipetentWhatappDemoVideo()
	{
		return "whatappdemoVideo";
	}
	
    @RequestMapping("/home")
	public String recipetentHome()
	{
		return "recipetentHome";
	}
	
	@RequestMapping("/log")
	public String recipetentLog()
	{
	return "recipetentLog";
	}
	
	@RequestMapping("/payment")
	public String paymentInfo()
	{
	return "payment";
	}
	
	@RequestMapping("/login")
	public String recipetentLogin(HttpServletRequest request,HttpSession session,HttpServletResponse response)
	{
		
		
		   session.setAttribute("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION",response));
		    
			    
			Authentication authentication =	SecurityContextHolder.getContext().getAuthentication();
				
			if(authentication == null || authentication instanceof AnonymousAuthenticationToken)
			{
						return "recipetentLogin";
			}
			else
				{
					 return "redirect:/home";
				}
	}

	@RequestMapping("/forgotPassword")
	public String recipetentForgotPassword()
	{
     Authentication authentication =	SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken)
		{
	    return "recipetentforgot";
		}
		else{
		      return "redirect:/home";
			}
	}
	
	@RequestMapping("/register")
	public String register(Model model)
	{
		Authentication authentication =	SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken)
		{
		model.addAttribute("user", new UserDetailsDataObject());
	    return "recipetentSignup";
		}
		else{
		      return "redirect:/home";
			}
	}
	 
	private String getErrorMessage(HttpServletRequest request, String key,HttpServletResponse response) {
	      Exception exception = (Exception) request.getSession().getAttribute(key); 
	      String error = ""; 
	
	     try
	     {
	    	 String loginMessage = exception.getMessage();
	    	 
	    	 
	     if(loginMessage.contains("Wating for Approval"))
	     {
	    	 error = "Waiting for Approval"; 
	     }
	     else if(loginMessage.contains("User is Blocked"))
	     {
	    	 error = "User is Blocked"; 
	     }
	     else if(loginMessage.contains("payment"))
	     {
	    	 String[] paymentDetails = loginMessage.split(",");
	    	 
	    	 String UserName = paymentDetails[1];
	    	 
	    	SimpleDateFormat dateFormater=new SimpleDateFormat("dd/MM/yyy");
	 		String date = dateFormater.format(new Date());
	 		try {
	 		
	 			String payemntDB = OPDEtiicosRepo.findPaymentDetails(UserName);
	 			
	 			String[] payemntDBSplit = payemntDB.split(",");
	 			
	 			String paidDate = payemntDBSplit[0];
	 			String paidStatus = payemntDBSplit[1];
	 		 
	    	 
				if(paidStatus.equals("free"))
				{
					error = "";
				}
				else if(!paidDate.isEmpty() && !paidStatus.equals("unpaid"))
				{
	    		    Date currentDate = dateFormater.parse(date);
					Date expiredDate = dateFormater.parse(paidDate); 
			
				if(currentDate.before(expiredDate))
				{
					error = "";
				}
				else
				{
					response.sendRedirect("/opdetiicos/payment");
				}
				}else
				{
					response.sendRedirect("/opdetiicos/payment");
				}
	    	}catch (Exception e) {
				response.sendRedirect("/opdetiicos/payment");
			}
	     }
	  	 else if (exception instanceof BadCredentialsException) { 
	        error = "Invalid username and password!"; 
	      } else { 
	         error = "Invalid username and password!"; 
	      } 
	      return error;
	      
	     }catch (Exception e) {
	    	 error = "Invalid username and password!"; 
	    	 return error;
		}
	      
	   }
	
}
