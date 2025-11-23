package Service;

import Entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 * 包含：发布评论、回复评论（指定parentId）、删除评论、查询评论列表
 */
public interface CommentService {
    /**
     * 发布新评论（parentId=0 表示一级评论，非0表示回复）
     * @param comment 评论实体（需包含 content、parentId、time）
     * @return 发布成功返回true，失败返回false
     */
    boolean publishComment(Comment comment);

    /**
     * 删除评论（根据commentId）
     * @param commentId 评论ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteComment(Integer commentId);

    /**
     * 根据父级ID查询子评论（用于加载回复列表）
     * @param parentId 父评论ID（一级评论parentId=0）
     * @return 子评论列表（按时间升序）
     */
    List<Comment> getCommentsByParentId(Integer parentId);
}