package Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    private String placeId; // 地点-id
    private String placeName; // 名称
    private String openTime; // 开放时间
    private String placeIntro; // 简介
}