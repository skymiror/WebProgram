package DAO.impl;

import DAO.CollectionDAO;
import Entity.Collection;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CollectionDAOImpl implements CollectionDAO {

    /**
     * 添加收藏：向collection表插入一条收藏记录
     */
    @Override
    public boolean addCollection(Collection collection) {
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
            pstmt.setString(1, collection.getC_userId());
            pstmt.setString(2, collection.getC_strategyId());
            pstmt.setTimestamp(3, new java.sql.Timestamp(collection.getCollectTime().getTime()));

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
}
