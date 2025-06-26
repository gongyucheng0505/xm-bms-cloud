package com.xm.bms.user.service.impl;

import com.xm.bms.user.domain.User;
import com.xm.bms.user.mapper.UserMapper;
import com.xm.bms.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public void createUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        userMapper.insert(user);
    }

    @Override
    public void updateUser(User user) {
        userMapper.update(user);
    }

    @Override
    public int deleteUser(Long id) {
        return userMapper.deleteById(id);
    }

    @Override
    public void batchInsertOrUpdate(List<User> userList) {

        userMapper.batchInsertOrUpdate(userList);

    }
}
