package DAO;

import Entity.Place;

import java.sql.SQLException;
import java.util.List;

public interface PlaceDAO {
    // 添加地点（ID生成+插入统一在DAO层完成）
    int insert(Place place) throws SQLException;

    // 根据 ID 删除地点
    int deleteByTipId(String tipId) throws SQLException;

    // 修改地点简介
    int updatePlaceIntro(String placeId, String newIntro) throws SQLException;

    // 修改开放时间
    int updateOpenTime(String placeId, String newOpenTime) throws SQLException;

    // 根据关键字查询地点
    List<Place> searchByKeyword(String keyword) throws SQLException;

    // 检查地点名称是否已存在（新增：用于重复校验）
    boolean isPlaceNameExists(String placeName) throws SQLException;

    List<Place> findByTipId(String tipId) throws SQLException;
}