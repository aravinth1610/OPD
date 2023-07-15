package com.opdetiicos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.opdetiicos.entity.OPDAllRequestCalls;

@Repository
public interface OPDEtiicosCallRequest extends JpaRepository<OPDAllRequestCalls,String> {

	@Modifying
    @Transactional
    @Query(value = "update OPD_all_call_request set request_time=?1 where gmail=?2",nativeQuery = true)
	Integer updaterequestTime(String requestTime,String gmail);
	
    Boolean existsByGmail(String gmail);
    
    @Query(value = "select count(gmail) from OPD_all_call_request order by request_time ASC",nativeQuery = true)
    Integer registerFirstUsers();
    
}
