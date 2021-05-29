package com.lgdisplay.bigdata.api.glue.scheduler.repository;

import com.lgdisplay.bigdata.api.glue.scheduler.model.ServerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerHealthRepository extends JpaRepository<ServerStatus, String> {

}
