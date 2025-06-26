package com.xm.bms.warn.domain;

import lombok.Data;

@Data
public class WarnRequest {

    private Long carId;
    private Long warnId;  // 可选
    private String signal;   // 信号（JSON格式）

}
