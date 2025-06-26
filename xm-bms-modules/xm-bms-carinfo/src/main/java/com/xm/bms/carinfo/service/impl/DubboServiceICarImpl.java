package com.xm.bms.carinfo.service.impl;

import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.carinfo.mapper.CarInfoMapper;
import com.xm.bms.carinfo.service.DubboServiceCar;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service  // 确保 Spring 管理该类
public class DubboServiceICarImpl implements DubboServiceCar {
    @Autowired
    private CarInfoMapper carInfoMapper;
    @Override
    public CarInfo  getCarBttearyById(Long id) {

        return carInfoMapper.selectById(id);
    }
}
