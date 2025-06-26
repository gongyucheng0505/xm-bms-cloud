package com.xm.bms.carinfo.mapper;

import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.carinfo.domain.CarSignal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarSignalMapper {

    CarSignal selectById(Long id);

    List<CarSignal> selectAll();

    int insert(CarSignal carSignal);

    int update(CarSignal carSignal);

    int deleteById(Long id);
    int deleteCarSignal(Long id);

}
