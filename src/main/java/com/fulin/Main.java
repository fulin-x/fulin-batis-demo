package com.fulin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @Author: Fulin
 * @Description: 主函数
 * @DateTime: 2025/4/30 下午10:06
 **/
public class Main {
    public static void main(String[] args) {
        FulinSqlSessionFactory fulinSqlSessionFactory = new FulinSqlSessionFactory();
        UserMapper userMapper = fulinSqlSessionFactory.getMapper(UserMapper.class);
        System.out.println(userMapper.selectById(1));
        System.out.println(userMapper.selectByName("李四"));
        System.out.println(userMapper.selectByNameAndAge("张三", 21));
    }

    private static User jdbcSelectById(int id){
        String jdbcUrl = "jdbc:mysql://192.168.5.99:3306/db_batis?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String password = "root";

        String sql = "select id, name, age from user where id = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(id);
                    user.setName(resultSet.getString("name"));
                    user.setAge(resultSet.getInt("age"));
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}