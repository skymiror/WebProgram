package Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collection {
    private String c_userId;
    private String c_strategyId;
    private Date collectTime; // 收藏时间
}
