package com.opdetiicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opdetiicos.entity.RazorpayPaymentDetails;

@Repository
public interface RazorpayPaymentDetailsRepository extends JpaRepository<RazorpayPaymentDetails,String> {

}
