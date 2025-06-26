package com.xm.bms.carinfo.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CarSignal {

    @ExcelProperty("ID")
    private Long id;  // 数据库中对应的 id

    @ExcelProperty("车辆ID")
    private Long carId;  // 车辆ID，数据库中对应的 car_id

    @ExcelProperty("最大电压")
    private Double maxVoltage;  // 最大电压

    @ExcelProperty("最小电压")
    private Double minVoltage;  // 最小电压

    @ExcelProperty("最大电流")
    private Double maxCurrent;  // 最大电流

    @ExcelProperty("最小电流")
    private Double minCurrent;  // 最小电流

    @ExcelProperty("报告时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportTime;  // 报告时间，格式：yyyy-MM-dd HH:mm:ss
}
