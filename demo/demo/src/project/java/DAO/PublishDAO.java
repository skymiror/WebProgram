package DAO;

import Entity.Publish;

public interface PublishDAO {
    // 发布攻略（新增记录）
    boolean addPublish(Publish publish);

    // 删除攻略（根据tipsId删除）
    boolean deletePublishByTipsId(String tipsId);
}
