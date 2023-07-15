package com.opdetiicos.opdcontroller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.opdetiicos.customDTO.ForgotPasswordDataObject;
import com.opdetiicos.customDTO.NurseRequestValues;
import com.opdetiicos.customDTO.PaymentVerifyMail;
import com.opdetiicos.customDTO.RegisterDataObject;
import com.opdetiicos.customDTO.TransationRazorpayDetails;
import com.opdetiicos.customDTO.UpdateCallRequestDataObject;
import com.opdetiicos.customDTO.UpdateWard;
import com.opdetiicos.customDTO.UserDetailsDataObject;
import com.opdetiicos.customEntity.OPDCustomEntity;
import com.opdetiicos.services.OPDEtiicosServices;

@RestController
public class OPDRestController {

	@Autowired
	private OPDEtiicosServices OPDEtiicosServices;
	
	@GetMapping("/innerprocess/etiicos/faq/")
	private OPDCustomEntity FAQ(HttpSession session)
	{
		return OPDEtiicosServices.FAQQueries(session);
	}
	
	@GetMapping("/innerprocess/etiicos/perdoctoramount/")
	private OPDCustomEntity perDoctorAmount(HttpSession session)
	{
		return OPDEtiicosServices.perDoctorAmount(session);
	}

	@PostMapping("/innerprocess/etiicos/paymentgmailverify/")
	private OPDCustomEntity paymentGmailVerification(String gmail,HttpSession session)
	{
		session.setAttribute("payregistergmail", gmail);
    	String registerGmailPayiedAttribute = (String) session.getAttribute("payregistergmail");
		
		return OPDEtiicosServices.gmailPaymentVerify(registerGmailPayiedAttribute);
	}
	
	@PostMapping("/innerprocess/etiicos/paymentgOTPverify/")
	private OPDCustomEntity paymentOTPConformations(PaymentVerifyMail paymentVerifymail,HttpSession session)
	{
		session.setAttribute("paymentmailotp", paymentVerifymail);
		PaymentVerifyMail paymentGmailAttribute = (PaymentVerifyMail) session.getAttribute("paymentmailotp");
		return OPDEtiicosServices.paymentVerifyMailID(paymentGmailAttribute);
	}
	
	@PostMapping("/innerprocess/etiicos/pay/")
	private OPDCustomEntity razerPayTranstation(String amount,HttpSession session)
	{
		session.setAttribute("amount", amount);
		String paymentAmount = (String) session.getAttribute("amount");
		
		System.out.println(paymentAmount);
		
		return OPDEtiicosServices.createTransation(paymentAmount);
	}
	
	@PostMapping("/innerprocess/etiicos/paymentsuccess/")
	private OPDCustomEntity paymentSuccessDetails(TransationRazorpayDetails transationDetails,HttpSession session)
	{
		
		session.setAttribute("transationdetails", transationDetails);
		TransationRazorpayDetails transationSuccess = (TransationRazorpayDetails) session.getAttribute("transationdetails");
		return OPDEtiicosServices.successFullTransation(transationSuccess);
	}
	
	
	@PostMapping("/innerprocess/etiicos/gmailverify/")
	private OPDCustomEntity singupAuthandication(String gmail,HttpSession session)
	{
		session.setAttribute("registergmail", gmail);
    	String registerGmailAttribute = (String) session.getAttribute("registergmail");
		return OPDEtiicosServices.registerdGmailOTP(registerGmailAttribute);
	}
	
	
	@PostMapping("/innerprocess/etiicos/OTPverify/")
	private OPDCustomEntity signUpOTPConformations(RegisterDataObject registerDetails,HttpSession session)
	{
		session.setAttribute("registerotp", registerDetails);
		RegisterDataObject registerGmailAttribute = (RegisterDataObject) session.getAttribute("registerotp");
		return OPDEtiicosServices.signUpAuthentationVerify(registerGmailAttribute);
	}
	
