package com.xm.bms.warnrelu.mapper;

import com.xm.bms.warnrelu.domain.BatteryWarningRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JudgeByRulesMapper {
    List<BatteryWarningRule> selectByRuleNoAndBatteryType(@Param("ruleNo") Long ruleNo,
                                                          @Param("batteryType") String batteryType);
}
