package com.fulin;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Fulin
 * @Description: SqlSession工厂
 * @DateTime: 2025/4/30 下午10:24
 **/
public class FulinSqlSessionFactory {

    private static String JDBC_URL = "jdbc:mysql://192.168.5.99:3306/db_batis?useSSL=false&serverTimezone=UTC";
    private static String DB_USER = "root";
    private static String PASSWORD = "root";

    @SuppressWarnings("all")
    public <T> T getMapper(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz},
                new MapperInvocationHandler());
    }

    static class MapperInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().startsWith("select")) {
                return invokeSelect(proxy, method, args);
            }
            return null;
        }

        private Object invokeSelect(Object proxy, Method method, Object[] args) {
            String sql = createSelectSql(method);
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                for(int i = 0; i < args.length; i++){
                    Object arg = args[i];
                    if (arg instanceof Integer) {
                        statement.setInt(i + 1, (int) arg);
                    } else if (arg instanceof String) {
                        statement.setString(i + 1, arg.toString());
                    } else {
                        throw new RuntimeException("参数类型不支持");
                    }
                }
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                    return parseResult(resultSet, method.getReturnType());
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Object parseResult(ResultSet resultSet, Class<?> returnType) throws Exception {
            Constructor<?> constructor = returnType.getConstructor();
            Object result = constructor.newInstance();
            Field[] declaredFields = returnType.getDeclaredFields();
            for (Field field : declaredFields) {
                Object column = null;
                String name = field.getName();
                if (field.getType() == String.class) {
                    column = resultSet.getString(name);
                }else if(field.getType() == Integer.class){
                    column = resultSet.getInt(name);
                }
                field.setAccessible(true);
                field.set(result,column);
            }
            return result;
        }

        private String createSelectSql(Method method) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT ");
            List<String> selectCols = getSelectCols(method.getReturnType());
            sqlBuilder.append(String.join(",", selectCols));
            sqlBuilder.append(" FROM ");
            String tableName = getSelectTableName(method.getReturnType());
            sqlBuilder.append(tableName);
            sqlBuilder.append(" WHERE ");
            String where = getSelectWhere(method);
            sqlBuilder.append(where);
            sqlBuilder.append(";");
            System.out.println(sqlBuilder);
            return sqlBuilder.toString();
        }

        private String getSelectWhere(Method method) {
            return Arrays.stream(method.getParameters())
                    .map((parameter) -> {
                        Param param = parameter.getAnnotation(Param.class);
                        String column = param.value();
                        return column + " = ?";
                    }).collect(Collectors.joining(" and "));
        }

        private String getSelectTableName(Class<?> returnType) {
            Table table = returnType.getAnnotation(Table.class);
            if (table == null) {
                throw new RuntimeException("返回值无法确定查询表");
            }
            return table.tableName();
        }

        private List<String> getSelectCols(Class<?> returnType) {
            Field[] declaredFields = returnType.getDeclaredFields();
            return Arrays.stream(declaredFields).map(Field::getName).toList();
        }
    }
}
