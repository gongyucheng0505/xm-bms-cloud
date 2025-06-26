package com.xm.bms.warnrelu.domain;

import lombok.Data;

import java.util.Date;
@Data
public class BatteryWarningRule {
    private Long id;
    private Long ruleNo;
    private String name;
    private String batteryType;
    private String ruleDesc;
    private Date createTime;


}
