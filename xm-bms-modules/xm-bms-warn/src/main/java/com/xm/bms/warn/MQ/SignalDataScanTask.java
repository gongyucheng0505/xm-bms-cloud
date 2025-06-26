        package com.xm.bms.warn.MQ;

        import com.xm.bms.carinfo.domain.CarSignal;
        import com.xm.bms.carinfo.mapper.CarSignalMapper;
        import com.xm.bms.common.web.CommonResult;
        import lombok.extern.slf4j.Slf4j;
        import org.apache.rocketmq.spring.core.RocketMQTemplate;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.core.ParameterizedTypeReference;
        import org.springframework.http.HttpMethod;
        import org.springframework.http.ResponseEntity;
        import org.springframework.scheduling.annotation.Scheduled;
        import org.springframework.stereotype.Component;
        import org.springframework.web.client.RestTemplate;

        import java.util.List;

        @Slf4j
        @Component
        public class SignalDataScanTask {

            @Autowired
            private RocketMQTemplate rocketMQTemplate;
            @Autowired
            private RestTemplate restTemplate;
            @Autowired
            private CarSignalMapper carSignalMapper;

                @Scheduled(fixedRate = 60000)
                public void scanAndSendSignalData() {
                    try {
                        ResponseEntity<CommonResult<List<CarSignal>>> response = restTemplate.exchange(
                                "http://localhost:8083/api/carSignals",
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<CommonResult<List<CarSignal>>>() {
                                }
                        );

                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            List<CarSignal> carSignals = response.getBody().getData();
                            if (carSignals != null && !carSignals.isEmpty()) {
                                rocketMQTemplate.convertAndSend("signal-topic", carSignals);
                                log.info("发送信号数据：{}", carSignals);
                            } else {
                                log.warn("未获取到任何车辆信号数据");
                            }
                        } else {
                            log.error("请求 carSignals 接口失败：{}", response.getStatusCode());
                        }
                    } catch (Exception e) {
                        log.error("扫描并发送信号数据时出错", e);
                    }
                }
        }
