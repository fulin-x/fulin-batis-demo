package com.fulin;

import lombok.Data;

/**
 * @Author: Fulin
 * @Description: 用户表
 * @DateTime: 2025/4/30 下午10:22
 **/
@Data
@Table(tableName = "user")
public class User {
    private Integer id;
    private String name;
    private Integer age;
}
