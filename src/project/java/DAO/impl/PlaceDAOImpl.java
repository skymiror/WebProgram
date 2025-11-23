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
        int randomNum = random.nextInt(900000) + 100000; // 100000-999999
        return String.valueOf(randomNum);
    }

    // 检查placeid是否已存在（内部查重）
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

    // 生成唯一placeid（内部使用，不对外暴露）
    public String generateUniquePlaceId() throws SQLException {
        String placeId;
        do {
            placeId = generateRandomPlaceId();
        } while (isPlaceIdExists(placeId));
        return placeId;
    }

    // 新增：检查地点名称是否已存在（用于重复添加校验）
    @Override
    public boolean isPlaceNameExists(String placeName) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 精确匹配地点名称，避免重复添加
            String sql = "SELECT COUNT(*) FROM place WHERE placename = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, placeName.trim());
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    // 新增地点（核心：DAO层内部生成唯一ID并写入数据库）
    @Override
    public int insert(Place place) throws SQLException {
        // 1. 生成唯一placeid（DAO层内部完成）
        String uniquePlaceId = generateUniquePlaceId();
        // 2. 给place实体设置ID（后续可通过place.getPlaceId()获取）
        place.setPlaceId(uniquePlaceId);

        // 3. 插入数据库
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO place (placeid, placename, opentime, placeintro,p_tipid) " +
                    "VALUES (?, ?, ?, ?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, place.getPlaceId());
            pstmt.setString(2, place.getPlaceName().trim());
            pstmt.setString(3, place.getOpenTime() != null ? place.getOpenTime().trim() : "");
            pstmt.setString(4, place.getPlaceIntro() != null ? place.getPlaceIntro().trim() : "");
            pstmt.setString(5, place.getP_tipId());
            return pstmt.executeUpdate(); // 返回受影响行数（1表示成功）
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 删除地点（不变）
    @Override
    public int deleteByTipId(String tipId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM place WHERE p_tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tipId);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 更新地点介绍（不变）
    @Override
    public int updatePlaceIntro(String placeId, String newIntro) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE place SET placeintro = ? WHERE placeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newIntro.trim());
            pstmt.setString(2, placeId);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 更新开放时间（不变）
    @Override
    public int updateOpenTime(String placeId, String newOpenTime) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE place SET opentime = ? WHERE placeid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newOpenTime.trim());
            pstmt.setString(2, placeId);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 通过关键字查询（不变）
    @Override
    public List<Place> searchByKeyword(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Place> places = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT placeid, placename, opentime, placeintro " +
                    "FROM place " +
                    "WHERE placename LIKE ? OR placeintro LIKE ?";
            pstmt = conn.prepareStatement(sql);
            String likeParam = "%" + keyword.trim() + "%";
            pstmt.setString(1, likeParam);
            pstmt.setString(2, likeParam);

            rs = pstmt.executeQuery();
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
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Place> findByTipId(String tipId) throws SQLException {
        if (tipId == null || tipId.trim().isEmpty()) {
            return new ArrayList<>(); // 空参数返回空列表
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Place> places = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            // 查询该tipId关联的所有地点（一个tipId可能对应多个place）
            String sql = "SELECT placeid, placename, opentime, placeintro, p_tipid " +
                    "FROM place WHERE p_tipid = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tipId); // 设置单个tipId参数

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Place place = new Place();
                place.setPlaceId(rs.getString("placeid"));
                place.setPlaceName(rs.getString("placename"));
                place.setOpenTime(rs.getString("opentime"));
                place.setPlaceIntro(rs.getString("placeintro"));
                place.setP_tipId(rs.getString("p_tipid"));
                places.add(place);
            }
            return places;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}