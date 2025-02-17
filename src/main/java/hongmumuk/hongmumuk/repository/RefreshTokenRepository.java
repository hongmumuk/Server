package hongmumuk.hongmumuk.repository;

import hongmumuk.hongmumuk.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findById(String userId);
}
