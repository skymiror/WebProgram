package DAO;

import Entity.Strategy;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface StrategyDAO {
    // 发布攻略
    int insert(Strategy strategy) throws SQLException;

    // 根据ID查询攻略
    Strategy selectByPostId(String tipId) throws SQLException;

    int updateStrategy(String tipId, String newTitle, String newContent) throws SQLException;

    //查询攻略（通过关键字）
    List<Strategy> selectByTitleKeyword(String keyword) throws SQLException;

    // 删除攻略
    int deleteByPostId(String tipId) throws SQLException;
    // 查询所有攻略
    List<Strategy> selectAll() throws SQLException;

}
