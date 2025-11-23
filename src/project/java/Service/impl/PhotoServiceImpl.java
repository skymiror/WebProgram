package Service.impl;

import DAO.PhotoDAO;
import DAO.impl.PhotoDAOImpl;
import Entity.Photo;
import Service.PhotoService;
import java.sql.SQLException;
import java.util.List;

public class PhotoServiceImpl implements PhotoService {
    private final PhotoDAO photoDAO = new PhotoDAOImpl();

    @Override
    public void uploadPhoto(Photo photo) throws SQLException {
        // 此处可添加业务校验（如：图片描述长度限制、tipid 格式校验等）
        if (photo.getDescribe().length() > 500) {
            throw new SQLException("图片描述不能超过500字");
        }
        if (!photo.getTipId().matches("\\d+")) { // 校验 tipid 为数字
            throw new SQLException("tipid 必须是数字");
        }

        // 调用 DAO 层方法，将图片信息插入数据库
        photoDAO.addPhoto(photo);
    }

    @Override
    public List<Photo> selectByTipId(String tipid) throws SQLException {
        return photoDAO.selectByTipId(tipid);
    }
}
