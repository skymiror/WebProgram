package Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Integer commentId; // 评论id
    private String content; // 内容
    private Integer parentId; // 父级id
    private Date time; // 时间
}