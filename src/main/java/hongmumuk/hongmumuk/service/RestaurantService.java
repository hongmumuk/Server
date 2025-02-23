package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.JwtUtil;
import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.LikeAndDislikeDto;
import hongmumuk.hongmumuk.entity.LikedRestaurant;
import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.entity.User;
import hongmumuk.hongmumuk.repository.LikedRestaurantRepository;
import hongmumuk.hongmumuk.repository.RestaurantRepository;
import hongmumuk.hongmumuk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;
    private final UserRepository userRepository;

    // 식당 좋아요 추가 기능
    @Transactional
    public ResponseEntity<?> likeRestaurant(LikeAndDislikeDto likeAndDislikeDto) {
        String userEmail = JwtUtil.getCurrentUserEmail();
        Long likedRestaurantId = likeAndDislikeDto.getId();

        Optional<User> userId = userRepository.findByEmail(userEmail);
        Optional<Restaurant> restaurantId = restaurantRepository.findById(likedRestaurantId);

        User user = userId.get();
        Restaurant restaurant = restaurantId.get();

        LikedRestaurant likedRestaurant = LikedRestaurant.builder()
                .user(user)
                .restaurant(restaurant)
                .build();

        likedRestaurantRepository.save(likedRestaurant);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    // 식당 좋아요 삭제 기능
    @Transactional
    public ResponseEntity<?> dislikeRestaurant(LikeAndDislikeDto likeAndDislikeDto) {
        String userEmail = JwtUtil.getCurrentUserEmail();
        Long dislikedRestaurantId = likeAndDislikeDto.getId();

        Optional<User> userId = userRepository.findByEmail(userEmail);
        Optional<Restaurant> restaurantId = restaurantRepository.findById(dislikedRestaurantId);

        User user = userId.get();
        Restaurant restaurant = restaurantId.get();

        LikedRestaurant dislikedRestaurant = LikedRestaurant.builder()
                .user(user)
                .restaurant(restaurant)
                .build();

        likedRestaurantRepository.delete(dislikedRestaurant);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }
}
