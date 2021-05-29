package com.lgdisplay.bigdata.api.glue.scheduler.monitoring;

import com.lgdisplay.bigdata.api.glue.scheduler.model.ServerStatus;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.ServerHealthRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.service.ResourceService;
import com.lgdisplay.bigdata.api.glue.scheduler.model.ServerHealth;
import com.lgdisplay.bigdata.api.glue.scheduler.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * Glue API Server의 리소스를 주기적으로 체크하는 Scheduled Task.
 */
@Slf4j
@Component
public class ServerHealthCheckTask {

    @Autowired
    ServerHealthRepository serverHealthRepository;
    //ResourceService resourceService;

    long[] prevTicks = new long[CentralProcessor.TickType.values().length];

    @Scheduled(fixedRate = 50000)
    public void execute() {
        String hostname=null;
        InetAddress ip=null;
        try {
             ip = InetAddress.getLocalHost();
             hostname=ip.getHostName();
        }catch(UnknownHostException e){
            log.error(String.valueOf(e));
        }

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();

        int logicalProcessorCount = cpu.getLogicalProcessorCount();
        int physicalProcessorCount = cpu.getPhysicalProcessorCount();

        GlobalMemory memory = hal.getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();

        int cpuLoadRatio = (int) (cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100);
        int memoryRatio = (int) (((double) availableMemory / (double) totalMemory) * 100);

        ServerStatus serverStatus = ServerStatus.builder()
                .serverName(hostname)
                .ipAddress(ip.getHostAddress())
                .physicalProcessorCount(physicalProcessorCount)
                .logicalProcessorCount(logicalProcessorCount)
                .totalMemory(totalMemory)
                .availableMemory(availableMemory)
                .cpuLoadRatio(cpuLoadRatio)
                .memoryRatio(memoryRatio)
                .build();
        serverHealthRepository.save(serverStatus);

        this.prevTicks = cpu.getSystemCpuLoadTicks();
    }

}