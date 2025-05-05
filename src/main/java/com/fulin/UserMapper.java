package com.fulin;

/**
 * @Author: Fulin
 * @Description: 用户表
 * @DateTime: 2025/4/30 下午10:23
 **/
public interface UserMapper {
    User selectById(@Param("id") int id);

    User selectByName(@Param("name") String name);

    User selectByNameAndAge(@Param("name") String name, @Param("age") int age);
}
