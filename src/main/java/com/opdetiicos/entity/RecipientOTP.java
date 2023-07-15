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
@AllArgsConstructor
@NoArgsConstructor
@Table(name="recipient_otp")
public class RecipientOTP {

	@Id
	@Column(name="recipient_user")
	private String recipientUser;
	@Column(name="otp_updateduser")
	private Integer OTPUpdatedUser;
}
