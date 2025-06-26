package com.xm.bms.carinfo.domain;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CarInfoDTO {
    private Long id;
    private String vid;
    private String batteryType;
    private Integer totalKm;
    private BigDecimal healthRate;
    private String createTime; // 用字符串表示时间

    // 构造方法、Getter 和 Setter 方法
}
