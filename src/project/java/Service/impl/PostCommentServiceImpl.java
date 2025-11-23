package Service.impl;

import DAO.CommentDAO;
import DAO.PostCommentDAO;
import DAO.impl.CommentDAOImpl;
import DAO.impl.PostCommentDAOImpl;
import Entity.PostComment;
import Service.PostCommentService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostCommentServiceImpl implements PostCommentService {
    // 依赖关联表DAO
    private final PostCommentDAO postCommentDAO = new PostCommentDAOImpl();
    // 依赖CommentDAO（用于校验评论是否存在）
    private final CommentDAO commentDAO = new CommentDAOImpl();

    @Override
    public boolean addPostComment(PostComment postComment) {
        // 1. 参数非空校验
        if (postComment == null) {
            System.err.println("关联失败：PostComment实体不能为空");
            return false;
        }
        // 2. 核心字段校验（用户ID、攻略ID、评论ID均不可空）
        if (postComment.getCommentUserId() == null || postComment.getCommentUserId().trim().isEmpty()
                || postComment.getTipId() == null || postComment.getTipId().trim().isEmpty()
                || postComment.getCommentId() == null || postComment.getCommentId().trim().isEmpty()) {
            System.err.println("关联失败：用户ID、攻略ID、评论ID均不能为空");
            return false;
        }
        // 3. 校验评论是否真实存在（避免关联无效评论）
        try {
            if (commentDAO.selectByCommentId(Integer.parseInt(postComment.getCommentId())) == null) {
                System.err.println("关联失败：评论ID不存在");
                return false;
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        // 4. 调用DAO新增关联
        return postCommentDAO.addComment(postComment);
    }

    @Override
    public boolean deletePostComment(String commentId) {
        // 1. 参数校验
        if (commentId == null || commentId.trim().isEmpty()) {
            System.err.println("解除关联失败：评论ID不能为空");
            return false;
        }
        // 2. 调用DAO删除关联（根据评论ID批量删除，避免遗漏）
        return postCommentDAO.deleteComment(commentId);
    }

    @Override
    public String[] getCommentIdsByTipId(String tipId) {
        // 1. 参数校验
        if (tipId == null || tipId.trim().isEmpty()) {
            System.err.println("查询失败：攻略ID不能为空");
            return new String[0];
        }
        // 2. 调用DAO查询该攻略下的所有评论ID（需给DAO新增方法）
        try {
            List<String> commentIdList = ((PostCommentDAOImpl) postCommentDAO).selectCommentIdsByTipId(tipId.trim());
            return commentIdList.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    @Override
    public PostComment[] getPostCommentsByUserId(String userId) {
        // 1. 参数校验
        if (userId == null || userId.trim().isEmpty()) {
            System.err.println("查询失败：用户ID不能为空");
            return new PostComment[0];
        }
        // 2. 调用DAO查询该用户的所有关联记录（需给DAO新增方法）
        try {
            List<PostComment> postCommentList = ((PostCommentDAOImpl) postCommentDAO).selectByUserId(userId.trim());
            return postCommentList.toArray(new PostComment[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new PostComment[0];
        }
    }
}