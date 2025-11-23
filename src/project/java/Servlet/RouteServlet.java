package Servlet;

import Entity.Route;
import Service.RouteService;
import Service.impl.RouteServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/RouteServlet")
public class RouteServlet extends HttpServlet {
    private final RouteService routeService = new RouteServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> responseMap = new HashMap<>();

        try {
            String action = request.getParameter("action");
            if (action == null || action.trim().isEmpty()) {
                throw new IllegalArgumentException("操作类型不能为空（支持：add/delete/updateTitle/updateDay/search/getAll）");
            }

            switch (action.trim()) {
                case "add":
                    handleAddRoute(request, responseMap);
                    break;
                case "delete":
                    handleDeleteRoute(request, responseMap);
                    break;
                case "updateTitle":
                    handleUpdateTitle(request, responseMap);
                    break;
                case "updateDay":
                    handleUpdateDay(request, responseMap);
                    break;
                case "search":
                    handleSearchRoute(request, responseMap);
                    break;
                case "getAll":
                    handleGetAllRoutes(request, responseMap);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的操作：" + action);
            }
        } catch (IllegalArgumentException e) {
            responseMap.put("code", 400);
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        } catch (SQLException e) {
            responseMap.put("code", 500);
            responseMap.put("success", false);
            responseMap.put("message", "数据库异常：" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            responseMap.put("code", 500);
            responseMap.put("success", false);
            responseMap.put("message", "服务器异常：" + e.getMessage());
            e.printStackTrace();
        }

        objectMapper.writeValue(response.getWriter(), responseMap);
    }

    // 新增路线（不变）
    private void handleAddRoute(HttpServletRequest request, Map<String, Object> responseMap) {
        try {
            Route route = objectMapper.readValue(request.getInputStream(), Route.class);
            boolean success = routeService.addRoute(route);
            responseMap.put("code", 200);
            responseMap.put("success", success);
            responseMap.put("message", success ? "路线新增成功" : "路线新增失败");
            if (success) {
                responseMap.put("routeId", route.getRouteId());
            }
        } catch (IOException e) {
            throw new RuntimeException("参数解析失败：" + e.getMessage());
        }
    }

    // 删除路线（不变，通过 DAO 结果判断）
    private void handleDeleteRoute(HttpServletRequest request, Map<String, Object> responseMap) {
        String routeId = request.getParameter("routeId");
        boolean success = routeService.deleteRoute(routeId);
        responseMap.put("code", 200);
        responseMap.put("success", success);
        responseMap.put("message", success ? "路线删除成功" : "路线删除失败（路线不存在或异常）");
    }

    // 修改标题（不变）
    private void handleUpdateTitle(HttpServletRequest request, Map<String, Object> responseMap) {
        try {
            Map<String, String> updateData = objectMapper.readValue(request.getInputStream(), Map.class);
            String routeId = updateData.get("routeId");
            String newTitle = updateData.get("newTitle");
            boolean success = routeService.updateRouteTitle(routeId, newTitle);
            responseMap.put("code", 200);
            responseMap.put("success", success);
            responseMap.put("message", success ? "标题修改成功" : "标题修改失败（路线不存在或未修改）");
        } catch (IOException e) {
            throw new RuntimeException("参数解析失败：" + e.getMessage());
        }
    }

    // 修改天数（不变）
    private void handleUpdateDay(HttpServletRequest request, Map<String, Object> responseMap) {
        try {
            Map<String, Object> updateData = objectMapper.readValue(request.getInputStream(), Map.class);
            String routeId = (String) updateData.get("routeId");
            Integer newDay = (Integer) updateData.get("newDay");
            boolean success = routeService.updateRouteDay(routeId, newDay);
            responseMap.put("code", 200);
            responseMap.put("success", success);
            responseMap.put("message", success ? "天数修改成功" : "天数修改失败（路线不存在或未修改）");
        } catch (IOException e) {
            throw new RuntimeException("参数解析失败：" + e.getMessage());
        }
    }

    // 关键字搜索（不变）
    private void handleSearchRoute(HttpServletRequest request, Map<String, Object> responseMap) throws SQLException {
        String keyword = request.getParameter("keyword");
        List<Route> routes = routeService.searchRouteByKeyword(keyword);
        responseMap.put("code", 200);
        responseMap.put("success", true);
        responseMap.put("message", "搜索成功");
        responseMap.put("routeList", routes);
    }

    // 查询所有路线（不变）
    private void handleGetAllRoutes(HttpServletRequest request, Map<String, Object> responseMap) throws SQLException {
        List<Route> allRoutes = routeService.getAllRoutes();
        responseMap.put("code", 200);
        responseMap.put("success", true);
        responseMap.put("message", "查询成功");
        responseMap.put("routeList", allRoutes);
    }

    // 支持 GET 请求（不变）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    // 跨域预检（不变）
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doOptions(request, response);
    }
}