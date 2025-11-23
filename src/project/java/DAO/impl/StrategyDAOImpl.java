package DAO.impl;

import DAO.StrategyDAO;
import Entity.Strategy;
import Util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StrategyDAOImpl implements StrategyDAO {

    private DBUtil dbUtil = new DBUtil();
    private final Random random = new Random(); // 随机数生成器（优化性能）

    @Override
    public int insert(Strategy strategy) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dbUtil.getConnection();
            String sql = "INSERT INTO strategy (tipid, title, content,s_userid,time) VALUES (?, ?, ?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, strategy.getTipId());
            pstmt.setString(2, strategy.getTitle());
            pstmt.setString(3, strategy.getContent());
            pstmt.setString(4, strategy.getS_userId());
            pstmt.setTimestamp(5,new Timestamp(strategy.getCreateTime().getTime()));
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 生成并返回唯一的6位纯数字tipid（范围：100000 ~ 999999，避免以0开头）
    public String generateUniqueTipid() throws SQLException {
        while (true) {
            String tipid = generateRandomDigitalTipid(6);
            if (!isTipidExists(tipid)) {
                return tipid;
            }
        }
    }

    // 生成6位纯数字字符串（确保是100000~999999的整数，无前置0）
    private String generateRandomDigitalTipid(int length) {
        if (length != 6) {
            throw new IllegalArgumentException("tipid必须是6位数字");
        }
        // 生成100000 ~ 999999之间的随机整数（6位纯数字，无前置0）
        int num = random.nextInt(900000) + 100000;
        return String.valueOf(num);
    }

    // 校验tipid是否已存在（复用逻辑）
    private boolean isTipidExists(String tipid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dbUtil.getConnection();
            String sql = "SELECT COUNT(1) FROM strategy WHERE tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tipid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // 存在返回true，不存在返回false
            }
            return false;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    // 以下方法保持不变（仅修复selectByPostId的SQL语法错误）
    @Override
    public Strategy selectByPostId(String postId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dbUtil.getConnection();
            // 修复SQL语法错误：content后加空格，避免拼接成contentFROM
            String sql = "SELECT tipid, title, content FROM strategy WHERE tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, postId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Strategy strategy = new Strategy();
                strategy.setTipId(rs.getString("tipid"));
                strategy.setTitle(rs.getString("title"));
                strategy.setContent(rs.getString("content"));
                return strategy;
            }
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs); // 修复：之前未关闭ResultSet，现已补充
        }
    }

    @Override
    public int updateStrategy(String tipId, String newTitle, String newContent) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dbUtil.getConnection();
            // 动态构建SQL，根据参数是否为null决定是否更新对应字段
            StringBuilder sql = new StringBuilder("UPDATE strategy SET ");
            List<String> updateFields = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            if (newTitle != null && !newTitle.isEmpty()) {
                updateFields.add("title = ?");
                params.add(newTitle);
            }
            if (newContent != null && !newContent.isEmpty()) {
                updateFields.add("content = ?");
                params.add(newContent);
            }

            // 如果没有需要更新的字段，直接返回0
            if (updateFields.isEmpty()) {
                return 0;
            }

            sql.append(String.join(", ", updateFields))
                    .append(" WHERE tipid = ?");
            params.add(tipId);

            pstmt = conn.prepareStatement(sql.toString());
            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }


    @Override
    public int deleteByPostId(String tipId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dbUtil.getConnection();
            String sql = "DELETE FROM strategy WHERE tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tipId);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public List<Strategy> selectAll() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Strategy> strategies = new ArrayList<>();
        try {
            conn = dbUtil.getConnection();
            String sql = "SELECT tipid, title, content FROM strategy";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Strategy strategy = new Strategy();
                strategy.setTipId(rs.getString("tipid"));
                strategy.setTitle(rs.getString("title"));
                strategy.setContent(rs.getString("content"));
                strategies.add(strategy);
            }
            return strategies;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Strategy> selectByTitleKeyword(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Strategy> strategies = new ArrayList<>();
        try {
            conn = dbUtil.getConnection();
            String sql = "SELECT tipid, title, content FROM strategy WHERE title LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Strategy strategy = new Strategy();
                strategy.setTipId(rs.getString("tipid"));
                strategy.setTitle(rs.getString("title"));
                strategy.setContent(rs.getString("content"));
                strategies.add(strategy);
            }
            return strategies;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    public List<Strategy> selectByKeywordWithLambda(String keyword) throws SQLException {
        List<Strategy> allStrategies = selectAll();
        return allStrategies.stream()
                .filter(strategy -> {
                    String title = strategy.getTitle();
                    return title != null && title.toLowerCase().contains(keyword.toLowerCase());
                })
                .sorted(Comparator.comparing(Strategy::getTitle))
                .collect(Collectors.toList());
    }
}