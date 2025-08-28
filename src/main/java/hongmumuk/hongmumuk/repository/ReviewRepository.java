package hongmumuk.hongmumuk.repository;

import hongmumuk.hongmumuk.entity.Review;
import hongmumuk.hongmumuk.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    long countByRestaurantId(long restaurantId);

    Page<Review> findByRestaurantId(long restaurantId, Pageable pageable);

    Page<Review> findByUser(User user, Pageable pageable);

    List<Review> findByUserIdIn(List<Long> userIds);
}
