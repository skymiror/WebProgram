package DAO.impl;

import DAO.StrategyDAO;
import Entity.Strategy;
import Util.DBUtil; // 自定义数据库连接工具类

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StrategyDAOImpl implements StrategyDAO {

    // 数据库连接工具类（获取连接、关闭资源）
    private DBUtil dbUtil = new DBUtil();

    @Override
    public int insert(Strategy strategy) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 1. 获取数据库连接
            conn = dbUtil.getConnection();
            // 2. 编写SQL
            String sql = "INSERT INTO strategy (tipid, title, content) " +
                    "VALUES (?, ?, ?, ?)";
            // 3. 创建PreparedStatement（预编译SQL，防止SQL注入）
            pstmt = conn.prepareStatement(sql);
            // 4. 设置参数（按SQL中"?"的顺序）
            pstmt.setString(1, strategy.getTipId());
            pstmt.setString(2, strategy.getTitle());
            pstmt.setString(3, strategy.getContent());
            // 5. 执行SQL（返回影响行数）
            return pstmt.executeUpdate();
        } finally {
            // 6. 关闭资源（无论成功失败都要关闭）
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public Strategy selectByPostId(String postId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dbUtil.getConnection();
            String sql = "SELECT tipid, title, content" +
                    "FROM strategy WHERE tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, postId);
            // 执行查询，返回结果集
            rs = pstmt.executeQuery();
            // 处理结果集（若有数据，封装为Strategy对象）
            if (rs.next()) {
                Strategy strategy = new Strategy();
                strategy.setTipId(rs.getString("tipid"));
                strategy.setTitle(rs.getString("title"));
                strategy.setContent(rs.getString("content"));


                return strategy;
            }
            return null; // 无数据返回null
        } finally {
            DBUtil.close(conn, pstmt, null); // 关闭结果集、Statement、连接
        }
    }

    @Override
    public int updateTitle(String tipId, String newTitle) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dbUtil.getConnection();
            String sql = "UPDATE strategy SET title = ? WHERE tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newTitle);
            pstmt.setString(2, tipId);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int updateContent(String tipId, String newContent) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dbUtil.getConnection();
            String sql = "UPDATE strategy SET content = ? WHERE tipid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newContent);
            pstmt.setString(2, tipId);
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
    //查询所有攻略
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
            DBUtil.close(conn, pstmt, rs); // 关闭 ResultSet
        }
    }
    //通过关键字查询攻略
    @Override
    public List<Strategy> selectByTitleKeyword(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Strategy> strategies = new ArrayList<>();

        try {
            conn = dbUtil.getConnection();
            // SQL 用 LIKE 匹配关键字（% 表示任意字符）
            String sql = "SELECT tipid, title, content FROM strategy WHERE title LIKE ?";
            pstmt = conn.prepareStatement(sql);
            // 设置参数：关键字前后加 %，实现模糊匹配（如“北京”匹配“北京攻略”“去北京”等）
            pstmt.setString(1, "%" + keyword + "%");

            rs = pstmt.executeQuery();
            // 封装结果集
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
    //用 Lambda 处理查询结果
    public List<Strategy> selectByKeywordWithLambda(String keyword) throws SQLException {
        List<Strategy> allStrategies = selectAll(); // 获取所有攻略

        return allStrategies.stream()
                // 筛选标题包含关键词
                .filter(strategy -> {
                    String title = strategy.getTitle();
                    return title != null && title.toLowerCase().contains(keyword.toLowerCase());
                })
                // 按标题升序排序
                .sorted(Comparator.comparing(Strategy::getTitle))
                .collect(Collectors.toList());
    }
}