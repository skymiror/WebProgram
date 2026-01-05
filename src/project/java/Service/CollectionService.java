package Service;

import Entity.Collection;

public interface CollectionService {
    // 添加收藏
    boolean addCollection(String userId, String strategyId);

    // 删除收藏
    boolean deleteCollection(String userId, String strategyId);

    boolean checkCollectionExists(String userId, String strategyId);
}