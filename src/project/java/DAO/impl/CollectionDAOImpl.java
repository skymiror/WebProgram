package DAO.impl;

import DAO.CollectionDAO;
import Entity.Collection;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CollectionDAOImpl implements CollectionDAO {

    /**
     * 添加收藏：向collection表插入一条收藏记录
     */
    @Override
    public boolean addCollection(String userId, String strategyId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();

            // 2. 编写插入SQL
            String sql = "INSERT INTO collection (c_userId, c_strategyId, c_time) " +
                    "VALUES (?, ?, ?)";

            // 3. 创建PreparedStatement
            pstmt = conn.prepareStatement(sql);

            // 4. 设置参数
            pstmt.setString(1, userId);
            pstmt.setString(2, strategyId);
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));

            // 5. 执行插入，返回受影响行数
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 6. 关闭资源
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 删除收藏：根据用户ID和攻略ID联合删除（保证唯一性）
     */
    @Override
    public boolean deleteCollection(String userId, String strategyId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();

            // 2. 编写删除SQL
            String sql = "DELETE FROM collection WHERE c_userId = ? AND c_strategyId = ?";

            // 3. 创建PreparedStatement
            pstmt = conn.prepareStatement(sql);

            // 4. 设置参数
            pstmt.setString(1, userId);
            pstmt.setString(2, strategyId);

            // 5. 执行删除，返回受影响行数
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 6. 关闭资源
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean checkCollectionExists(String userId, String strategyId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();

            // 2. 编写查询SQL：检查是否存在匹配的收藏记录
            String sql = "SELECT 1 FROM collection WHERE c_userId = ? AND c_strategyId = ? LIMIT 1";

            // 3. 创建PreparedStatement
            pstmt = conn.prepareStatement(sql);

            // 4. 设置参数
            pstmt.setString(1, userId);
            pstmt.setString(2, strategyId);

            // 5. 执行查询
            rs = pstmt.executeQuery();

            // 6. 判断是否存在记录（有结果则返回true）
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 7. 关闭资源（包含ResultSet）
            DBUtil.close(conn, pstmt, rs);
        }
    }


}
