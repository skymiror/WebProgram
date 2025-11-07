package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    // MySQL 8.x 驱动类（可省略静态加载，但建议保留）
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    // URL：必须加时区参数serverTimezone=UTC，关闭SSL
    private static final String URL = "jdbc:mysql://localhost:3306/web-sql?serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // 静态代码块：加载驱动（MySQL 8.x可省略，但加了更稳妥）
    static {
        try {
            Class.forName(DRIVER);
            System.out.println("MySQL 8.x 驱动加载成功"); // 用于验证
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL驱动加载失败，请检查依赖是否正确");
        }
    }

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        System.out.println("=== DBUtil 尝试连接数据库 ===");
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("数据库连接成功：" + conn); // 用于验证连接状态
        return conn;
    }

    // 关闭资源
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
                System.out.println("数据库连接已关闭"); // 用于验证
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}