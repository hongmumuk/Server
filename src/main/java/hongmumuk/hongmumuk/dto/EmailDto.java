package hongmumuk.hongmumuk.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {

    private String email;
    private Boolean univ_check;

    @Builder
    @Data
    public static class VerifyDto {
        private String email;
        private Integer code;

    }
}
