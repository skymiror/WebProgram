package Service.impl;

import DAO.CollectionDAO;
import DAO.impl.CollectionDAOImpl;
import Entity.Collection;
import Service.CollectionService;

public class CollectionServiceImpl implements CollectionService {
    // 依赖数据访问层
    private CollectionDAO collectionDAO = new CollectionDAOImpl();

    @Override
    public boolean addCollection(String userId, String strategyId) {
        // 调用DAO层添加收藏的方法
        return collectionDAO.addCollection( userId, strategyId);
    }

    @Override
    public boolean deleteCollection(String userId, String strategyId) {
        // 调用DAO层删除收藏的方法
        return collectionDAO.deleteCollection(userId, strategyId);
    }

    @Override
    public boolean checkCollectionExists(String userId, String strategyId) {
        return collectionDAO.checkCollectionExists(userId, strategyId);
    }
}