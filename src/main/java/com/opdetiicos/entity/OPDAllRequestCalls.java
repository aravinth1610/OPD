package com.opdetiicos.entity;

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
@Table(name="OPD_all_call_request")
public class OPDAllRequestCalls {

	@Id
	private String user_id;
	private String gmail;
	private String hospital;
	private String hospital_id;
	private String password;
	private String request_time;
	private Boolean request;
	private String state;
	private String city;
	

}
