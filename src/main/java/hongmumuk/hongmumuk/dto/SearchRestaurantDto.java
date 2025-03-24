package hongmumuk.hongmumuk.dto;

import hongmumuk.hongmumuk.entity.Restaurant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
// pagenation없이 모든 식당 정보 주기 (검색기능)
public class SearchRestaurantDto {
    public Long id;
    public String name;
    public Integer likes;
    public Double front;
    public Double back;
    public String imageUrl;
    public String category;

    public static SearchRestaurantDto from(Restaurant restaurant) {
        return new SearchRestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getLikes(),
                restaurant.getFront(),
                restaurant.getBack(),
                restaurant.getImageUrl(),
                restaurant.getCategory().name() // Enum → String 변환
        );
    }
}