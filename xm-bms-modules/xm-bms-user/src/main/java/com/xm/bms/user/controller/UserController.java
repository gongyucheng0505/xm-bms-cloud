package com.xm.bms.user.controller;

import com.alibaba.excel.EasyExcel;
import com.xm.bms.common.web.CommonResult;
import com.xm.bms.user.domain.User;
import com.xm.bms.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public CommonResult<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return CommonResult.fail(404, "用户不存在");
        }
        return CommonResult.success(user);
    }

    @GetMapping
    public CommonResult<List<User>> getAll() {
        List<User> users = userService.getAllUsers();
        return CommonResult.success(users);
    }

    @PostMapping
    public CommonResult<Void> create(@RequestBody User user) {
        userService.createUser(user);
        return CommonResult.success("插入成功");
    }

    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return CommonResult.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<String> deleteUser(@PathVariable Long id) throws InterruptedException {
        int result = userService.deleteUser(id);

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
        List<User> users = userService.getAllUsers();

        try {
            // 先将数据写入内存字节数组流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EasyExcel.write(out, User.class).sheet("用户数据").doWrite(users);

            byte[] bytes = out.toByteArray();

            // 设置响应头
            String fileName = URLEncoder.encode("用户数据", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20") + ".xlsx";

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
                        List<User> userList = EasyExcel.read(inputStream)
                                .head(User.class)
                                .sheet()
                                .doReadSync();

                        LocalDateTime now = LocalDateTime.now();
                        for (User user : userList) {
                            if (user.getCreateTime() == null) {
                                user.setCreateTime(now);
                            }
                        }

                        userService.batchInsertOrUpdate(userList);
                        return Mono.just("导入成功，导入条数：" + userList.size());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }



    @GetMapping("/importTemplate")
    public Mono<Void> importTemplate(ServerHttpResponse response) {
        String fileName = UriUtils.encode("用户导入模板.xlsx", StandardCharsets.UTF_8);
        response.getHeaders().setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        response.getHeaders().setContentDispositionFormData("attachment", fileName);

        return Mono.fromCallable(() -> {
            // 先写入内存流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            EasyExcel.write(baos, User.class)
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
