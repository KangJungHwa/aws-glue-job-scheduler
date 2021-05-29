package com.lgdisplay.bigdata.api.glue.scheduler.controller;

import com.lgdisplay.bigdata.api.glue.scheduler.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/glue")
public class ServerHealthController {

    @Autowired
    ResourceService resourceService;

    @GetMapping("/status")
    public ResponseEntity service() {
        return ResponseEntity.ok(resourceService.getApiServerHealth());
    }

}