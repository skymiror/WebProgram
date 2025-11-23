package Servlet;

import Service.UserService;
import Service.impl.UserServiceImpl;
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

@WebServlet("/UpdateIntro")
public class UpdateIntroServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, String> updateData = objectMapper.readValue(req.getInputStream(), Map.class);
            String account = updateData.get("account");
            String newIntro = updateData.get("newIntro");

            if (account == null || account.trim().isEmpty()) {
                throw new SQLException("账号不能为空，修改失败");
            }

            userService.updateIntro(account.trim(), newIntro);

            response.put("code", 200);
            response.put("success", true);
            response.put("message", "简介修改成功");

        } catch (SQLException e) {
            response.put("code", 400);
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "服务器异常：" + e.getMessage());
            e.printStackTrace();
        }

        objectMapper.writeValue(resp.getWriter(), response);
    }

}