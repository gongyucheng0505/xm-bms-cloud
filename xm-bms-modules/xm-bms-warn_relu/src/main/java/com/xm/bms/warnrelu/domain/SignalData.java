package com.xm.bms.warnrelu.domain;

import lombok.Data;

@Data
public class SignalData {


    private Double mx; // 最大电压
    private Double mi; // 最小电压
    private Double ix; // 最大电流
    private Double ii; // 最小电流
}
