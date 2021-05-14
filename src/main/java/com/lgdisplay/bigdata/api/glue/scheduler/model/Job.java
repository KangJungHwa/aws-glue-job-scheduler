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
@Entity(name = "api_glue_job")
public class Job {

    @Id
    @Column(name = "job_id")
    Long jobId;

    @Column(name = "username", columnDefinition = "VARCHAR(100)", nullable = true)
    String username;

    @Column(name = "job_name", columnDefinition = "VARCHAR(255)", nullable = true)
    String jobName;

    @Column(name = "script_name", columnDefinition = "VARCHAR(255)", nullable = true)
    String scriptName;

    @Column(name = "script_location", columnDefinition = "VARCHAR(255)", nullable = true)
    String scriptLocation;

    /**
     * 생성일 (이 필드에는 값을 입력하지 않아도 Hibernate가 INSERT시 자동으로 기록)
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

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "body", nullable = true)
    String body;

}
