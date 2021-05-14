package com.lgdisplay.bigdata.api.glue.scheduler.repository;

import com.lgdisplay.bigdata.api.glue.scheduler.model.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends CrudRepository<Job, Long> {

    Optional<Job> findByUsernameAndJobName(String username, String jobName);

    @Query("FROM com.lgdisplay.bigdata.api.glue.scheduler.model.Job j WHERE j.username = :username  ORDER BY j.createDate DESC")
    List<Job> findAllLimitN(@Param("username") String username, Pageable pageable);

    Optional<List<Job>> findJobsByUsername(String username);

    List<Job> findByJobNameIn(List<String> jobname);
}
