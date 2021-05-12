package com.lgdisplay.bigdata.api.glue.scheduler.model.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartJobRunRequest {

    @JsonProperty("JobName")
    private String jobName;

    @JsonProperty("JobRunId")
    private String jobRunId;

    @JsonProperty("Arguments")
    private Map<String, String> arguments;

    @JsonProperty("AllocatedCapacity")
    private Integer allocatedCapacity;

    @JsonProperty("Timeout")
    private Integer timeout;

    @JsonProperty("MaxCapacity")
    private Double maxCapacity;

    @JsonProperty("SecurityConfiguration")
    private String securityConfiguration;

    @JsonProperty("NotificationProperty")
    private NotificationProperty notificationProperty;

    @JsonProperty("WorkerType")
    private String workerType;

    @JsonProperty("NumberOfWorkers")
    private Integer numberOfWorkers;
}
