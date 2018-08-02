package com.caox.sharding.service.impl;

import com.caox.sharding.entity.Student;
import com.caox.sharding.mapper.StudentMapper;
import com.caox.sharding.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by BF100 on 2018/7/10.
 */
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    public StudentMapper studentMapper;

    public boolean insert(Student student) {
        return studentMapper.insert(student) > 0 ? true : false;
    }
}
