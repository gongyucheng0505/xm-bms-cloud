package com.xm.bms.carinfo.controller;

import com.alibaba.excel.EasyExcel;
import com.xm.bms.carinfo.domain.CarSignal;
import com.xm.bms.carinfo.service.CarInfoService;
import com.xm.bms.carinfo.service.CarSignalService;
import com.xm.bms.common.web.CommonResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/carSignals")
public class CarSignalController {

    @Resource
    private CarSignalService carSignalService;

    @GetMapping("/{id}")
    public CommonResult<CarSignal> getCarInfo(@PathVariable Long id) {
        CarSignal carSignal = carSignalService.getCarSignalById(id);
        if (carSignal == null) {
            return CommonResult.fail(404, "状态信息不存在");
        }
        return CommonResult.success(carSignal);
    }

    @GetMapping
    public CommonResult<List<CarSignal>> getAll() {
        List<CarSignal> carSignalList = carSignalService.getAllCarSignals();
        return CommonResult.success(carSignalList);
    }

    @PostMapping
    public CommonResult<Void> create(@RequestBody CarSignal carSignal) {
        int result = carSignalService.createCarSignal(carSignal);
        if (result == 0) {
            return CommonResult.fail(404, "车id不存在");
        } else {
            return CommonResult.success("状态信息新增成功");
        }
    }

    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody CarSignal carSignal) {
        carSignal.setId(id);
        carSignalService.updateCarSignal(carSignal);
        return CommonResult.success("状态信息修改成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<String> deleteCarSignal(@PathVariable Long id) throws InterruptedException {
        int result = carSignalService.deleteCarSignal(id);

        // 判断返回的结果
        if (result == 0) {
            // 删除失败，说明没有找到对应的记录
            return CommonResult.fail(404, "记录不存在");
        } else {
            // 删除成功
            return CommonResult.success("状态信息删除成功");
        }
    }

}
