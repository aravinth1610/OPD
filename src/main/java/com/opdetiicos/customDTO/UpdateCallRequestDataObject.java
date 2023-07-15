package com.opdetiicos.customDTO;

import lombok.Data;

@Data
public class UpdateCallRequestDataObject {

	private String dataBaseName;
	private Boolean status;
	private String dataTyped;
	private String callno;
	private String startTime;
	private String endTime;
	private String endView;
	private String doctorName;
	
	
}
