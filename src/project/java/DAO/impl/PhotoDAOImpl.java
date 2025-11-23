package DAO.impl;
import DAO.PhotoDAO;
import Entity.Photo;
import Util.DBUtil;

import java.sql.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PhotoDAOImpl implements PhotoDAO{
    @Override
    public void addPhoto(Photo photo) throws SQLException {
        String sql = "INSERT INTO photo (`describe`, tipid, path) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, photo.getDescribe()); // 图片描述
            pstmt.setString(2, photo.getTipId());    // 关联的帖子ID
            pstmt.setString(3, photo.getPath());     // 图片存储路径
            // 4. 执行插入操作
            pstmt.executeUpdate();
        } finally {
            // 5. 关闭资源（必须调用 DBUtil 的 close 方法，避免连接泄露）
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public List<Photo> selectByTipId(String tipid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Photo> photos = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM photo WHERE tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tipid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Photo photo = new Photo();
                photo.setPhotoId(rs.getInt("photoId"));
                photo.setDescribe(rs.getString("describe"));
                photo.setTipId(rs.getString("tipid"));
                photo.setPath(rs.getString("path"));
                photos.add(photo);
            }
            return photos;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}
