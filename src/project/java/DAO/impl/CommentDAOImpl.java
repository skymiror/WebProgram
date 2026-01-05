package DAO.impl;

import DAO.CommentDAO;
import Entity.Comment;
import Util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentDAOImpl implements CommentDAO {

    @Override
    public boolean publishComment(Comment comment) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DBUtil.getConnection();
            // 注意：这里的表是comment，字段要和comment表一致（不是视图）
            String sql = "INSERT INTO comment (text, parentId, time) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, comment.getContent());
            statement.setInt(2, comment.getParentId());
            statement.setTimestamp(3, new Timestamp(comment.getTime().getTime()));

            int rows = statement.executeUpdate();
            if (rows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    comment.setCommentId(generatedKeys.getInt(1));
                }
            }
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(connection, statement, generatedKeys);
        }
    }

    @Override
    public boolean deleteComment(Integer commentId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

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
            DBUtil.close(connection, statement, resultSet);
        }
    }

    @Override
    public Comment selectByCommentId(Integer commentId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 修正：comment表的内容字段是text，不是content
            String sql = "SELECT commentId, text, parentId, time FROM comment WHERE commentId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, commentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("commentId"));
                comment.setContent(rs.getString("text")); // 这里要和表字段一致（text）
                comment.setParentId(rs.getInt("parentId"));
                comment.setTime(rs.getTimestamp("time"));
                return comment;
            }
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Comment> selectByParentId(Integer parentId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Comment> commentList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            // 修正：comment表的内容字段是text，不是content
            String sql = "SELECT commentId, text, parentId, time FROM comment WHERE parentId = ? ORDER BY time ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, parentId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("commentId"));
                comment.setContent(rs.getString("text")); // 这里要和表字段一致（text）
                comment.setParentId(rs.getInt("parentId"));
                comment.setTime(rs.getTimestamp("time"));
                commentList.add(comment);
            }
            return commentList;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Map<String, Object>> selectCommentsByTipIdFromView(String C_TipId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> commentList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            // 核心修正：SQL字段必须和comment_detail视图完全一致
            String sql = "SELECT commentid, comment_content, parentid, comment_time, username, C_TipId FROM comment_detail WHERE C_TipId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, C_TipId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("commentId", rs.getInt("commentid"));       // 对应视图的commentid
                commentMap.put("content", rs.getString("comment_content")); // 对应视图的comment_content
                commentMap.put("parentId", rs.getInt("parentid"));         // 对应视图的parentid
                commentMap.put("time", rs.getTimestamp("comment_time"));   // 对应视图的comment_time
                commentMap.put("username", rs.getString("username"));       // 对应视图的username
                commentMap.put("C_TipId", rs.getString("C_TipId"));         // 对应视图的C_TipId
                // 注意：视图中没有C_UserId字段，需要从关联表补充（如果需要）
                commentMap.put("replies", new ArrayList<>());              // 初始化子回复
                commentList.add(commentMap);
            }
            return commentList;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}