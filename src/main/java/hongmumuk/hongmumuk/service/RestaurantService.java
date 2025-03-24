package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.JwtUtil;
import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.LikeAndDislikeDto;
import hongmumuk.hongmumuk.dto.RestaurantDto;
import hongmumuk.hongmumuk.entity.*;
import hongmumuk.hongmumuk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    // 식당 좋아요 추가 기능
    @Transactional
    public ResponseEntity<?> likeRestaurant(LikeAndDislikeDto likeAndDislikeDto) {
        String userEmail = JwtUtil.getCurrentUserEmail();
        Long likedRestaurantId = likeAndDislikeDto.getId();

        Optional<User> userId = userRepository.findByEmail(userEmail);
        if(userId.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }
        User user = userId.get();

        Optional<Restaurant> restaurantId = restaurantRepository.findById(likedRestaurantId);
        if(restaurantId.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }
        Restaurant restaurant = restaurantId.get();

        if (likedRestaurantRepository.existsByUserAndRestaurant(user, restaurant)) {
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.BAD_REQUEST));
        }
        else {
            LikedRestaurant likedRestaurant = LikedRestaurant.builder()
                    .user(user)
                    .restaurant(restaurant)
                    .build();
            restaurant.setLikes(restaurant.getLikes()+1);
            likedRestaurantRepository.save(likedRestaurant);

            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
        }
    }

    // 식당 좋아요 삭제 기능
    @Transactional
    public ResponseEntity<?> dislikeRestaurant(LikeAndDislikeDto likeAndDislikeDto) {
        String userEmail = JwtUtil.getCurrentUserEmail();
        Long dislikedRestaurantId = likeAndDislikeDto.getId();

        Optional<User> userId = userRepository.findByEmail(userEmail);
        if(userId.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }
        User user = userId.get();

        Optional<Restaurant> restaurantId = restaurantRepository.findById(dislikedRestaurantId);
        if(restaurantId.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }
        Restaurant restaurant = restaurantId.get();

        Optional<LikedRestaurant> likedRestaurant = likedRestaurantRepository.findByUserAndRestaurant(user, restaurant);
        if (likedRestaurant.isEmpty()) {
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.LIKED_NOT_EXISTS));
        }

        if (likedRestaurantRepository.existsByUserAndRestaurant(user, restaurant)) {
            restaurant.setLikes(restaurant.getLikes()-1);
            likedRestaurantRepository.delete(likedRestaurant.get());
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
        }
        else {
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.BAD_REQUEST));
        }
    }

    @Transactional
    public ResponseEntity<?> findRestaurant(int restaurantId, boolean isUser) {
        // 식당 정보 가져오기 -> name, likes, category, longitude, latitude, front, back
        Optional<Restaurant> restaurant = restaurantRepository.findById((long) restaurantId);
        if(restaurant.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }

        String name = restaurant.get().getName();
        String address = restaurant.get().getAddress();
        Integer likes = restaurant.get().getLikes();
        String category = String.valueOf(restaurant.get().getCategory());
        Double longitude = restaurant.get().getLongitude();
        Double latitude = restaurant.get().getLatitude();
        Double front = restaurant.get().getFront();
        Double back = restaurant.get().getBack();
        String naverLink = restaurant.get().getNaverLink();
        String kakaoLink = restaurant.get().getKakaoLink();
        String imageUrl = restaurant.get().getImageUrl();
        boolean hasLiked;

        if (isUser) {
            String userEmail = JwtUtil.getCurrentUserEmail();
            Optional<User> userId = userRepository.findByEmail(userEmail);
            User user = userId.get();
            Optional<LikedRestaurant> likedRestaurant = likedRestaurantRepository.findByUserAndRestaurant(user,restaurant.get());

            if(likedRestaurant.isEmpty()){
                hasLiked = false;
            }
            else {
                hasLiked = true;
            }
        }
        else {
            hasLiked = false;

        }


        // 블로그 정보 가져오기
        List<Blog> blog = blogRepository.findAllByRestaurant(restaurant);

        RestaurantDto restaurantDto = RestaurantDto.builder()
                .id(String.valueOf(restaurantId))
                .address(address)
                .name(name)
                .likes(likes)
                .category(category)
                .longitude(longitude)
                .latitude(latitude)
                .hasLiked(hasLiked)
                .front(front)
                .back(back)
                .naverLink(naverLink)
                .kakaoLink(kakaoLink)
                .imageUrl(imageUrl)
                .blogs(blog)
                .build();

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, restaurantDto));
    }


}
