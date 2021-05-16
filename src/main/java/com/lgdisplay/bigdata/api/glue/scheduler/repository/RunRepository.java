package com.lgdisplay.bigdata.api.glue.scheduler.repository;

import com.lgdisplay.bigdata.api.glue.scheduler.model.Run;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RunRepository extends CrudRepository<Run, String> {

    @Query("FROM com.lgdisplay.bigdata.api.glue.scheduler.model.Run j WHERE j.jobName = :jobName")
    List<Run> findAllLimitN(@Param("jobName") String jobName, Pageable pageable);

    Optional<Run> findByJobNameAndJobRunId(String jobName, String runId);

    Optional<List<Run>> findByJobName(String jobName);

    Optional<Run> findByTriggerId(String triggerId);

}
