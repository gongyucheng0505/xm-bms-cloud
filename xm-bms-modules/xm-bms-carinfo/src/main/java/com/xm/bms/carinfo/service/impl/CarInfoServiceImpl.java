package com.xm.bms.carinfo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xm.bms.carinfo.config.JedisClient;
import com.xm.bms.carinfo.domain.CarInfo;
import com.xm.bms.carinfo.mapper.CarInfoMapper;
import com.xm.bms.carinfo.mapper.CarSignalMapper;
import com.xm.bms.carinfo.service.CarInfoService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarInfoServiceImpl implements CarInfoService {

    private static final String CACHE_PREFIX = "car:info:";
    @Autowired
    private CarInfoMapper carInfoMapper;
    @Autowired
    private CarSignalMapper carSignalMapper;
    @Resource
    private JedisClient jedisClient;
    @Resource
    private ObjectMapper objectMapper;

    @Override

    public CarInfo getCarInfoById(Long id) {
        String key = CACHE_PREFIX + id;
        try (Jedis jedis = jedisClient.getJedis()) {
            // 1. 读缓存
            String json = jedis.get(key);
            if (json != null) {
                return objectMapper.readValue(json, CarInfo.class);
            }
            // 2. 未命中，查数据库
            CarInfo carInfo = carInfoMapper.selectById(id);
            if (carInfo != null) {
                // 3. 写缓存，设置30分钟过期
                jedis.setex(key, 1800, objectMapper.writeValueAsString(carInfo));
            }
            return carInfo;
        } catch (Exception e) {
            // Redis 出错，降级查询数据库
            return carInfoMapper.selectById(id);
        }
    }

    @Override
    public List<CarInfo> getAllCarInfos() {
        return carInfoMapper.selectAll();
    }

    @Override
    public void createCarInfo(CarInfo carInfo) {
        LocalDateTime now = LocalDateTime.now();
        carInfo.setCreateTime(now);
        // 生成16位VID：以"XM2025"开头 + 10位随机数字
        String vid = "XM2025" + RandomStringUtils.randomNumeric(10);
        carInfo.setVid(vid);

        carInfoMapper.insert(carInfo);
    }

    @Override
    public void updateCarInfo(CarInfo carInfo) {
        // 1. 更新数据库
        carInfoMapper.update(carInfo);
        // 2. 删除旧缓存
        try (Jedis jedis = jedisClient.getJedis()) {
            jedis.del("car:info:" + carInfo.getId());  // 删除缓存
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
            String key = "car:info:" + carInfo.getId();
            jedis.setex(key, 1800, objectMapper.writeValueAsString(carInfo));  // 设置30分钟过期时间
        } catch (Exception e) {
            // 如果写入缓存失败，记录日志
        }
    }


    @Transactional
    @Override
    public int deleteCarInfo(Long id) {
        // 1. 删除缓存，防止脏数据
        try (Jedis jedis = jedisClient.getJedis()) {
            jedis.del("car:info:" + id);  // 删除车辆信息缓存
            jedis.del("car:signal:" + id);  // 删除车辆信息缓存
        } catch (Exception e) {
            e.printStackTrace();  // 打印异常堆栈信息
        }

        // 2. 延时删除缓存，防止缓存未及时更新
        try {
            Thread.sleep(200);  // 延时100ms确保缓存删除操作完成
        } catch (InterruptedException ignored) {
            ignored.printStackTrace();  // 打印异常
        }

        // 3. 删除与车辆ID相关的状态信息
        carSignalMapper.deleteCarSignal(id);  // 确保你已在 Mapper 中定义该方法

        // 4. 删除车辆信息
        int result = carInfoMapper.deleteById(id);

        // 5. 再次删除缓存（如果需要的话）
        try (Jedis jedis = jedisClient.getJedis()) {
            jedis.del("car:info:" + id);  // 再次删除缓存
            jedis.del("car:signal:" + id);  // 删除车辆信息缓存

        } catch (Exception e) {
            e.printStackTrace();  // 打印异常堆栈信息
            // 或者使用日志记录，比如：logger.error("延迟删除缓存失败", e);
        }

        return result;  // 返回删除结果
    }

    @Override
    public void batchInsertOrUpdate(List<CarInfo> carInfoList) {
        carInfoMapper.batchInsertOrUpdate(carInfoList);
    }
    @Override
    public CarInfo getCarBttearyById(Long id) {

        return carInfoMapper.selectById(id);
    }
}
