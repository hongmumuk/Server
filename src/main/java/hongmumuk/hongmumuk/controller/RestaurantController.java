package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.dto.LikeAndDislikeDto;
import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {


    private final RestaurantService restaurantService;

    // 식당 좋아요 추가 기능
    @PostMapping("/like")
    public ResponseEntity<?> like(@RequestBody LikeAndDislikeDto likeAndDislikeDto) {
        return restaurantService.likeRestaurant(likeAndDislikeDto);
    }


    // 식당 좋아요 삭제 기능
    @PostMapping("/dislike")
    public ResponseEntity<?> dislike(@RequestBody LikeAndDislikeDto likeAndDislikeDto) {
        return restaurantService.dislikeRestaurant(likeAndDislikeDto);
    }

    @GetMapping("")
    public ResponseEntity<?> findRestaurant(@RequestParam int restaurantId, boolean isUser) {
        return restaurantService.findRestaurant(restaurantId, isUser);
    }
}
