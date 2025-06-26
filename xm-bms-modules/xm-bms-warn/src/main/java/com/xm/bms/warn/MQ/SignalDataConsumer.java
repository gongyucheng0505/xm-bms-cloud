package com.xm.bms.warn.MQ;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.carinfo.domain.CarSignal;
import com.xm.bms.carinfo.service.CarInfoService;
import com.xm.bms.common.web.CommonResult;
import com.xm.bms.warn.domain.CarSignalWarning;
import com.xm.bms.warn.domain.WarnRequest;
import com.xm.bms.warn.domain.WarnResponse;
import com.xm.bms.warn.mapper.CarSignalWarningMapper;
import com.xm.bms.warn.service.CarSignalWarningReluService;
import com.xm.bms.warn.service.CarSignalWarningService;
import com.xm.bms.warnrelu.domain.SignalData;
import com.xm.bms.warnrelu.domain.WarningResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "signal-topic",
        consumerGroup = "signal-consumer-group",
        selectorExpression = "*"
)
public class SignalDataConsumer implements RocketMQListener<List<CarSignal>> {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CarSignalWarningReluService carSignalWarningReluService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CarSignalWarningMapper carSignalWarningMapper;
    @Override
    public void onMessage(List<CarSignal> carSignals) {
        System.out.println("接收数据"+carSignals);
        List<CarSignalWarning> carSignalWarningList = new ArrayList<>();

        for (CarSignal carSignal : carSignals) {
            Long carId = carSignal.getCarId();
            CarSignalWarning carSignalWarning = new CarSignalWarning();
            SignalData signalData = new SignalData();
            signalData.setMx(carSignal.getMaxVoltage());
            signalData.setMi(carSignal.getMinVoltage());
            signalData.setIx(carSignal.getMaxCurrent());
            signalData.setIi(carSignal.getMinCurrent());
            String url = "http://localhost:8083/api/carInfos/" + carId;
            ResponseEntity<CommonResult> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    CommonResult.class
            );
            Object data = Objects.requireNonNull(response.getBody()).getData();
            CarInfo carInfo = objectMapper.convertValue(data, CarInfo.class);
            System.out.println(carInfo);
            if (carInfo == null) {
                // 处理无对应 carId 的情况（例如返回一个错误信息）
                carSignalWarningList.add(null);
            } else {
                String batteryType = carInfo.getBatteryType();
                for (long ruleId = 1; ruleId <= 2; ruleId++) {
                    if (ruleId == 1) {
                        WarningResult warningResult = carSignalWarningReluService.getWarningResult(ruleId, batteryType, signalData);
                        carSignalWarning.setCarId(carId);
                        carSignalWarning.setWarnLevel(Math.toIntExact(warningResult.getWarnLevel()));
                        carSignalWarning.setWarnName(warningResult.getWarnName());
                        carSignalWarning.setRuleId(1L);
                        carSignalWarningList.add(carSignalWarning);
                    }

                    if (ruleId == 2) {
                        WarningResult warningResult = carSignalWarningReluService.getWarningResult(ruleId, batteryType, signalData);
                        carSignalWarning.setCarId(carId);
                        carSignalWarning.setWarnLevel(Math.toIntExact(warningResult.getWarnLevel()));
                        carSignalWarning.setWarnName(warningResult.getWarnName());
                        carSignalWarning.setRuleId(2L);
                        carSignalWarningList.add(carSignalWarning);
                    }
                }
            }
        }
        // 1. 过滤 null 值
        List<CarSignalWarning> validWarnings = carSignalWarningList
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        System.out.println("接收到的信号数据：" + carSignalWarningList);
        try {
            int result = carSignalWarningMapper.batchInsertOrUpdate(validWarnings);
            log.info("执行成功，影响记录数：{}", result);
        } catch (Exception e) {
            log.error("批量插入或更新失败", e);
        }

    }
}
