package hongmumuk.hongmumuk.dto;

import hongmumuk.hongmumuk.entity.Blog;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestaurantDto {
    private String id;
    private String name;
    private String address;
    private Integer likes;
    private String category;
    private Double longitude;
    private Double latitude;
    private Boolean hasLiked;
    private Double front;
    private Double back;
    private String naverLink;
    private String kakaoLink;
    private String imageUrl;
    // 블로그 리스트
    private List<Blog> blogs;

}