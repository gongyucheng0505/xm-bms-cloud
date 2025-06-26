package com.xm.bms.warn.mapper;

import com.xm.bms.warn.domain.CarSignalWarning;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarSignalWarningMapper {

    CarSignalWarning selectById(Long id);

    int batchInsertOrUpdate(List<CarSignalWarning> carSignalWarningList);

}
