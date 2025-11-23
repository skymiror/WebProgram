package DAO.impl;

import DAO.UserDAO;
import Entity.User;
import Util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {

    @Override
    public void insert(User user) throws SQLException {
        // 直接写原始字段名，与数据库表严格对应
        String sql = "INSERT INTO user (username, account, password, introduce, registerTime, phone) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 参数顺序与SQL字段顺序完全一致
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getAccount());
            pstmt.setString(3, user.getPassword()); // 明文存储密码（按需求保留）
            // 处理简介空值：未填写时设为空字符串
            pstmt.setString(4, user.getIntroduce() == null ? "" : user.getIntroduce());
            // 后端自动填充注册时间（避免空指针）
            pstmt.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setString(6, user.getPhone());

            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                System.out.println("警告：数据未插入！可能是字段不匹配或值不符合约束（如账号长度6位、手机号11位）");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("插入用户失败：" + e.getMessage(), e);
        }
    }

    @Override
    public User findByAccount(String account) throws SQLException {
        // 核心修复：phone字段后添加空格（避免语法错误）
        String sql = "SELECT username, account, password, introduce, registerTime, phone " +
                "FROM user WHERE account = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    // ResultSet取值时字段名与数据库一致
                    user.setUsername(rs.getString("username"));
                    user.setAccount(rs.getString("account"));
                    user.setPassword(rs.getString("password"));
                    user.setIntroduce(rs.getString("introduce"));
                    user.setRegisterTime(rs.getTimestamp("registerTime"));
                    user.setPhone(rs.getString("phone"));
                    System.out.println("查询成功，找到用户：" + user.getUsername());
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("查询用户失败：" + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void updateIntro(String account, String newIntroduce) throws SQLException {
        String sql = "UPDATE user SET introduce = ? WHERE account = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newIntroduce == null ? "" : newIntroduce);
            pstmt.setString(2, account);

            int rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("更新简介失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void updatePassword(String account, String newPassword) throws SQLException {
        String sql = "UPDATE user SET password = ? WHERE account = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword); // 明文存储新密码
            pstmt.setString(2, account);

            int rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("更新密码失败：" + e.getMessage(), e);
        }
    }
}