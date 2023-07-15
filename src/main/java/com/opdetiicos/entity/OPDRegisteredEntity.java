package com.opdetiicos.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="opd_etiicos_registered")
public class OPDRegisteredEntity {

	@Id
	private String user_id;
	private String gmail;
	private String hospital;
	@Column(name="hospital_id")
	private String hospitalId;
	private String password;
	private String registered_time;
	private String state;
	private String city;
	@Column(name="payment_status")
	private String paymentStatus;
	@Column(name="next_payment_date")
	private String nextPaymentDate;
	
	
}
