package com.opdetiicos.customDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWard {

	private String doctorName;
	private String ward_no;
	private String recipientID;
	
}
