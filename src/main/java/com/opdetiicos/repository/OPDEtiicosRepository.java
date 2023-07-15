package com.opdetiicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.opdetiicos.entity.OPDRegisteredEntity;

import java.lang.String;
import java.util.List;



@Repository
public interface OPDEtiicosRepository extends JpaRepository<OPDRegisteredEntity, String> {

	
	
	OPDRegisteredEntity findByGmail(String gmail);	
	
	@Query(value = "select hospital from opd_etiicos_registered where gmail=?1",nativeQuery = true)
	String findByHospitalname(String gmail);
	
    Boolean existsByGmail(String gmail);
    
	@Query(value = "select hospital_id from opd_etiicos_registered where gmail=?1",nativeQuery = true)
    String findByHospitalID(String gmail);
    
    @Modifying
    @Transactional
    @Query(value = "update opd_etiicos_registered set password=?1 where gmail=?2",nativeQuery = true)
    Integer updatePassword(String password,String gmail);
    
    @Query(value = "select hospital_id from opd_etiicos_registered",nativeQuery = true)
    List<String> existsHospitalID();

     Boolean existsByHospitalId(String hospitalid);
     
     @Query(value = "select next_payment_date,payment_status from opd_etiicos_registered where gmail=?1",nativeQuery = true)
     String findPaymentDetails(String gmail);
     
     @Query(value = "select payment_status from opd_etiicos_registered where gmail=?1",nativeQuery = true)
     String findPaymentStatus(String gmail);
  
     @Modifying
     @Transactional
     @Query(value = "update opd_etiicos_registered set next_payment_date=?1,payment_status=?2 where gmail=?3",nativeQuery = true)
     Integer updatePaymentDetails(String next_payment_date,String payment_status,String gmail);
     
     @Modifying
     @Transactional
     @Query(value = "update opd_etiicos_registered set payment_status=?1 where gmail=?2",nativeQuery = true)
     Integer isNotUpdatePaymentDetails(String payment_status,String gmail);
    
}
