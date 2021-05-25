package com.lgdisplay.bigdata.api.glue.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity(name = "api_glue_job_run")
public class Run {

    @Id
    @Column(name = "job_run_id", columnDefinition = "VARCHAR(255)")
    String jobRunId;

    @Column(name = "job_name", columnDefinition = "VARCHAR(255)")
    String jobName;

    @Column(name = "job_run_state", columnDefinition = "VARCHAR(2000)", nullable = true)
    String jobRunState;

    @Column(name = "user_name", columnDefinition = "VARCHAR(255)", nullable = true)
    String userName;

    @Column(name = "trigger_name", columnDefinition = "VARCHAR(255)")
    String triggerName;

    @Column(name = "trigger_id", columnDefinition = "VARCHAR(255)")
    String triggerId;

    @Column(name = "pid")
    Long pid;

    /**
     * 생성시간 (이 필드에는 값을 입력하지 않아도 Hibernate가 INSERT시 자동으로 기록)
     */
    @CreationTimestamp
    @Column(name = "create_TS", insertable = true, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createDate;

    /**
     * (이 필드에는 값을 입력하지 않아도 Hibernate가 UPDATE시 자동으로 기록)
     */
    @UpdateTimestamp
    @Column(name = "update_ts", insertable = false, updatable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateDate;

}
