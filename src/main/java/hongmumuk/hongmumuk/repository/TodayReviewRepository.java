package hongmumuk.hongmumuk.repository;

import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.entity.TodayReview;
import hongmumuk.hongmumuk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodayReviewRepository extends JpaRepository<TodayReview, Long> {

    Optional<TodayReview> findByUserAndRestaurant(User user, Restaurant restaurant);
}
