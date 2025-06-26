package com.xm.bms.warn;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableDubbo(scanBasePackages = "com.xm.bms.service.mapper")
//@EnableDiscoveryClient
@EnableScheduling
@ComponentScan(basePackages = "com.xm.bms")  // 确保扫描到你的服务类包
@MapperScan({"com.xm.bms.carinfo.mapper", "com.xm.bms.warn.mapper"})
@SpringBootApplication
public class WarnApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarnApplication.class, args);
    }

}
