package hongmumuk.hongmumuk.dto;

import hongmumuk.hongmumuk.entity.Restaurant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantPageDto {
    public Long id;
    public String name;
    public Integer likes;
    public Double front;
    public Double back;
    public String category;

    public static RestaurantPageDto from(Restaurant restaurant) {
        return new RestaurantPageDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getLikes(),
                restaurant.getFront(),
                restaurant.getBack(),
                restaurant.getCategory().name()
        );
    }
}
