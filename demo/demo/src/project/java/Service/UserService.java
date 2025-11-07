package Service;

import Entity.User;
import java.sql.SQLException;

public interface UserService {
    User login(String account, String password) throws SQLException;
    void register(User user) throws SQLException;
    // 可根据业务需求添加其他方法，如修改密码、查询用户信息等
}