package hongmumuk.hongmumuk.dto;

import hongmumuk.hongmumuk.entity.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private String nickName;
    private String email;

    @Data
    @Builder
    public static class passwordDto{
        private String password;
    }
}
