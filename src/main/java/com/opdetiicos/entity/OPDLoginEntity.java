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
@Table(name="opd_etiicos_login")
public class OPDLoginEntity {

	@Id
	private String registeredTime;
	private String gmail;
	private String hospital;
	private String hospital_id;
}
