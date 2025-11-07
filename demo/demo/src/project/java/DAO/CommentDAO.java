package DAO;

import Entity.Comment;

public interface CommentDAO {
    // 发布评论（commentId 自增，无需手动设置）
    boolean publishComment(Comment comment);
    // 删除评论（根据 commentId）
    boolean deleteComment(Integer commentId);
}
