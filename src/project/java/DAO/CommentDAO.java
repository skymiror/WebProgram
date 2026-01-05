package DAO;

import Entity.Comment;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CommentDAO {
    // 发布评论（commentId 自增，无需手动设置）
    boolean publishComment(Comment comment);
    // 删除评论（根据 commentId）
    boolean deleteComment(Integer commentId);

    // 新增方法
    /**
     * 根据commentId查询评论（用于校验父评论存在性）
     */
    Comment selectByCommentId(Integer commentId) throws SQLException;

    /**
     * 根据parentId查询子评论列表（按时间升序）
     */
    List<Comment> selectByParentId(Integer parentId) throws SQLException;

    List<Map<String, Object>> selectCommentsByTipIdFromView(String C_TipId) throws SQLException;
}
