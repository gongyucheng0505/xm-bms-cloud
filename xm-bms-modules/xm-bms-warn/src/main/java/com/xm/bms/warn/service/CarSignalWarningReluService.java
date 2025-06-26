package com.xm.bms.warn.service;

import com.xm.bms.warnrelu.domain.SignalData;
import com.xm.bms.warnrelu.domain.WarningResult;

public interface CarSignalWarningReluService {
    WarningResult getWarningResult(Long ruleId, String batteryType, SignalData signalData);


    /**
     * 电压差报警 - 三元电池
     *
     * @param maxVoltage 最大电压
     * @param minVoltage 最小电压
     * @return WarningResult 包含报警等级和预警名称
     */
    WarningResult voltageDifferenceAlertForNCM(double maxVoltage, double minVoltage);

    /**
     * 电压差报警 - 铁锂电池
     *
     * @param maxVoltage 最大电压
     * @param minVoltage 最小电压
     * @return WarningResult 包含报警等级和预警名称
     */
    WarningResult voltageDifferenceAlertForLiFePO4(double maxVoltage, double minVoltage);

    /**
     * 电流差报警 - 三元电池
     *
     * @param maxCurrent 最大电流
     * @param minCurrent 最小电流
     * @return WarningResult 包含报警等级和预警名称
     */
    WarningResult currentDifferenceAlertForNCM(double maxCurrent, double minCurrent);

    /**
     * 电流差报警 - 铁锂电池
     *
     * @param maxCurrent 最大电流
     * @param minCurrent 最小电流
     * @return WarningResult 包含报警等级和预警名称
     */
    WarningResult currentDifferenceAlertForLiFePO4(double maxCurrent, double minCurrent);
}
