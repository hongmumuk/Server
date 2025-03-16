package hongmumuk.hongmumuk.repository;

import hongmumuk.hongmumuk.entity.Blog;
import hongmumuk.hongmumuk.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findAllByRestaurant(Optional<Restaurant> restaurant);

}
