package com.xm.bms.warnrelu.service.impl;

import com.xm.bms.warnrelu.domain.BatteryWarningRule;
import com.xm.bms.warnrelu.domain.SignalData;
import com.xm.bms.warnrelu.domain.WarningResult;
import com.xm.bms.warnrelu.mapper.JudgeByRulesMapper;
import com.xm.bms.warnrelu.service.JudgeByRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JudgeByRulesImpl implements JudgeByRules {


    @Autowired
    private JudgeByRulesMapper judgeByRulesMapper;


    @Override
    public WarningResult getWarningResult(Long reluNo, String batteryType, SignalData signalData) {
        List<BatteryWarningRule> rules = judgeByRulesMapper.selectByRuleNoAndBatteryType(reluNo, batteryType);
        if (rules == null || rules.isEmpty()) {
            return new WarningResult(-1L, "无规则");
        } else {
            System.out.println(rules);

            WarningResult warningResult = null;
            return warningResult;
        }
    }

}
