package hongmumuk.hongmumuk.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {

    private String email;

    @Builder
    @Data
    public static class VerifyDto {
        private String email;
        private String code;

    }
}
