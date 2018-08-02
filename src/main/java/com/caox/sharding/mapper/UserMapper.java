package com.caox.sharding.mapper;

import com.caox.sharding.entity.User;

import java.util.List;

/**
 * Created by BF100 on 2018/7/10.
 */
public interface UserMapper {
    Integer insert(User u);

    List<User> findAll();

    List<User> findByUserIds(List<Integer> userIds);
}
