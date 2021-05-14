package com.lgdisplay.bigdata.api.glue.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ImportResource(locations = {"classpath:applicationContext*.xml"})
@EnableWebMvc
public class Starter {

    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }

}
