package com.lgdisplay.bigdata.api.glue.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity(name = "api_glue_server_status")
public class ServerStatus {

    @Id
    @Column(name = "server_name", columnDefinition = "VARCHAR(255)")
    String serverName;

    @Column(name = "ip_address", columnDefinition = "VARCHAR(255)")
    String ipAddress;

    @Column(name = "logical_processor_count")
    Integer logicalProcessorCount;

    @Column(name = "physical_processor_count")
    Integer physicalProcessorCount;

    @Column(name = "total_memory")
    Long totalMemory;

    @Column(name = "available_memory")
    Long availableMemory;

    @Column(name = "cpuLoad_ratio")
    Integer cpuLoadRatio;

    @Column(name = "memory_ratio")
    Integer memoryRatio;

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
