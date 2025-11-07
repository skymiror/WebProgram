package DAO;

import Entity.PostComment;

public interface PostCommentDAO {
    // 发表评论：新增评论记录
    boolean addComment(PostComment postComment);

    // 删除评论：根据评论ID删除
    boolean deleteComment(String commentId);
}