	@PostMapping("/signup")
	private OPDCustomEntity registered(UserDetailsDataObject registered,HttpServletResponse res,HttpSession session,Model model)
	{
		 session.setAttribute("registeredList",registered);
		 UserDetailsDataObject HospitalsOfListCity = (UserDetailsDataObject) session.getAttribute("registeredList");
		 	
		 return OPDEtiicosServices.registrationData(HospitalsOfListCity,res,model);
	
	}
	
    @PostMapping("/innerprocess/authorizedOTP/")
    private OPDCustomEntity forgotPassword(String gmail,HttpSession session)
	{
    	session.setAttribute("forgotpasswordMail", gmail);
    	String gmailAttribute = (String) session.getAttribute("forgotpasswordMail");
		return OPDEtiicosServices.forgotPasswordOTP(gmailAttribute);
	}
	
	
	
	@PostMapping("/innerprocess/forgot/")
	private OPDCustomEntity updateForgotPassword(ForgotPasswordDataObject forgotpasswordData,HttpServletResponse response,HttpSession session) throws IOException
    {
		 session.setAttribute("forgotPassword",forgotpasswordData);
		 ForgotPasswordDataObject forgotPasswordDetails = (ForgotPasswordDataObject) session.getAttribute("forgotPassword");
		if(OPDEtiicosServices.updatingForgotPassword(forgotPasswordDetails))
		{
			return new OPDCustomEntity(200,"/opdetiicos/login","success");
		}
		else
		{
			return new OPDCustomEntity(400,"non-success");
		}	
    }

	    @PostMapping("/innerprocess/doctor/nursecall/")
	    private OPDCustomEntity nurseCall(UpdateCallRequestDataObject requestNurseCall,HttpSession session)
	    {
            session.setAttribute("nursecall", requestNurseCall);
	    	
            UpdateCallRequestDataObject nurseCallRequest = (UpdateCallRequestDataObject) session.getAttribute("nursecall");
	    	     
           if(OPDEtiicosServices.UpdateDoctorStauts(nurseCallRequest))
           {
            	return new OPDCustomEntity(200,"success");
            }
            else
            {
           	return new OPDCustomEntity(400,"non-success");
           }
	    	
	    }
	     
	     @PostMapping("/innerprocess/recipientdatas/")
	     private OPDCustomEntity recipientDatas(String databaseName,HttpSession session)
	     {
	        session.setAttribute("recipientidvalues", databaseName);
	    	
	        String recipientID = (String) session.getAttribute("recipientidvalues");
	        
	    	return OPDEtiicosServices.recipientFullDatas(recipientID);
	     }
	     
	     @PostMapping("/innerprocess/recipientlog/")
	     private OPDCustomEntity recpientLogsDatas(String databaseName,HttpSession session)
	     {
	        session.setAttribute("logdatabasenamerecipient", databaseName);
	    	
	        String recipentLogDataBaseURL = (String) session.getAttribute("logdatabasenamerecipient");
	    
	        return OPDEtiicosServices.recipitentCallLog(recipentLogDataBaseURL);
	     }
	     
	     @PostMapping("/innerprocess/recipientaudiopause/")
	     private void audiopause(NurseRequestValues pauseAudioStatus,HttpSession session)
	     {
	        session.setAttribute("pauseAudio",pauseAudioStatus);
	    	
	        NurseRequestValues audioPause = (NurseRequestValues) session.getAttribute("pauseAudio");
	        
	        OPDEtiicosServices.pauseAudioStatus(audioPause);
	      
	     }
	     
	     @PostMapping("/innerprocess/exceldwnrecipientlog/")
	     private OPDCustomEntity recpientExcelDwnLogsDatas(String hospitalName,HttpSession session)
	     {
	        session.setAttribute("dwnexcellogdatabasenamerecipient", hospitalName);
	    	
	        String recipentDwnLogDataBaseURL = (String) session.getAttribute("dwnexcellogdatabasenamerecipient");
	        
	    	return OPDEtiicosServices.recipitentDwnCallLog(recipentDwnLogDataBaseURL);
	     }
	     
}
