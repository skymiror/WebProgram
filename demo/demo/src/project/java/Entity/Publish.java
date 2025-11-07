package Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publish {
    private String p_userid; // 发布ID
    private String p_placeId;
    private String p_tipsId; // 发布内容
    private String p_routeId; // 发布标题
    private Date time; // 发布时间
}
