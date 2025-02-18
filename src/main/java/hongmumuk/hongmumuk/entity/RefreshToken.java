package hongmumuk.hongmumuk.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id //User id와 동일하게 저장
    private String id;

    private String refreshToken;

    public void updateValue(String token) {
        this.refreshToken = token;
    }
}
