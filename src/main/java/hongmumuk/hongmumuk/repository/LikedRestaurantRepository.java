package hongmumuk.hongmumuk.repository;

import hongmumuk.hongmumuk.entity.LikedRestaurant;
import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikedRestaurantRepository extends JpaRepository<LikedRestaurant, Long> {
    List<LikedRestaurant> findByUser(User user);

    Optional<LikedRestaurant> findByUserAndRestaurant(User user, Restaurant restaurant);

    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);

    List<LikedRestaurant> findAllByRestaurant(Restaurant restaurant);
}
