package hongmumuk.hongmumuk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {
    private String accessToken;
    private String refreshToken;
}
