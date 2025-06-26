package com.xm.bms.warnrelu.controller;

import com.xm.bms.warnrelu.domain.SignalData;
import com.xm.bms.warnrelu.domain.WarningResult;
import com.xm.bms.warnrelu.service.JudgeByRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warning")
public class WarningController {

    @Autowired
    private JudgeByRules judgeByRules;

    /**
     * 计算报警结果接口
     *
     * @param batteryType 电池类型
     * @param signalData  信号数据（通过请求体传入）
     * @return WarningResult 报警等级和名称
     */
    @PostMapping("/calculate")
    public WarningResult calculateWarning(@RequestParam Long ruleNo,
                                          @RequestParam String batteryType,
                                          @RequestBody SignalData signalData) {
        return judgeByRules.getWarningResult(ruleNo, batteryType, signalData);
    }
}
