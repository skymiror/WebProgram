package Service;

import Entity.User;
import java.sql.SQLException;

public interface UserService {
    User login(String account, String password) throws SQLException;
    void register(User user) throws SQLException;
    void updateIntro(String account, String newIntro) throws SQLException;
}