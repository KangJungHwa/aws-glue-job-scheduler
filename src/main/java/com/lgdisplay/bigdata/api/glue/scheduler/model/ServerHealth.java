package com.lgdisplay.bigdata.api.glue.scheduler.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ServerHealth implements Serializable {

    int logicalProcessorCount;
    int physicalProcessorCount;

    long totalMemory;
    long availableMemory;

    int cpuLoadRatio;
    int memoryRatio;

    @Override
    public String toString() {
        return String.format("Physical Processor : %s, Logical Processors : %s, Total Memory : %s, Available Memory : %s, CPU Load : %s, Memory : %s", physicalProcessorCount, logicalProcessorCount, totalMemory, availableMemory, cpuLoadRatio, memoryRatio);
    }
}
