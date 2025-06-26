package com.xm.bms.carinfo.service;

import com.xm.bms.carinfo.domain.CarInfo;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

public interface CarInfoService {
    CarInfo getCarInfoById(Long id);
    List<CarInfo> getAllCarInfos();
    void createCarInfo(CarInfo carInfo);
    void updateCarInfo(CarInfo carInfo);
    int deleteCarInfo(Long id) throws InterruptedException;

    void batchInsertOrUpdate(List<CarInfo> carInfoList);

    @DubboService
    CarInfo getCarBttearyById(Long id);
}
