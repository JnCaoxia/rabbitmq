package com.caox.sharding.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by BF100 on 2018/7/10.
 */
@Setter
@Getter
@ToString
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer userId;

    private String name;

    private Integer age;
}
