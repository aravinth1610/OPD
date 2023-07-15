package com.opdetiicos.customDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDataObject {

	private Integer otp;
	private String registerGmail;
	
}
