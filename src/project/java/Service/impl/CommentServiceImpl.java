package Service.impl;

import DAO.CommentDAO;
import DAO.impl.CommentDAOImpl;
import Entity.Comment;
import Service.CommentService;
import java.sql.SQLException;
import java.util.List;

/**
 * 评论服务实现类
 * 负责业务逻辑校验（如内容非空、父级评论存在性）
 */
public class CommentServiceImpl implements CommentService {
    // 依赖DAO层实现
    private final CommentDAO commentDAO = new CommentDAOImpl();

    @Override
    public boolean publishComment(Comment comment) {
        // 1. 业务参数校验
        if (comment == null) {
            System.err.println("发布失败：评论实体不能为空");
            return false;
        }
        // 2. 校验评论内容（非空且长度不超过500字，对应数据库varchar(500)）
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()
                || comment.getContent().trim().length() > 500) {
            System.err.println("发布失败：评论内容不能为空且长度不超过500字");
            return false;
        }
        // 3. 校验父级评论（若parentId≠0，需确认父评论存在）
        Integer parentId = comment.getParentId();
        if (parentId != null && parentId > 0) {
            try {
                // 调用DAO查询父评论是否存在（需在CommentDAO新增selectByParentId方法）
                Comment parentComment = ((CommentDAOImpl) commentDAO).selectByCommentId(parentId);
                if (parentComment == null) {
                    System.err.println("发布失败：父级评论不存在");
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        // 4. 调用DAO执行发布
        return commentDAO.publishComment(comment);
    }

    @Override
    public boolean deleteComment(Integer commentId) {
        // 业务校验：评论ID非空
        if (commentId == null || commentId <= 0) {
            System.err.println("删除失败：评论ID无效");
            return false;
        }
        // 调用DAO执行删除
        return commentDAO.deleteComment(commentId);
    }

    @Override
    public List<Comment> getCommentsByParentId(Integer parentId) {
        // 业务校验：父级ID默认为0（查询一级评论）
        if (parentId == null) {
            parentId = 0;
        }
        // 调用DAO查询子评论（需在CommentDAO新增该方法）
        try {
            return ((CommentDAOImpl) commentDAO).selectByParentId(parentId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}