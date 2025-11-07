package DAO;

import Entity.Route;

import java.sql.SQLException;
import java.util.List;

public interface RouteDAO {
    // 增加路线（自动生成routeid）
    boolean addRoute(Route route);

    // 删除路线（根据routeid）
    boolean deleteRoute(String routeId);

    // 修改标题（根据routeid）
    boolean updateTitle(String routeId, String newTitle);

    // 修改天数（根据routeid）
    boolean updateDay(String routeId, Integer newDay);

    //按关键字查询路线（匹配路线标题）
    List<Route> searchByKeyword(String keyword) throws SQLException;

   //查询所有路线（用于 Lambda 内存筛选）
    List<Route> selectAll() throws SQLException;
}
