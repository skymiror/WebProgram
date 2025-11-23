package Service;

import Entity.Strategy;

import java.sql.SQLException;
import java.util.List;

public interface StrategyService {
    void insert(Strategy strategy) throws SQLException;
    List<Strategy> selectAll() throws SQLException;
    List<Strategy> selectAllWithCoverImage() throws SQLException;
}
