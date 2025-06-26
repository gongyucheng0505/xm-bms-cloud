package com.xm.bms.warn.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.carinfo.domain.CarInfoDTO;
import com.xm.bms.carinfo.mapper.CarInfoMapper;
import com.xm.bms.carinfo.service.CarInfoService;
import com.xm.bms.carinfo.service.DubboServiceCar;
import com.xm.bms.common.web.CommonResult;
import com.xm.bms.warn.domain.*;
import com.xm.bms.warn.mapper.CarSignalWarningMapper;
import com.xm.bms.warn.service.CarSignalWarningReluService;
import com.xm.bms.warn.service.CarSignalWarningService;
import com.xm.bms.warnrelu.domain.SignalData;
import com.xm.bms.warnrelu.domain.WarningResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarSignalWarningServiceImpl implements CarSignalWarningService {

    /**
     * 处理预警上报
     *
     * @param warnData 上报的预警数据
     * @return 处理后的预警结果
     */
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CarSignalWarningReluService carSignalWarningReluService;
    @Autowired
    private CarSignalWarningMapper carSignalWarningMapper;

    public List<WarnResponse> processWarning(List<WarnRequest> warnData) {
        List<WarnResponse> carSignalWarningList = new ArrayList<>();

        for (WarnRequest warnRequest : warnData) {
            Long carId = warnRequest.getCarId();
            Long warnId = warnRequest.getWarnId();
            String signal = warnRequest.getSignal();
            SignalData signalData = new SignalData();
            WarnResponse carSignalWarning = new WarnResponse();
            try {
                JsonNode rootNode = objectMapper.readTree(signal);
                // 手动将 JsonNode 中的字段值提取出来并设置到 SignalData 对象
                signalData.setMx(rootNode.get("Mx").asDouble());
                signalData.setMi(rootNode.get("Mi").asDouble());
                signalData.setIx(rootNode.get("Ix").asDouble());
                signalData.setIi(rootNode.get("Ii").asDouble());
                System.out.println(signalData);
            } catch (Exception e) {
                e.printStackTrace();
                // 错误处理：无法解析信号数据
            }

            String url = "http://localhost:8083/api/carInfos/" + carId;
            ResponseEntity<CommonResult> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    CommonResult.class // We expect the response to be wrapped in CommonResult
            );

            // Get the data (CarInfo object) from the response
            CarInfo carInfo = (CarInfo) response.getBody().getData();
            System.out.println(carInfo);
            if (carInfo == null) {
                // 处理无对应 carId 的情况（例如返回一个错误信息）
                carSignalWarningList.add(null);
            } else {
                String batteryType = carInfo.getBatteryType();
                if (warnId != null) {
                    // 根据指定的 warnId 计算报警等级
                    WarningResult warningResult = carSignalWarningReluService.getWarningResult(warnId, batteryType, signalData);
                    carSignalWarning.setCarId(carId);
                    carSignalWarning.setWarnLevel(Math.toIntExact(warningResult.getWarnLevel()));
                    carSignalWarning.setWarnName(warningResult.getWarnName());
                    carSignalWarning.setRuleId(warnId);
                    carSignalWarningList.add(carSignalWarning);
                } else {
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
        }
        return carSignalWarningList;
    }
    @Transactional
    @Override
    public void batchInsertOrUpdate(List<CarSignalWarning> carSignalWarningList) {
        carSignalWarningMapper.batchInsertOrUpdate(carSignalWarningList);
    }
}
