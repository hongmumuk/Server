package hongmumuk.hongmumuk.dto;

import hongmumuk.hongmumuk.entity.Restaurant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchRestaurantDto {
    public Long id;
    public String name;
    public Integer likes;
    public Double front;
    public Double back;
    public String category;

    public static SearchRestaurantDto from(Restaurant restaurant) {
        return new SearchRestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getLikes(),
                restaurant.getFront(),
                restaurant.getBack(),
                restaurant.getCategory().name() // Enum → String 변환
        );
    }
}