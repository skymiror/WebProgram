package Service.impl;

import DAO.StrategyDAO;
import DAO.impl.StrategyDAOImpl;
import Entity.Strategy;
import Service.PhotoService;
import Service.impl.PhotoServiceImpl;
import Service.StrategyService;

import java.sql.SQLException;
import java.util.List;

public class StrategyServiceImpl implements StrategyService {
    private final StrategyDAO strategyDAO = new StrategyDAOImpl();
    private final PhotoService photoService = new PhotoServiceImpl();

    @Override
    public void insert(Strategy strategy) throws SQLException {
        strategyDAO.insert(strategy);
    }

    @Override
    public List<Strategy> selectAll() throws SQLException {
        return strategyDAO.selectAll();
    }

    @Override
    public List<Strategy> selectAllWithCoverImage() throws SQLException {
        List<Strategy> strategies = strategyDAO.selectAll();
        for (Strategy strategy : strategies) {
            // 只加载**一张**关联图片作为封面（若没有则设为默认占位图）
            List<Entity.Photo> photos = photoService.selectByTipId(strategy.getTipId());
            if (!photos.isEmpty()) {
                strategy.setCoverImagePath(photos.get(0).getPath());
            } else {
                // 无图片时显示默认占位图（可替换为你项目中的默认图片路径）
                strategy.setCoverImagePath("https://img.icons8.com/ios-filled/100/image.png");
            }
        }
        return strategies;
    }
}