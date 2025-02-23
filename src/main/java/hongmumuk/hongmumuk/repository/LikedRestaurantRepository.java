package hongmumuk.hongmumuk.repository;

import hongmumuk.hongmumuk.entity.LikedRestaurant;
import hongmumuk.hongmumuk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikedRestaurantRepository extends JpaRepository<LikedRestaurant, Long> {
    List<LikedRestaurant> findByUser(User user);
}
