package DAO;
import Entity.User;
import java.sql.SQLException;
public interface UserDAO {
    // 1. 注册用户（插入用户数据）
    void insert(User user) throws SQLException;

    // 2. 根据账号查询用户（用于验证账号是否存在、修改信息时查询原用户）
    User findByAccount(String account) throws SQLException;

    // 3. 修改用户简介
    void updateIntro(String account, String newIntro) throws SQLException;

    // 4. 修改用户密码
    void updatePassword(String account, String newPassword) throws SQLException;

}
