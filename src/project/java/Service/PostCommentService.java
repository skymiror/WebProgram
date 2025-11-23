package Service;

import Entity.PostComment;

public interface PostCommentService {
    /**
     * 发布评论时调用，关联用户-攻略-评论
     */
    boolean addPostComment(PostComment postComment);

    /**
     * 删除评论时调用
     */
    boolean deletePostComment(String commentId);

    /**
     * 根据攻略ID查询该攻略的所有评论ID（用于加载攻略下的评论列表）
     */
    String[] getCommentIdsByTipId(String tipId);

    /**
     * 根据用户ID查询该用户发表的所有评论关联（用于个人中心我的评论）
     */
    PostComment[] getPostCommentsByUserId(String userId);
}