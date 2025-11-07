package Servlet;

import Entity.User;
import Service.UserService;
import Service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * RESTful风格的用户接口
 * 路径：/api/users（复数形式，符合REST资源命名规范）
 */
@WebServlet("/Register")
public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理用户注册（创建资源）
     * HTTP方法：POST（语义：创建资源）
     * 路径：/api/users（无需额外路径，通过POST方法区分“创建”操作）
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json;charset=UTF-8");

        // 关键：打印前端传的JSON数据（确认没传空）
        String json = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // 解析JSON为User对象
        User user = objectMapper.readValue(json, User.class);

        Map<String, Object> response = new HashMap<>(3);
        try {
            userService.register(user);
            response.put("success", true);
            response.put("code", 200);
            response.put("data", user.getAccount());
        } catch (SQLException e) {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "服务器错误：" + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", e.getMessage());
            System.out.println("异常信息：" + e.getMessage());
        }

        objectMapper.writeValue(resp.getWriter(), response);
    }
    /**
     * 处理跨域预检请求（OPTIONS方法）
     */
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}