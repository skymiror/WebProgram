package DAO.impl;
import DAO.PublishDAO;
import Entity.Publish;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PublishDAOImpl implements PublishDAO {

    /**
     * 发布攻略：插入新记录
     */
    @Override
    public boolean addPublish(Publish publish) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 1. 获取连接
            conn = DBUtil.getConnection();

            // 2. 编写SQL（注意表名和字段与数据库一致，这里假设表是posttips）
            String sql = "INSERT INTO posttips (user-id, place-id, tips-id, time, routeid) " +
                    "VALUES (?, ?, ?, ?, ?)";

            // 3. 创建PreparedStatement
            pstmt = conn.prepareStatement(sql);

            // 4. 设置参数
            pstmt.setString(1, publish.getP_userid());
            pstmt.setString(2, publish.getP_placeId());
            pstmt.setString(3, publish.getP_tipsId());
            pstmt.setTimestamp(4, new java.sql.Timestamp(publish.getTime().getTime()));
            pstmt.setString(5, publish.getP_routeId());

            // 5. 执行插入，返回受影响行数
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 6. 关闭资源（调用DBUtil的close方法，注意参数顺序）
            DBUtil.close(conn, pstmt, null); // ResultSet为null，因为插入操作无结果集
        }
    }

    /**
     * 删除攻略：根据tips-id删除
     */
    @Override
    public boolean deletePublishByTipsId(String tipsId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 1. 获取连接
            conn = DBUtil.getConnection();
            // 2. 编写删除SQL
            String sql = "DELETE FROM posttips WHERE tips-id = ?";
            // 3. 创建PreparedStatement
            pstmt = conn.prepareStatement(sql);
            // 4. 设置参数
            pstmt.setString(1, tipsId);
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
