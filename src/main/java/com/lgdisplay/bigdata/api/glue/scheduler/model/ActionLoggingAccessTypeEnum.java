package com.lgdisplay.bigdata.api.glue.scheduler.model;

import com.lgdisplay.bigdata.api.glue.scheduler.model.BaseEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionLoggingAccessTypeEnum implements BaseEnumCode<String> {

    START_JOB("START_JOB"),
    KILL_JOB("KILL_JOB");

    private final String value;

}
