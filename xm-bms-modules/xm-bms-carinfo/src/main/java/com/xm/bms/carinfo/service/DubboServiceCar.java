package com.xm.bms.carinfo.service;

import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.carinfo.domain.CarInfoDTO;

public interface DubboServiceCar {

    CarInfo getCarBttearyById(Long id);
}
