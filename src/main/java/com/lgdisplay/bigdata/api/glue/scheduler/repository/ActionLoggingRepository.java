package com.lgdisplay.bigdata.api.glue.scheduler.repository;

import com.lgdisplay.bigdata.api.glue.scheduler.model.ActionLogging;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLoggingRepository extends CrudRepository<ActionLogging, Long> {
}
