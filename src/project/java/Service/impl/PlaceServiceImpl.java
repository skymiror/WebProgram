package Service.impl;

import DAO.PlaceDAO;
import DAO.impl.PlaceDAOImpl;
import Entity.Place;
import Service.PlaceService;
import java.sql.SQLException;
import java.util.List;

public class PlaceServiceImpl implements PlaceService {
    private final PlaceDAO placeDAO = new PlaceDAOImpl();

    @Override
    public Place insert(Place place) throws SQLException {
        // 业务层仅做参数校验，ID生成和插入完全委托给DAO层
        if (place == null || place.getPlaceName() == null || place.getPlaceName().trim().isEmpty()) {
            throw new SQLException("地点名称不能为空");
        }
        // 调用DAO层插入方法（DAO层内部生成ID并设置到place实体）
        placeDAO.insert(place);
        // 返回包含ID的place实体
        return place;
    }

    @Override
    public boolean isPlaceNameExists(String placeName) throws SQLException {
        // 业务层转发到DAO层查询
        if (placeName == null || placeName.trim().isEmpty()) {
            return false;
        }
        return placeDAO.isPlaceNameExists(placeName);
    }

    @Override
    public List<Place> searchByKeyword(String keyword) throws SQLException {
        return placeDAO.searchByKeyword(keyword);
    }

    @Override
    public List<Place> findByTipId(String tipId) throws SQLException {
        return placeDAO.findByTipId(tipId);
    }
}