package hongmumuk.hongmumuk.dto;

import hongmumuk.hongmumuk.entity.Restaurant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RandomDto {
    private Long id;
    private String name;

    public static RandomDto from(Restaurant restaurant) {
        return new RandomDto(
                restaurant.getId(),
                restaurant.getName()
        );
    }
}
