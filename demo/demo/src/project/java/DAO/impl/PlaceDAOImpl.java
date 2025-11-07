package DAO.impl;

import DAO.PlaceDAO;
import Entity.Place;
import Util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaceDAOImpl implements PlaceDAO {

    // 生成6位随机placeid
    private String generateRandomPlaceId() {
        Random random = new Random();
        int randomNum = random.nextInt(900000) + 100000;
        return String.valueOf(randomNum);
    }

    // 检查placeid是否已存在
    private boolean isPlaceIdExists(String placeId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM place WHERE placeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, placeId);
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    // 生成唯一placeid
    private String generateUniquePlaceId() throws SQLException {
        String placeId;
        do {
            placeId = generateRandomPlaceId();
        } while (isPlaceIdExists(placeId));
        return placeId;
    }

    // 新增地点
    @Override
    public int insert(Place place) throws SQLException {
        String uniquePlaceId = generateUniquePlaceId();
        place.setPlaceId(uniquePlaceId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO place (placeid, placename, opentime, placeintro) " +
                    "VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, place.getPlaceId());
            pstmt.setString(2, place.getPlaceName());
            pstmt.setString(3, place.getOpenTime());
            pstmt.setString(4, place.getPlaceIntro());
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 删除地点
    @Override
    public int deleteByPlaceId(String placeId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM place WHERE placeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, placeId);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 更新地点介绍
    @Override
    public int updatePlaceIntro(String placeId, String newIntro) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE place SET placeintro = ? WHERE placeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newIntro);
            pstmt.setString(2, placeId);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 更新开放时间（保持不变）
    @Override
    public int updateOpenTime(String placeId, String newOpenTime) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE place SET opentime = ? WHERE placeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newOpenTime);
            pstmt.setString(2, placeId);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 通过关键字查询
    @Override
    public List<Place> searchByKeyword(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Place> places = new ArrayList<>(); // 存储查询到的地点列表

        try {
            conn = DBUtil.getConnection();
            // SQL：模糊匹配地点名称（placename）或介绍（placeintro）包含关键字的记录
            String sql = "SELECT placeid, placename, opentime, placeintro " +
                    "FROM place " +
                    "WHERE placename LIKE ? OR placeintro LIKE ?";
            pstmt = conn.prepareStatement(sql);

            // 关键字前后加%，实现“包含关键字”的模糊查询（如“故宫”可匹配“故宫博物院”“游故宫”等）
            String likeParam = "%" + keyword + "%";
            pstmt.setString(1, likeParam); // 匹配地点名称
            pstmt.setString(2, likeParam); // 匹配地点介绍

            rs = pstmt.executeQuery();
            // 遍历结果集，封装为Place对象
            while (rs.next()) {
                Place place = new Place();
                place.setPlaceId(rs.getString("placeid"));
                place.setPlaceName(rs.getString("placename"));
                place.setOpenTime(rs.getString("opentime"));
                place.setPlaceIntro(rs.getString("placeintro"));
                places.add(place);
            }
            return places;
        } finally {
            DBUtil.close(conn, pstmt, rs); // 关闭资源
        }
    }
}