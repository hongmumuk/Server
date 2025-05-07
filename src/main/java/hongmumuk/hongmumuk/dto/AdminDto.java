package hongmumuk.hongmumuk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Data
@Builder
public class AdminDto {

    @Data
    @Builder
    public static class modifyRestaurantDto{

        private Long rid;

        @Schema(defaultValue = "null")
        private String name;
        @Schema(defaultValue = "null")
        private String address;
        @Schema(defaultValue = "null")
        private Double longitude;
        @Schema(defaultValue = "null")
        private Double latitude;
        @Schema(defaultValue = "null")
        private Double front;
        @Schema(defaultValue = "null")
        private Double back;
        @Schema(defaultValue = "null")
        private String naverLink;
        @Schema(defaultValue = "null")
        private String kakaoLink;
    }

    @Data
    @Builder
    public static class deleteRestaurantDto{
        private Long rid;
    }

    @Data
    @Builder
    public static class modifyResNameDto{
        private Long rid;
        private String name;
    }
}
