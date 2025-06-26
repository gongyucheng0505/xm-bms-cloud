package com.xm.bms.carinfo.controller;

import com.alibaba.excel.EasyExcel;

import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.carinfo.service.CarInfoService;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/carInfos")
public class CarInfoController {

    @Autowired
    private CarInfoService carInfoService;

    @GetMapping("/{id}")
    public CommonResult<CarInfo> getCarInfo(@PathVariable Long id) {
        CarInfo carInfo = carInfoService.getCarInfoById(id);
        if (carInfo == null) {
            return CommonResult.fail(404, "车辆信息不存在");
        }
        return CommonResult.success(carInfo);
    }

    @GetMapping
    public CommonResult<List<CarInfo>> getAll() {
        List<CarInfo> carInfos = carInfoService.getAllCarInfos();
        return CommonResult.success(carInfos);
    }

    @PostMapping
    public CommonResult<Void> create(@RequestBody CarInfo carInfo) {

        carInfoService.createCarInfo(carInfo);
        return CommonResult.success("车辆信息插入成功");
    }

    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody CarInfo carInfo) {
        carInfo.setId(id);
        carInfoService.updateCarInfo(carInfo);
        return CommonResult.success("车辆信息修改成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<String> deleteCarInfo(@PathVariable Long id) throws InterruptedException {
        int result = carInfoService.deleteCarInfo(id);

        // 判断返回的结果
        if (result == 0) {
            // 删除失败，说明没有找到对应的记录
            return CommonResult.fail(404, "记录不存在");
        } else {
            // 删除成功
            return CommonResult.success("状态信息删除成功");
        }
    }
    @PostMapping("/export")
    public Mono<Void> export(ServerHttpResponse response) {
        List<CarInfo> carInfos = carInfoService.getAllCarInfos();

        try {
            // 先将数据写入内存字节数组流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EasyExcel.write(out, CarInfo.class).sheet("车辆信息数据").doWrite(carInfos);

            byte[] bytes = out.toByteArray();

            // 设置响应头
            String fileName = URLEncoder.encode("车辆信息数据", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20") + ".xlsx";

            response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName);
            response.getHeaders().setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            response.getHeaders().setContentLength(bytes.length);

            // 返回响应体
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }


    @PostMapping("/import")
    public Mono<String> importData(ServerHttpRequest request) {
        return DataBufferUtils.join(request.getBody())
                .flatMap(dataBuffer -> {
                    try (InputStream inputStream = dataBuffer.asInputStream(true)) {
                        List<CarInfo> carInfoList = EasyExcel.read(inputStream)
                                .head(CarInfo.class)
                                .sheet()
                                .doReadSync();
                        LocalDateTime now = LocalDateTime.now();
                        for (CarInfo carInfo : carInfoList) {
                            if (carInfo.getCreateTime() == null) {
                                carInfo.setCreateTime(now);
                            }
                            // 生成16位vid，比如 "XM2025" + 10位数字随机数
                            String vid = "XM2025" + RandomStringUtils.randomNumeric(10);
                            carInfo.setVid(vid);
                        }

                        carInfoService.batchInsertOrUpdate(carInfoList);
                        return Mono.just("导入成功，导入条数：" + carInfoList.size());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }


    @GetMapping("/importTemplate")
    public Mono<Void> importTemplate(ServerHttpResponse response) {
        String fileName = UriUtils.encode("车辆信息导入模板.xlsx", StandardCharsets.UTF_8);
        response.getHeaders().setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        response.getHeaders().setContentDispositionFormData("attachment", fileName);

        return Mono.fromCallable(() -> {
            // 先写入内存流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            EasyExcel.write(baos, CarInfo.class)
                    .sheet("模板")
                    .doWrite(new ArrayList<>());  // 空数据写模板
            return baos.toByteArray();
        }).flatMap(bytes -> {
            // 再将字节写入响应的 DataBuffer
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        });
    }


}
