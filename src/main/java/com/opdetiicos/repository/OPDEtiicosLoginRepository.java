package com.opdetiicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opdetiicos.entity.OPDLoginEntity;

@Repository
public interface OPDEtiicosLoginRepository extends JpaRepository<OPDLoginEntity,String> {

}
