package com.xm.bms.warnrelu.domain;

import lombok.Data;

@Data
public class WarningResult {
    public WarningResult(Long warnLevel, String warnName) {
        this.warnLevel = warnLevel;
        this.warnName = warnName;
    }

    private Long warnLevel;    // 报警等级
    private String warnName;  // 预警名称
}
