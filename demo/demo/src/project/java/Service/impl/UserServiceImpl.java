package Service.impl;

import DAO.UserDAO;
import DAO.impl.UserDAOImpl;
import Entity.User;
import Service.UserService;

import java.sql.SQLException;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public User login(String account, String password) throws SQLException {
        // 调用DAO层查询用户，校验密码等业务逻辑
        User user = userDAO.findByAccount(account);
        if (user == null || !password.equals(user.getPassword())) {
            throw new SQLException("账号或密码错误");
        }
        // 封装返回结果（可隐藏敏感字段）
        User loginUser = new User();
        loginUser.setUsername(user.getUsername());
        loginUser.setAccount(user.getAccount());
        return loginUser;
    }

    @Override
    public void register(User user) throws SQLException {
        // 校验账号唯一性、参数格式等业务逻辑
        if (userDAO.findByAccount(user.getAccount()) != null) {
            throw new SQLException("账号已存在");
        }
        userDAO.insert(user);
    }
}