package com.xm.bms.carinfo.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CarInfo {

    @ExcelProperty("车辆ID")
    private Long id;

    @ExcelProperty("车辆唯一标识VID")
    private String vid;
    @ExcelProperty("电池类型")
    @JSONField(serialize = false)
    private String batteryType;

    @ExcelProperty("总行驶公里数")
    private Integer totalKm;

    @ExcelProperty("健康率（%）")
    private BigDecimal healthRate;

    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
