package Servlet;

import DAO.impl.PlaceDAOImpl;
import DAO.impl.RouteDAOImpl;
import DAO.impl.StrategyDAOImpl;
import DAO.impl.PhotoDAOImpl;
import Entity.Strategy;
import Entity.Place;
import Entity.Route;
import Entity.Photo;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/StrategyDetail")
public class StrategyDetailServlet extends HttpServlet {
    private final StrategyDAOImpl strategyDAO = new StrategyDAOImpl();
    private final PlaceDAOImpl placeDAO = new PlaceDAOImpl();
    private final RouteDAOImpl routeDAO = new RouteDAOImpl();
    private final PhotoDAOImpl photoDAO = new PhotoDAOImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 获取攻略ID参数
            String tipId = req.getParameter("tipId");
            if (tipId == null || tipId.trim().isEmpty()) {
                throw new SQLException("攻略ID不能为空");
            }

            // 2. 查询攻略基本信息
            Strategy strategy = strategyDAO.selectByPostId(tipId);
            if (strategy == null) {
                throw new SQLException("未找到ID为" + tipId + "的攻略");
            }

            // 3. 查询关联的地点信息
            List<Place> places = placeDAO.findByTipId(tipId);

            // 4. 查询关联的路线信息
            Route routes = routeDAO.findByTipId(tipId);

            // 5. 查询关联的图片信息
            List<Photo> photos = photoDAO.selectByTipId(tipId);

            // 6. 封装返回结果
            result.put("success", true);
            result.put("strategy", strategy);
            result.put("places", places);
            result.put("routes", routes);
            result.put("photos", photos);
            result.put("message", "攻略详情查询成功");

        } catch (SQLException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
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
}