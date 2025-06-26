package com.xm.bms.warn.service.impl;


import com.xm.bms.warn.service.CarSignalWarningReluService;
import com.xm.bms.warnrelu.domain.SignalData;
import com.xm.bms.warnrelu.domain.WarningResult;
import org.springframework.stereotype.Service;

@Service
public class CarSignalWarningReluServiceImpl implements CarSignalWarningReluService {

    /**
     * 根据规则ID和电池类型获取报警结果
     *
     * @param ruleId  规则ID
     * @param batteryType 电池类型
     * @param signalData 信号数据
     * @return WarningResult 包含报警等级和预警名称
     */
    @Override
    public WarningResult getWarningResult(Long ruleId, String batteryType, SignalData signalData) {
        WarningResult warningResult = null;

        // 根据规则ID和电池类型选择对应的报警方法
        if (ruleId == 1) { // 电压差报警
            // 判断电压数据是否完整
            if (signalData.getMx() != null && signalData.getMi() != null) {
                // 判断电池类型
                if ("三元电池".equals(batteryType)) {
                    warningResult = voltageDifferenceAlertForNCM(signalData.getMx(), signalData.getMi());
                } else if ("铁锂电池".equals(batteryType)) {
                    warningResult = voltageDifferenceAlertForLiFePO4(signalData.getMx(), signalData.getMi());
                }
            } else {
                // 如果电压数据不完整，返回 null
                warningResult = null;
            }
        } else if (ruleId == 2) { // 电流差报警
            // 判断电流数据是否完整
            if (signalData.getIx() != null && signalData.getIi() != null) {
                // 判断电池类型
                if ("三元电池".equals(batteryType)) {
                    warningResult = currentDifferenceAlertForNCM(signalData.getIx(), signalData.getIi());
                } else if ("铁锂电池".equals(batteryType)) {
                    warningResult = currentDifferenceAlertForLiFePO4(signalData.getIx(), signalData.getIi());
                }
            } else {
                // 如果电流数据不完整，返回 null
                warningResult = null;
            }
        }

        return warningResult;
    }

    // 电压差报警 - 三元电池
    @Override
    public WarningResult voltageDifferenceAlertForNCM(double maxVoltage, double minVoltage) {
        double diff = maxVoltage - minVoltage;
        int warnLevel;
        String warnName = "电压差报警 - 三元电池";

        if (diff >= 5) {
            warnLevel = 0;
        } else if (diff >= 3) {
            warnLevel = 1;
        } else if (diff >= 1) {
            warnLevel = 2;
        } else if (diff >= 0.6) {
            warnLevel = 3;
        } else if (diff >= 0.2) {
            warnLevel = 4;
        } else {
            warnLevel = -1;
        }

        return new WarningResult((long) warnLevel, warnName);
    }

    // 电压差报警 - 铁锂电池
    @Override
    public WarningResult voltageDifferenceAlertForLiFePO4(double maxVoltage, double minVoltage) {
        double diff = maxVoltage - minVoltage;
        int warnLevel;
        String warnName = "电压差报警 - 铁锂电池";

        if (diff >= 2) {
            warnLevel = 0;
        } else if (diff >= 1) {
            warnLevel = 1;
        } else if (diff >= 0.7) {
            warnLevel = 2;
        } else if (diff >= 0.4) {
            warnLevel = 3;
        } else if (diff >= 0.2) {
            warnLevel = 4;
        } else {
            warnLevel = -1;
        }

        return new WarningResult((long) warnLevel, warnName);
    }

    // 电流差报警 - 三元电池
    @Override
    public WarningResult currentDifferenceAlertForNCM(double maxCurrent, double minCurrent) {
        double diff = maxCurrent - minCurrent;
        int warnLevel;
        String warnName = "电流差报警 - 三元电池";

        if (diff >= 3) {
            warnLevel = 0;
        } else if (diff >= 1) {
            warnLevel = 1;
        } else if (diff >= 0.2) {
            warnLevel = 2;
        } else {
            warnLevel = -1;
        }

        return new WarningResult((long) warnLevel, warnName);
    }

    // 电流差报警 - 铁锂电池
    @Override
    public WarningResult currentDifferenceAlertForLiFePO4(double maxCurrent, double minCurrent) {
        double diff = maxCurrent - minCurrent;
        int warnLevel;
        String warnName = "电流差报警 - 铁锂电池";

        if (diff >= 1) {
            warnLevel = 0;
        } else if (diff >= 0.5) {
            warnLevel = 1;
        } else if (diff >= 0.2) {
            warnLevel = 2;
        } else {
            warnLevel = -1;
        }

        return new WarningResult((long) warnLevel, warnName);
    }
}
