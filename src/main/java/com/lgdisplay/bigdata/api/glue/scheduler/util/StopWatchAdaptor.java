package com.lgdisplay.bigdata.api.glue.scheduler.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopWatchAdaptor {

    StopWatch stopWatch;

    public StopWatchAdaptor(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    public void start(String taskName, String format, Object... args) throws IllegalStateException {
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        String message = String.format("'%s' 작업을 시작합니다. %s", taskName, String.format(format, args));
        log.info("{}", message);
        stopWatch.start(taskName);
    }

    public String getRequestId() {
        return stopWatch.getId();
    }

    public void start(String taskName) throws IllegalStateException {
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        String message = String.format("'%s' 작업을 시작합니다.", taskName);
        log.info("{}", message);
        stopWatch.start(taskName);
    }

    public String stopAndSummary() throws IllegalStateException {
        if (stopWatch.isRunning()) stopWatch.stop();
        return stopWatch.prettyPrint();
    }

    public void stop() throws IllegalStateException {
        stopWatch.stop();
    }

    public String prettyPrint() {
        return stopWatch.prettyPrint();
    }

    public boolean isRunning() {
        return stopWatch.isRunning();
    }

    public String prettyJSON() {
        try {
            return stopWatch.prettyJson();
        } catch (Exception e) {
            return "";
        }
    }
}
