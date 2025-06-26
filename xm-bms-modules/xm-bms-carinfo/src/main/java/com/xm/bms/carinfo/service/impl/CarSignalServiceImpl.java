package com.xm.bms.carinfo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xm.bms.carinfo.config.JedisClient;
import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.carinfo.domain.CarSignal;
import com.xm.bms.carinfo.mapper.CarInfoMapper;
import com.xm.bms.carinfo.mapper.CarSignalMapper;
import com.xm.bms.carinfo.service.CarSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarSignalServiceImpl implements CarSignalService {

    private static final String CACHE_PREFIX = "car:signal:";
    @Autowired
    private CarSignalMapper carSignalMapper;

    @Autowired
    private CarInfoMapper carInfoMapper;
    @Resource
    private JedisClient jedisClient;
    @Resource
    private ObjectMapper objectMapper;

    @Override

    public CarSignal getCarSignalById(Long id) {
        String key = CACHE_PREFIX + id;
        try (Jedis jedis = jedisClient.getJedis()) {
            // 1. 读缓存
            String json = jedis.get(key);
            if (json != null) {
                return objectMapper.readValue(json, CarSignal.class);
            }
            // 2. 未命中，查数据库
            CarSignal carSignal = carSignalMapper.selectById(id);
            if (carSignal != null) {
                // 3. 写缓存，设置30分钟过期
                jedis.setex(key, 1800, objectMapper.writeValueAsString(carSignal));
            }
            return carSignal;
        } catch (Exception e) {
            // Redis 出错，降级查询数据库
            return carSignalMapper.selectById(id);
        }
    }
    @Transactional
    @Override
    public List<CarSignal> getAllCarSignals() {
        return carSignalMapper.selectAll();
    }
    @Transactional
    @Override
    public int createCarSignal(CarSignal carSignal) {
        // 查询车id
        CarInfo carInfo = carInfoMapper.selectById(carSignal.getCarId());
        // 判断车信息是否存在
        if (carInfo != null) {
            // 设置报告时间
            LocalDateTime now = LocalDateTime.now();
            carSignal.setReportTime(now);

            // 插入新的 carSignal 数据
            carSignalMapper.insert(carSignal);
            // 返回 1 表示操作成功
            return 1;
        } else {
            // 返回 0 表示车 ID 不存在
            return 0;
        }
    }

    @Override
    public void updateCarSignal(CarSignal carSignal) {
        // 1. 更新数据库
        carSignalMapper.update(carSignal);
        // 2. 删除旧缓存
        try (Jedis jedis = jedisClient.getJedis()) {
            jedis.del(CACHE_PREFIX + carSignal.getId());  // 删除缓存
        } catch (Exception e) {
            // 如果删除缓存失败，记录日志
        }
        // 3. 等待缓存清理完成后再更新缓存
        try {
            Thread.sleep(100);  // 延迟100ms，确保缓存被清理
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 4. 将新数据写入缓存
        try (Jedis jedis = jedisClient.getJedis()) {
            String key = CACHE_PREFIX + carSignal.getId();
            jedis.setex(key, 1800, objectMapper.writeValueAsString(carSignal));  // 设置30分钟过期时间
        } catch (Exception e) {
            // 如果写入缓存失败，记录日志
        }
    }


    @Override
    public int deleteCarSignal(Long id) {
       int result=carSignalMapper.deleteCarSignal(id);
        // 再次删除缓存
        try (Jedis jedis = jedisClient.getJedis()) {
            jedis.del(CACHE_PREFIX + id);
        } catch (Exception e) {
            e.printStackTrace();  // 打印异常堆栈信息
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {

        }
        // 再次删除缓存
        try (Jedis jedis = jedisClient.getJedis()) {
            jedis.del(CACHE_PREFIX + id);
        } catch (Exception e) {
            e.printStackTrace();  // 打印异常堆栈信息
            // 或者使用日志记录，比如：logger.error("延迟删除缓存失败", e);
        }

        return result;
    }
}
