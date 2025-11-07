package DAO.impl;
import DAO.PostCommentDAO;
import Entity.PostComment;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostCommentDAOImpl implements PostCommentDAO {

    /**
     * 发表评论：向postcomment表插入评论记录
     */
    @Override
    public boolean addComment(PostComment postComment) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();

            // 2. 编写插入SQL（表字段：userid、tip-id、comment-id）
            String sql = "INSERT INTO postcomment (userid, tip_id, comment_id) " +
                    "VALUES (?, ?, ?)";

            // 3. 创建PreparedStatement
            pstmt = conn.prepareStatement(sql);

            // 4. 设置参数
            pstmt.setString(1, postComment.getCommentUserId());
            pstmt.setString(2, postComment.getTipId());
            pstmt.setString(3, postComment.getCommentId());

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
     * 删除评论：根据comment-id删除
     */
    @Override
    public boolean deleteComment(String commentId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();

            // 2. 编写删除SQL
            String sql = "DELETE FROM postcomment WHERE comment_id = ?";

            // 3. 创建PreparedStatement
            pstmt = conn.prepareStatement(sql);

            // 4. 设置参数
            pstmt.setString(1, commentId);

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