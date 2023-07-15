package com.opdetiicos.customDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDataObject {

	private String hospital;
	private String gmail;
	private String password;
	private String state;
	private String city;
}
