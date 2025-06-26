package com.xm.bms.warnrelu.service;

import com.xm.bms.warnrelu.domain.SignalData;
import com.xm.bms.warnrelu.domain.WarningResult;

public interface JudgeByRules {
    public WarningResult getWarningResult(Long reluNo, String batteryType, SignalData signalData);
}
