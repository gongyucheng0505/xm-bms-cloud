package com.xm.bms.carinfo.mapper;

import com.xm.bms.carinfo.domain.CarInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarInfoMapper {

    CarInfo selectById(Long id);

    List<CarInfo> selectAll();

    int insert(CarInfo carInfo);

    int update(CarInfo carInfo);

    int deleteById(Long id);

    void batchInsertOrUpdate(List<CarInfo> carInfoList);
}
