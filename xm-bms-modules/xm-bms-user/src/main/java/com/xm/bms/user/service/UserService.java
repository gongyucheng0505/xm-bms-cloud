package com.xm.bms.user.service;

import com.xm.bms.user.domain.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    void createUser(User user);
    void updateUser(User user);
    int deleteUser(Long id);

    void batchInsertOrUpdate(List<User> userList);
}
