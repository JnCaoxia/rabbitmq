package com.caox.sharding.mapper;

import com.caox.sharding.entity.Student;

import java.util.List;

/**
 * Created by BF100 on 2018/7/10.
 */
public interface StudentMapper {
    Integer insert(Student s);

    List<Student> findAll();

    List<Student> findByStudentIds(List<Integer> studentIds);
}
