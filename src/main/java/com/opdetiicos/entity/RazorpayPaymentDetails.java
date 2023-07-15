package com.opdetiicos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="razorpay_payment_details")
public class RazorpayPaymentDetails {

	@Id
	@Column(name = "gmail_id",length = 500)
	private String gmailID;
	@Column(name = "payment_id",length = 500)
	private String paymentID;
	@Column(name = "order_id",length = 500)
	private String orderID;
	@Column(name = "signature",length = 500)
	private String signature;
	private String count;
	
}
