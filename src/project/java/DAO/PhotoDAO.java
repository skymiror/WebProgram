package DAO;
import Entity.Photo;

import java.sql.SQLException;
import java.util.List;

public interface PhotoDAO {
    void addPhoto(Photo photo) throws SQLException;
    List<Photo> selectByTipId(String tipid) throws SQLException;
}
