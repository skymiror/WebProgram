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
 * 登录Servlet（对应前端登录页面）
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理用户登录
     * HTTP方法：POST
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json;charset=UTF-8");

        // 读取并打印前端传递的JSON数据
        String json = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // 解析JSON为User对象
        User loginParam = objectMapper.readValue(json, User.class);
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();

        Map<String, Object> response = new HashMap<>(3);
        try {
            // 调用Service层处理登录逻辑
            User loginUser = userService.login(account, password);

            response.put("success", true);
            response.put("code", 200);
            response.put("data", loginUser); // 返回登录成功的用户信息
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