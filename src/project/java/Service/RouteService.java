package Service;

import Entity.Route;
import java.sql.SQLException;
import java.util.List;

public interface RouteService {
    boolean addRoute(Route route);
    boolean deleteRoute(String routeId);
    boolean updateRouteTitle(String routeId, String newTitle);
    boolean updateRouteDay(String routeId, Integer newDay);
    List<Route> searchRouteByKeyword(String keyword) throws SQLException;
    List<Route> getAllRoutes() throws SQLException;
    Route getRouteByTipId(String tipId) throws SQLException;
}