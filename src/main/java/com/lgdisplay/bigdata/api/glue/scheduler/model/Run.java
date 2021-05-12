package com.lgdisplay.bigdata.api.glue.scheduler.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

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

    @Column(name = "arguments", columnDefinition = "VARCHAR(2000)", nullable = true)
    String arguments;

    @Column(name = "job_run_state", columnDefinition = "VARCHAR(2000)", nullable = true)
    String jobRunState;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "body", nullable = true)
    String body;

}
