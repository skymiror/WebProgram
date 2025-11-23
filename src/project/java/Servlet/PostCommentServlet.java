package Servlet;

import Entity.PostComment;
import Service.PostCommentService;
import Service.impl.PostCommentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/PostCommentServlet")
public class PostCommentServlet extends HttpServlet {
    private final PostCommentService postCommentService = new PostCommentServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> responseMap = new HashMap<>();

        try {
            String action = request.getParameter("action");
            if (action == null || action.trim().isEmpty()) {
                throw new IllegalArgumentException("操作类型不能为空（支持：add/delete/getByTipId/getByUserId）");
            }

            switch (action.trim()) {
                case "add": // 新增关联（发布评论时调用）
                    handleAddPostComment(request, responseMap);
                    break;
                case "delete": // 删除关联（删除评论时调用）
                    handleDeletePostComment(request, responseMap);
                    break;
                case "getByTipId": // 根据攻略ID查评论ID列表
                    handleGetByTipId(request, responseMap);
                    break;
                case "getByUserId": // 根据用户ID查关联记录
                    handleGetByUserId(request, responseMap);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的操作：" + action);
            }
        } catch (IllegalArgumentException e) {
            responseMap.put("code", 400);
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        } catch (Exception e) {
            responseMap.put("code", 500);
            responseMap.put("success", false);
            responseMap.put("message", "服务器异常：" + e.getMessage());
            e.printStackTrace();
        }

        objectMapper.writeValue(response.getWriter(), responseMap);
    }

    // 新增关联记录（配合发布评论）
    private void handleAddPostComment(HttpServletRequest request, Map<String, Object> responseMap) throws IOException {
        PostComment postComment = objectMapper.readValue(request.getInputStream(), PostComment.class);
        boolean success = postCommentService.addPostComment(postComment);
        responseMap.put("code", 200);
        responseMap.put("success", success);
        responseMap.put("message", success ? "关联成功" : "关联失败");
    }

    // 删除关联记录（配合删除评论）
    private void handleDeletePostComment(HttpServletRequest request, Map<String, Object> responseMap) {
        String commentId = request.getParameter("commentId");
        boolean success = postCommentService.deletePostComment(commentId);
        responseMap.put("code", 200);
        responseMap.put("success", success);
        responseMap.put("message", success ? "关联解除成功" : "关联解除失败");
    }

    // 根据攻略ID查该攻略的所有评论ID
    private void handleGetByTipId(HttpServletRequest request, Map<String, Object> responseMap) {
        String tipId = request.getParameter("tipId");
        String[] commentIds = postCommentService.getCommentIdsByTipId(tipId);
        responseMap.put("code", 200);
        responseMap.put("success", true);
        responseMap.put("commentIds", commentIds); // 返回评论ID数组，供前端查询具体评论内容
    }

    // 根据用户ID查该用户的所有评论关联（个人中心我的评论）
    private void handleGetByUserId(HttpServletRequest request, Map<String, Object> responseMap) {
        String userId = request.getParameter("userId");
        PostComment[] postComments = postCommentService.getPostCommentsByUserId(userId);
        responseMap.put("code", 200);
        responseMap.put("success", true);
        responseMap.put("postComments", postComments); // 返回关联记录，包含攻略ID和评论ID
    }

    // 支持GET请求查询（如加载攻略评论时）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}