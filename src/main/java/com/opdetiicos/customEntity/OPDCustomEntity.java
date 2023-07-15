package com.opdetiicos.customEntity;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OPDCustomEntity {
	
	public OPDCustomEntity(Integer status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	private Integer status;
	private Object content;
	private String message;
	
}
