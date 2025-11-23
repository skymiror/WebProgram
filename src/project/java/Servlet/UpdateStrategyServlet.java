package Servlet;

import DAO.StrategyDAO;
import DAO.impl.StrategyDAOImpl;
import Entity.Strategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/UpdateStrategy")
public class UpdateStrategyServlet extends HttpServlet {
    private final StrategyDAO strategyDAO = new StrategyDAOImpl();
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用于JSON解析和生成

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置编码，避免中文乱码
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        // 定义统一响应格式
        Map<String, Object> response = new HashMap<>();

        try {
            // 从请求体中读取JSON数据（与前端发送的JSON结构对应）
            Map<String, String> updateData = objectMapper.readValue(req.getInputStream(), Map.class);

            // 提取参数（与前端JSON的key保持一致）
            String strategyId = updateData.get("strategyId");
            String newTitle = updateData.get("newTitle");
            String newContent = updateData.get("newContent");

            // 参数校验
            if (strategyId == null || strategyId.trim().isEmpty()) {
                throw new SQLException("攻略ID不能为空");
            }
            if (newTitle == null || newTitle.trim().isEmpty()) {
                throw new SQLException("标题不能为空");
            }
            if (newContent == null || newContent.trim().isEmpty()) {
                throw new SQLException("内容不能为空");
            }

            // 验证攻略是否存在
            Strategy existingStrategy = strategyDAO.selectByPostId(strategyId.trim());
            if (existingStrategy == null) {
                throw new SQLException("攻略不存在，请检查ID");
            }

            // 调用DAO更新攻略（标题和内容）
            int rowsAffected = strategyDAO.updateStrategy(
                    strategyId.trim(),
                    newTitle.trim(),
                    newContent.trim()
            );

            if (rowsAffected > 0) {
                response.put("code", 200);
                response.put("success", true);
                response.put("message", "攻略修改成功");
            } else {
                throw new SQLException("更新失败，未修改任何内容");
            }

        } catch (SQLException e) {
            // 业务异常（如参数错误、攻略不存在）
            response.put("code", 400);
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            // 其他异常（如JSON解析失败、服务器错误）
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "服务器异常：" + e.getMessage());
            e.printStackTrace(); // 控制台打印详细异常，便于调试
        }

        // 将响应数据转换为JSON并返回
        objectMapper.writeValue(resp.getWriter(), response);
    }

    // 处理GET请求（避免前端误发GET导致的错误）
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> response = new HashMap<>();
        response.put("code", 405);
        response.put("success", false);
        response.put("message", "不支持GET请求，请使用POST");
        objectMapper.writeValue(resp.getWriter(), response);
    }
}