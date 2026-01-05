package DAO;

import Entity.Collection;

public interface CollectionDAO {
    // 添加收藏
    boolean addCollection(String userId, String strategyId);

    // 删除收藏（根据用户ID和攻略ID联合删除，因为是唯一标识）
    boolean deleteCollection(String userId, String strategyId);

    boolean checkCollectionExists(String userId, String strategyId);
}
