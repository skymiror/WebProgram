package Service;

import Entity.Place;
import java.sql.SQLException;
import java.util.List;

public interface PlaceService {
    Place insert(Place place) throws SQLException;

    //检查地点名称是否已存在
    boolean isPlaceNameExists(String placeName) throws SQLException;

    List<Place> searchByKeyword(String keyword) throws SQLException;

    List<Place> findByTipId(String tipId) throws SQLException;
}