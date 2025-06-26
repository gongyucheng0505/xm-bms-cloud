package com.xm.bms.warn.service;

import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.warn.domain.CarSignalWarning;
import com.xm.bms.warn.domain.WarnRequest;
import com.xm.bms.warn.domain.WarnResponse;

import java.util.List;

public interface CarSignalWarningService {
    public List<WarnResponse> processWarning(List<WarnRequest> warnData) ;
    void batchInsertOrUpdate(List<CarSignalWarning> carSignalWarningList);
}
