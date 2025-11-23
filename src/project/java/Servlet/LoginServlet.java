package Servlet;

import Entity.User;
import Service.UserService;
import Service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        // 接收前端数据
        Map<String, String> loginData = null;
        try {
            loginData = objectMapper.readValue(req.getInputStream(), Map.class);
        } catch (Exception e) {
            Map<String, Object> errorResp = new HashMap<>();
            errorResp.put("code", 400);
            errorResp.put("message", "请求格式错误");
            objectMapper.writeValue(resp.getWriter(), errorResp);
            return;
        }

        String account = loginData.get("account");
        String password = loginData.get("password");
        Map<String, Object> response = new HashMap<>();

        try {
            if (account == null || password == null || account.trim().isEmpty()) {
                throw new SQLException("账号密码不能为空");
            }

            // 登录成功，存入 Session
            User loginUser = userService.login(account.trim(), password.trim());
            HttpSession session = req.getSession(true); // 强制创建 Session
            session.setAttribute("loginUser", loginUser);
            session.setMaxInactiveInterval(3600); // Session 1小时有效

            // 手动设置 JSESSIONID Cookie，确保浏览器保存
            Cookie jsessionidCookie = new Cookie("JSESSIONID", session.getId());
            jsessionidCookie.setPath("/");
            jsessionidCookie.setHttpOnly(true);
            jsessionidCookie.setMaxAge(3600);
            resp.addCookie(jsessionidCookie);

            // 返回成功响应
            response.put("code", 200);
            response.put("success", true);
            response.put("data", loginUser);
            System.out.println("登录成功 - Session ID：" + session.getId());
        } catch (SQLException e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
        }

        objectMapper.writeValue(resp.getWriter(), response);
    }
}