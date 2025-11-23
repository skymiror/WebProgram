package Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Strategy {
    private String tipId; // 帖子ID
    private String title; // 标题
    private String content;// 正文内容
    private String coverImagePath;
    private String s_userId;
    private Date createTime;
}
