package com.xm.bms.carinfo;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@DubboComponentScan(basePackages = "com.xm.bms.carinfo.service.impl") //
public class CarinfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarinfoApplication.class, args);
    }

}
