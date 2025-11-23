package Servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        jakarta.servlet.http.HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Map<String, Object> response = new HashMap<>(3);
        response.put("success", true);
        response.put("code", 200);
        response.put("message", "退出登录成功");
        objectMapper.writeValue(resp.getWriter(), response);
    }
}