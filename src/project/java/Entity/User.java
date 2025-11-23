package Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;
    private String account;
    private String password;
    private String introduce;
    private Date registerTime;
    private String phone;
}
