package com.lgdisplay.bigdata.api.glue.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity(name = "api_glue_scheduler_logging")
public class ActionLogging {

    @Id
    @Column(name = "request_id")
    Long requestId;

    @Column(name = "username", columnDefinition = "VARCHAR(100)", nullable = true)
    String username;

    /**
     * 호출한 클라이언트의 IP 주소
     */
    @Column(name = "ip_address", columnDefinition = "VARCHAR(100)", nullable = true)
    String ipAddress;

    /**
     * 서비스 유형
     */
    @Column(name = "service_type", columnDefinition = "VARCHAR(50)", nullable = true)
    String serviceType;

    /**
     * AWS SDK API의 Action
     */
    @Column(name = "action_name", columnDefinition = "VARCHAR(100)", nullable = true)
    String actionName;

    /**
     * 리소스명 (예; S3의 경우 Bucket Name)
     */
    @Column(name = "resource_name", columnDefinition = "VARCHAR(255)", nullable = true)
    String resourceName;

    /**
     * 성공 및 실패
     */
    @Column(name = "status", columnDefinition = "VARCHAR(10)", nullable = true)
    @Enumerated(EnumType.STRING)
    ActionLoggingStatusTypeEnum status;

    /**
     * HTTP Status Code
     */
    @Column(name = "http_status")
    Integer httpStatus;

    /**
     * 생성일 (이 필드에는 값을 입력하지 않아도 Hibernate가 INSERT시 자동으로 기록)
     */
    @CreationTimestamp
    @Column(name = "start_ts", insertable = true, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp startDate;

    /**
     * 종료일 (이 필드에는 값을 입력하지 않아도 Hibernate가 UPDATE시 자동으로 기록)
     */
    @UpdateTimestamp
    @Column(name = "end_ts", insertable = false, updatable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp endDate;

    @Column(name = "elapsed_time_millis", nullable = true)
    Long elapsedTime;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "message", nullable = true)
    String message;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "layer_response_time", nullable = true)
    String layerResponseTime;
}
