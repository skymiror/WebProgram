package Service;

import Entity.Photo;

import java.sql.SQLException;
import java.util.List;

public interface PhotoService {
    void uploadPhoto(Photo photo) throws SQLException;
    List<Photo> selectByTipId(String tipid) throws SQLException;
}
