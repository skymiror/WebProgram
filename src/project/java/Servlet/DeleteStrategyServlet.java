package Servlet;

import DAO.PlaceDAO;
import DAO.RouteDAO;
import DAO.StrategyDAO;
import DAO.impl.PlaceDAOImpl;
import DAO.impl.RouteDAOImpl;
import DAO.impl.StrategyDAOImpl;
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

@WebServlet("/DeleteStrategy")
public class DeleteStrategyServlet extends HttpServlet {
    // 初始化DAO和Jackson对象（保持与其他Servlet风格一致）
    private final StrategyDAO strategyDAO = new StrategyDAOImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PlaceDAO placeDAO=new PlaceDAOImpl();
    private final RouteDAO routeDAO = new RouteDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 设置编码和响应格式
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 2. 定义统一响应结果
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // 3. 解析前端传递的JSON参数（与前端删除请求格式匹配）
            Map<String, String> deleteData = objectMapper.readValue(
                    request.getInputStream(),
                    Map.class
            );
            String strategyId = deleteData.get("strategyId"); // 前端传递的参数名

            // 4. 参数校验
            if (strategyId == null || strategyId.trim().isEmpty()) {
                throw new SQLException("攻略ID不能为空");
            }

            // 5. 验证攻略是否存在（避免删除不存在的记录）
            if (strategyDAO.selectByPostId(strategyId.trim()) == null) {
                throw new SQLException("攻略不存在或已被删除");
            }

            // 6. 执行删除操作

            placeDAO.deleteByTipId(strategyId.trim());
            routeDAO.deleteRoute(strategyId.trim());
            int rowsAffected = strategyDAO.deleteByPostId(strategyId.trim());

            if (rowsAffected > 0) {
                responseMap.put("code", 200);
                responseMap.put("success", true);
                responseMap.put("message", "攻略删除成功");
            } else {
                throw new SQLException("删除失败，未知错误");
            }

        } catch (SQLException e) {
            // 7. 业务异常处理（参数错误、攻略不存在等）
            responseMap.put("code", 400);
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        } catch (Exception e) {
            // 8. 系统异常处理（JSON解析失败、数据库异常等）
            responseMap.put("code", 500);
            responseMap.put("success", false);
            responseMap.put("message", "服务器异常：" + e.getMessage());
            e.printStackTrace(); // 控制台打印异常，便于调试
        }

        // 9. 返回JSON响应
        objectMapper.writeValue(response.getWriter(), responseMap);
    }

    /**
     * 处理跨域预检请求（OPTIONS）
     * 注：CorsFilter已统一配置，此处可保留空实现，避免重复处理
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doOptions(request, response);
    }

    /**
     * 禁止GET请求（避免误操作）
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("code", 405);
        responseMap.put("success", false);
        responseMap.put("message", "不支持GET请求，请使用POST");
        objectMapper.writeValue(response.getWriter(), responseMap);
    }
}