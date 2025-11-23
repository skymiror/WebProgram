package Service.impl;

import DAO.RouteDAO;
import DAO.impl.RouteDAOImpl;
import Entity.Route;
import Service.RouteService;
import java.sql.SQLException;
import java.util.List;

public class RouteServiceImpl implements RouteService {
    private final RouteDAO routeDAO = new RouteDAOImpl();

    @Override
    public boolean addRoute(Route route) {
        // 原有校验逻辑不变
        if (route == null) {
            System.err.println("新增失败：路线信息不能为空");
            return false;
        }
        if (route.getR_title() == null || route.getR_title().trim().isEmpty()
                || route.getR_title().trim().length() > 100) {
            System.err.println("新增失败：路线标题不能为空且长度不超过100字");
            return false;
        }
        if (route.getR_day() == null || route.getR_day() <= 0) {
            System.err.println("新增失败：路线天数必须为正整数");
            return false;
        }
        return routeDAO.addRoute(route);
    }

    @Override
    public boolean deleteRoute(String routeId) {
        // 校验 routeId 格式，不直接查 ID，通过 DAO 删除结果判断是否存在
        if (routeId == null || routeId.trim().isEmpty() || routeId.trim().length() != 6) {
            System.err.println("删除失败：路线ID必须为6位字符");
            return false;
        }
        // 直接调用 DAO 删除，返回 false 即表示路线不存在或删除失败
        boolean success = routeDAO.deleteRoute(routeId.trim());
        if (!success) {
            System.err.println("删除失败：路线不存在或删除异常");
        }
        return success;
    }

    @Override
    public boolean updateRouteTitle(String routeId, String newTitle) {
        // 校验参数格式，通过 DAO 更新结果判断路线是否存在
        if (routeId == null || routeId.trim().isEmpty() || routeId.trim().length() != 6) {
            System.err.println("修改失败：路线ID必须为6位字符");
            return false;
        }
        if (newTitle == null || newTitle.trim().isEmpty() || newTitle.trim().length() > 100) {
            System.err.println("修改失败：新标题不能为空且长度不超过100字");
            return false;
        }
        boolean success = routeDAO.updateTitle(routeId.trim(), newTitle.trim());
        if (!success) {
            System.err.println("修改失败：路线不存在或未修改内容");
        }
        return success;
    }

    @Override
    public boolean updateRouteDay(String routeId, Integer newDay) {
        // 同理，通过更新结果判断路线存在性
        if (routeId == null || routeId.trim().isEmpty() || routeId.trim().length() != 6) {
            System.err.println("修改失败：路线ID必须为6位字符");
            return false;
        }
        if (newDay == null || newDay <= 0) {
            System.err.println("修改失败：天数必须为正整数");
            return false;
        }
        boolean success = routeDAO.updateDay(routeId.trim(), newDay);
        if (!success) {
            System.err.println("修改失败：路线不存在或未修改内容");
        }
        return success;
    }

    @Override
    public List<Route> searchRouteByKeyword(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRoutes();
        }
        return routeDAO.searchByKeyword(keyword.trim());
    }

    @Override
    public List<Route> getAllRoutes() throws SQLException {
        return routeDAO.selectAll();
    }

    @Override
    public Route getRouteByTipId(String routeId) throws SQLException {
        return routeDAO.findByTipId(routeId);
    }

}