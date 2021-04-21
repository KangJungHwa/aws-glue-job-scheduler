package com.lgdisplay.bigdata.api.glue.scheduler.exception;

import com.lgdisplay.bigdata.api.glue.scheduler.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(basePackages = "com.lgdisplay.bigdata.api.glue.scheduler")
@RestController
@Slf4j
public class GlobalRestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = UnsupportedOperationException.class)
    public String handleBaseException(UnsupportedOperationException e) {
        log.warn("요청을 처리하던 도중 에러가 발생했습니다.\n{}", ExceptionUtils.getFullStackTrace(e));
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public String handleBaseException(HttpMessageNotReadableException e) {
        log.warn("요청을 처리하던 도중 에러가 발생했습니다.\n{}", ExceptionUtils.getFullStackTrace(e));
        return e.getMessage();
    }

}
