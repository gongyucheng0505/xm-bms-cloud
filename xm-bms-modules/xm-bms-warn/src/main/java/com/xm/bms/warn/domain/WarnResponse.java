package com.xm.bms.warn.domain;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class WarnResponse{
    private Long carId;       // 车辆 ID
    private Long ruleId;      // 规则 ID
    private Integer warnLevel; // 预警等级
    private String warnName;  // 预警名称
}
