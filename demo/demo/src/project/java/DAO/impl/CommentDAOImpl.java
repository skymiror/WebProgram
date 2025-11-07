package DAO.impl;

import DAO.CommentDAO;
import Entity.Comment;
import Util.DBUtil;

import java.sql.*;

public class CommentDAOImpl implements CommentDAO {

    @Override
    public boolean publishComment(Comment comment) {
        Connection connection = null;
        PreparedStatement statement = null;
        // 发布操作无需 ResultSet，设为 null
        ResultSet resultSet = null;

        try {
            // 1. 获取连接
            connection = DBUtil.getConnection();
            // 2. 编写 SQL（commentId 自增，不插入）
            String sql = "INSERT INTO comment (content, parentId, time) VALUES (?, ?, ?)";
            // 3. 创建 PreparedStatement
            statement = connection.prepareStatement(sql);
            // 4. 设置参数
            statement.setString(1, comment.getContent());
            statement.setInt(2, comment.getParentId());
            statement.setTimestamp(3, new Timestamp(comment.getTime().getTime()));
            // 5. 执行插入，返回影响行数
            int rows = statement.executeUpdate();
            return rows > 0; // 行数 > 0 表示成功

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 6. 调用工具类的 close 方法，按顺序关闭（ResultSet 传 null）
            DBUtil.close(connection, statement, resultSet);
        }
    }

    @Override
    public boolean deleteComment(Integer commentId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null; // 删除操作也无需 ResultSet

        try {
            connection = DBUtil.getConnection();
            String sql = "DELETE FROM comment WHERE commentId = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, commentId);
            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 同样按顺序关闭，ResultSet 传 null
            DBUtil.close(connection, statement, resultSet);
        }
    }
}