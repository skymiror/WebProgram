package Servlet;

import Entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import DAO.impl.UserDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/GetCurrentUser")
public class GetCurrentUserServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDAOImpl userDAOImpl = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String account = req.getParameter("account");
        Map<String, Object> response = new HashMap<>();

        try {
            if (account == null || account.trim().isEmpty()) {
                throw new SQLException("账号不能为空");
            }

            User loginUser = userDAOImpl.findByAccount(account.trim());

            if (loginUser != null) {
                response.put("code", 200);
                response.put("success", true);
                response.put("data", loginUser);
                System.out.println("获取用户信息成功：" + loginUser.getUsername() + "，账号：" + account);
            } else {
                response.put("code", 401);
                response.put("message", "用户不存在");
            }
        } catch (SQLException e) {
            response.put("code", 401);
            response.put("message", e.getMessage());
            System.out.println("获取用户信息失败：" + e.getMessage());
        }

        objectMapper.writeValue(resp.getWriter(), response);
    }
}