package DAO.impl;

import DAO.RouteDAO;
import Entity.Route;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouteDAOImpl implements RouteDAO {

    public String generateUniqueRouteId() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        while (true) {
            String routeId = String.format("%06d", new Random().nextInt(999999));
            try {
                conn = DBUtil.getConnection();
                String checkSql = "SELECT COUNT(*) FROM route WHERE routeid = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setString(1, routeId);
                rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    return routeId;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBUtil.close(conn, pstmt, rs);
            }
        }
    }

    @Override
    public boolean addRoute(Route route) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO route (routeid, r_title, r_day,r_tipid) VALUES (?, ?, ?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, route.getRouteId());
            pstmt.setString(2, route.getR_title());
            pstmt.setInt(3, route.getR_day());
            pstmt.setString(4, route.getR_tipId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean deleteRoute(String tipId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM route WHERE r_tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tipId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateTitle(String routeId, String newTitle) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE route SET r_title = ? WHERE routeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newTitle);
            pstmt.setString(2, routeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateDay(String routeId, Integer newDay) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE route SET r_day = ? WHERE routeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newDay);
            pstmt.setString(2, routeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public List<Route> searchByKeyword(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Route> routes = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT routeId, r_title, r_day FROM route WHERE r_title LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Route route = new Route();
                route.setRouteId(rs.getString("routeId"));
                route.setR_title(rs.getString("r_title"));
                route.setR_day(rs.getInt("r_day"));
                routes.add(route);
            }
            return routes;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Route> selectAll() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Route> allRoutes = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT routeId, r_title, r_day FROM route";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Route route = new Route();
                route.setRouteId(rs.getString("routeId"));
                route.setR_title(rs.getString("r_title"));
                route.setR_day(rs.getInt("r_day"));
                allRoutes.add(route);
            }
            return allRoutes;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public Route findByTipId(String tipId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT routeId, r_title, r_day, r_tipid FROM route WHERE r_tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tipId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Route route = new Route();
                route.setRouteId(rs.getString("routeId"));
                route.setR_title(rs.getString("r_title"));
                route.setR_day(rs.getInt("r_day"));
                route.setR_tipId(rs.getString("r_tipid"));
                return route;
            }
            // 未找到对应路线时返回null
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}