package com.lgdisplay.bigdata.api.glue.scheduler.repository;

import com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TriggerRepository extends JpaRepository<Trigger, String> {
    Optional<Trigger> findByName(String name);

    Optional<Trigger> findByUserNameAndName(String username, String name);

    List<Trigger> findListByName(String name);

    @Query(value = "SELECT t.name FROM api_glue_trigger t WHERE t.name = :name", nativeQuery = true)
    List<String> findListTriggers(@Param("name") String name);

    @Query(value = "SELECT t.schedule FROM api_glue_trigger t WHERE t.name = :name", nativeQuery = true)
    Optional<String> findCronExpression(@Param("name") String name);

    @Query(value = "SELECT t.name FROM api_glue_trigger t WHERE t.name = :name", nativeQuery = true)
    List<String> findListTriggersLimitN(@Param("name") String name, Pageable pageable);

    @Query(value = "SELECT name FROM api_glue_trigger", nativeQuery = true)
    List<String> findListAllTriggers();

    @Query(value = "SELECT name FROM api_glue_trigger", nativeQuery = true)
    List<String> findListAllTriggersLimitN(Pageable pageable);

    @Query("FROM com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger t WHERE t.name = :name ")
    List<Trigger> findAllByNameLimitN(@Param("name") String name, Pageable pageable);

    @Query("FROM com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger t ")
    List<Trigger> findAllLimitN(Pageable pageable);

}
