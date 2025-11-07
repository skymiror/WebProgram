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

    /**
     * 生成6位不重复的数字字符routeid
     */
    private String generateUniqueRouteId() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        while (true) {
            // 生成6位随机数字字符串
            String routeId = String.format("%06d", new Random().nextInt(999999));
            try {
                conn = DBUtil.getConnection();
                // 检查是否已存在该routeid
                String checkSql = "SELECT COUNT(*) FROM route WHERE routeid = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setString(1, routeId);
                rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    return routeId; // 不存在，返回该id
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBUtil.close(conn, pstmt, rs);
            }
        }
    }

    /**
     * 增加路线
     */
    @Override
    public boolean addRoute(Route route) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            // 自动生成routeid
            String routeId = generateUniqueRouteId();
            route.setRouteId(routeId);

            String sql = "INSERT INTO route (routeid, r_title, r_day) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, route.getRouteId());
            pstmt.setString(2, route.getR_title());
            pstmt.setInt(3, route.getR_day());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 删除路线
     */
    @Override
    public boolean deleteRoute(String routeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM route WHERE routeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, routeId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 修改标题
     */
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

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 修改天数
     */
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

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    // 数据库筛选：按关键字模糊匹配路线标题
    @Override
    public List<Route> searchByKeyword(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Route> routes = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            // SQL：匹配标题包含关键字的路线
            String sql = "SELECT routeId, r_title, r_day FROM route WHERE r_title LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%"); // 模糊匹配（包含关键字）

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

    // 查询所有路线（供 Lambda 内存处理）
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
}