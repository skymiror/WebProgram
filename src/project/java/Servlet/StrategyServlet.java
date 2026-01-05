package Servlet;

import Entity.Place;
import Entity.Route;
import Entity.Strategy;
import Service.PlaceService;
import Service.RouteService;
import Service.StrategyService;
import Service.impl.PlaceServiceImpl;
import Service.impl.RouteServiceImpl;
import Service.impl.StrategyServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/StrategyList")
public class StrategyServlet extends HttpServlet {
    private final StrategyService strategyService = new StrategyServiceImpl();
    private final RouteService routeService = new RouteServiceImpl();
    private final PlaceService placeService = new PlaceServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 加载所有攻略
            List<Strategy> strategies = strategyService.selectAllWithCoverImage();

            // 2. 封装返回结果（包含路线和地点）
            List<Map<String, Object>> strategyList = new ArrayList<>();
            for (Strategy strategy : strategies) {
                Map<String, Object> strategyMap = new HashMap<>();
                String tipId = strategy.getTipId();

                // 2.1 基础攻略信息
                strategyMap.put("tipid", tipId);
                strategyMap.put("title", strategy.getTitle());
                strategyMap.put("content", strategy.getContent());
                strategyMap.put("coverImagePath", strategy.getCoverImagePath());

                // 2.2 加载关联的路线信息
                Route route = routeService.getRouteByTipId(tipId);
                strategyMap.put("route", route != null ? mapRoute(route) : null);

                // 2.3 加载关联的地点信息
                List<Place> places = placeService.findByTipId(tipId);
                strategyMap.put("places", mapPlaces(places));

                strategyList.add(strategyMap);
            }

            result.put("success", true);
            result.put("strategyList", strategyList);
            result.put("message", "攻略列表（含路线和地点）加载成功");

        } catch (SQLException e) {
            result.put("success", false);
            result.put("message", "数据库查询失败：" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "服务器错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            out.write(objectMapper.writeValueAsString(result));
            out.close();
        }
    }

    // 辅助方法：将Route对象转换为Map（避免直接序列化实体类可能带来的问题）
    private Map<String, Object> mapRoute(Route route) {
        Map<String, Object> routeMap = new HashMap<>();
        routeMap.put("routeId", route.getRouteId());
        routeMap.put("title", route.getR_title());
        routeMap.put("day", route.getR_day());
        return routeMap;
    }

    // 辅助方法：将Place列表转换为Map列表
    private List<Map<String, Object>> mapPlaces(List<Place> places) {
        List<Map<String, Object>> placeMaps = new ArrayList<>();
        for (Place place : places) {
            Map<String, Object> placeMap = new HashMap<>();
            placeMap.put("placeId", place.getPlaceId());
            placeMap.put("placeName", place.getPlaceName());
            placeMap.put("openTime", place.getOpenTime());
            placeMap.put("placeIntro", place.getPlaceIntro());
            placeMaps.add(placeMap);
        }
        return placeMaps;
    }
}