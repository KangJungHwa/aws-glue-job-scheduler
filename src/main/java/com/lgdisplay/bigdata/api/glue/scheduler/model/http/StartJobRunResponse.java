package com.lgdisplay.bigdata.api.glue.scheduler.model.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartJobRunResponse {

    @JsonProperty("JobRunId")
    private String jobRunId;
}
