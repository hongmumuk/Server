package hongmumuk.hongmumuk.repository;

import hongmumuk.hongmumuk.entity.Category;
import hongmumuk.hongmumuk.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Page<Restaurant> findAllByCategory(Category category, Pageable pageable);

}
