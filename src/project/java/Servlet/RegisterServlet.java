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

@WebServlet("/Register")
public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String json = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        User user = objectMapper.readValue(json, User.class);

        Map<String, Object> response = new HashMap<>(3);
        try {
            userService.register(user);
            response.put("success", true);
            response.put("code", 200);
            response.put("data", user.getAccount());
            response.put("message", "注册成功");
        } catch (SQLException e) {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "服务器错误：" + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", e.getMessage());
            System.out.println("注册异常：" + e.getMessage());
        }

        objectMapper.writeValue(resp.getWriter(), response);
    }
}