package DAO;

import Entity.Route;
import java.sql.SQLException;
import java.util.List;

public interface RouteDAO {
    boolean addRoute(Route route);
    boolean deleteRoute(String tipId);
    boolean updateTitle(String routeId, String newTitle);
    boolean updateDay(String routeId, Integer newDay);
    List<Route> searchByKeyword(String keyword) throws SQLException;
    List<Route> selectAll() throws SQLException;
    Route findByTipId(String tipId) throws SQLException;
}