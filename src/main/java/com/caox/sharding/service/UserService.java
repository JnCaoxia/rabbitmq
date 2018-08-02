package com.caox.sharding.service;

import com.caox.sharding.entity.User;

import java.util.List;

/**
 * Created by BF100 on 2018/7/10.
 */
public interface UserService {
    public boolean insert(User u);

    public List<User> findAll();

    public List<User> findByUserIds(List<Integer> ids);

    public void transactionTestSucess();

    public void transactionTestFailure() throws IllegalAccessException;

}
