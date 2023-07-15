package com.opdetiicos.payment;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.opdetiicos.customEntity.OPDCustomEntity;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;


@Service
public class PaymentServices {

	
	private final static String keyID="rzp_live_ICTI7uJUjEBJnx";
	private final static  String keySecret="Nth2Rn2OROuT5piQrzSZtvA4";
	private final static String currency ="INR";
	
	
	public OPDCustomEntity createTransation(String userAmount)
	{
		//amount
		//currency
		//key
		//secret key 
	//	BigInteger amountValue = new BigInteger(userAmount);
		
		Double amountValue = Double.valueOf(userAmount);
		
		try {
		
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("amount", (amountValue *100));  // it consider as a (cent) value so we are multiple by 100.In Indian rupee cent consider as pisia
			jsonObject.put("currency", currency);
			jsonObject.put("payment_capture", true);
			
		RazorpayClient razerpayClient = new RazorpayClient(keyID, keySecret);
		
	    Order orderPay = razerpayClient.orders.create(jsonObject);
	    
	    String orderID = orderPay.get("id");
		String currency = orderPay.get("currency");
		Integer amount = orderPay.get("amount");
	    
		Map<String,Object> paymentDetails = new LinkedHashMap<String, Object>();
		
		paymentDetails.put("orderid", orderID);
		paymentDetails.put("currency", currency);
		paymentDetails.put("amount", amount);
		paymentDetails.put("keyid", keyID);
		
		return new OPDCustomEntity(200,paymentDetails,"success");
	    
		}catch (Exception e) {
			return new OPDCustomEntity(400,"non-success");
		}
	}
	
	
}
