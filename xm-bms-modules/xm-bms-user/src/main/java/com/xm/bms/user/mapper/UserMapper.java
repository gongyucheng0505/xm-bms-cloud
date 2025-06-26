package com.xm.bms.user.mapper;

import com.xm.bms.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(Long id);

    List<User> selectAll();

    int insert(User user);

    int update(User user);

    int deleteById(Long id);

    void batchInsertOrUpdate(List<User> userList);
}
