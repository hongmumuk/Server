package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.JwtUtil;
import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.ReviewDto;
import hongmumuk.hongmumuk.dto.ReviewPageDto;
import hongmumuk.hongmumuk.entity.*;
import hongmumuk.hongmumuk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final TodayReviewRepository todayReviewRepository;

    @Transactional
    public ResponseEntity<?> allReviews(long restaurantId, int page, String sort, boolean isUser) {

        long reviewCount = reviewRepository.countByRestaurantId(restaurantId);

        Pageable pageable;
        if ("high".equals(sort)) {
            pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "star"));
        } else if ("low".equals(sort)) {
            pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "star"));
        } else {
            pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        }

        Page<Review> reviewPage = reviewRepository.findByRestaurantId(restaurantId, pageable);
        List<Review> reviews = reviewPage.getContent();

        if (reviews.isEmpty()) {
            ReviewPageDto emptyResult = new ReviewPageDto(reviewCount, Collections.emptyList());
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, emptyResult));
        }

        List<Long> userIds = reviews.stream()
                .map(r -> r.getUser().getId())
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Long> userReviewCounts = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<Review> allReviewsByUsers = reviewRepository.findByUserIdIn(userIds);
            for (Long userIdVal : userIds) {
                long count = allReviewsByUsers.stream()
                        .filter(r -> r.getUser().getId().equals(userIdVal))
                        .count();
                userReviewCounts.put(userIdVal, count);
            }
        }

        List<Long> reviewIds = reviews.stream()
                .map(Review::getId)
                .collect(Collectors.toList());

        Map<Long, List<String>> imageUrlsByReviewId = new HashMap<>();
        if (!reviewIds.isEmpty()) {
            List<ReviewImage> images = reviewImageRepository.findAllByReviewIdIn(reviewIds);
            for (ReviewImage ri : images) {
                imageUrlsByReviewId
                        .computeIfAbsent(ri.getReview().getId(), k -> new ArrayList<>())
                        .add(ri.getImageUrl());
            }
        }

        if (isUser) {
            String userEmail = JwtUtil.getCurrentUserEmail();
            Optional<User> userId = userRepository.findByEmail(userEmail);
            if(userId.isEmpty()){
                return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
            }

            User currentUser = userId.get();
            List<ReviewDto> reviewDtos = reviews.stream().map(r -> {
                User user = r.getUser();
                long rank = userReviewCounts.getOrDefault(user.getId(), 0L);
                List<String> imgUrls = imageUrlsByReviewId.getOrDefault(r.getId(), Collections.emptyList());
                boolean isMine = currentUser.getId().equals(user.getId());

                return new ReviewDto(
                        r.getId(),
                        user.getNickName(),
                        r.getStar(),
                        r.getContent(),
                        r.getCreatedDate(),
                        rank,
                        isMine,
                        imgUrls
                );
            }).collect(Collectors.toList());
            ReviewPageDto result = new ReviewPageDto(reviewCount, reviewDtos);
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, result));
        }
        else {
            List<ReviewDto> reviewDtos = reviews.stream().map(r -> {
                User user = r.getUser();
                long rank = userReviewCounts.getOrDefault(user.getId(), 0L);
                List<String> imgUrls = imageUrlsByReviewId.getOrDefault(r.getId(), Collections.emptyList());
                boolean isMine = false;

                return new ReviewDto(
                        r.getId(),
                        user.getNickName(),
                        r.getStar(),
                        r.getContent(),
                        r.getCreatedDate(),
                        rank,
                        isMine,
                        imgUrls
                );
            }).collect(Collectors.toList());
            ReviewPageDto result = new ReviewPageDto(reviewCount, reviewDtos);
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, result));
        }
    }

    @Transactional
    public ResponseEntity<?> createReview(String email, List<String> imageUrls, ReviewDto.newReviewDto newReviewDto){

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNAUTHORIZED_ERROR));
        }

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(newReviewDto.getRid());

        if(restaurantOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }

        User user = userOptional.get();
        Restaurant restaurant = restaurantOptional.get();

        Review review = new Review();

        review.setUser(user);
        review.setStar(newReviewDto.getStar());
        review.setRestaurant(restaurant);
        review.setContent(newReviewDto.getContent());
        review.setCreatedDate(LocalDate.now());

        reviewRepository.save(review);

        if(imageUrls.isEmpty()){
            TodayReview todayReview = new TodayReview();

            todayReview.setUser(user);
            todayReview.setRestaurant(restaurant);

            todayReviewRepository.save(todayReview);

            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.CREATED));
        }
        else{
            for (String imageUrl : imageUrls) {
                ReviewImage reviewImage = new ReviewImage();

                reviewImage.setReview(review);
                reviewImage.setImageUrl(imageUrl);

                reviewImageRepository.save(reviewImage);
            }

            TodayReview todayReview = new TodayReview();

            todayReview.setUser(user);
            todayReview.setRestaurant(restaurant);

            todayReviewRepository.save(todayReview);

            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.CREATED));
        }
    }

    @Transactional
    public ResponseEntity<?> deleteReview(Long reviewId){
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if(reviewOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.REVIEW_NOT_AVAILABLE));
        }

        Review review = reviewOptional.get();

        User user = review.getUser();
        Restaurant restaurant = review.getRestaurant();

        Optional<TodayReview> todayReviewOptional = todayReviewRepository.findByUserAndRestaurant(user, restaurant);

        todayReviewOptional.ifPresent(todayReviewRepository::delete); // 리뷰 작성 여부 초기화.

        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewId(review.getId());

        reviewImageRepository.deleteAll(reviewImages);
        reviewRepository.delete(review);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    @Transactional
    public ResponseEntity<?> checkAvailable(String email, Long rid){

        Optional<User> userOptional = userRepository.findByEmail(email);
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(rid);

        if(userOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }

        if(restaurantOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }

        User user = userOptional.get();
        Restaurant restaurant = restaurantOptional.get();

        Optional<TodayReview> todayReviewOptional = todayReviewRepository.findByUserAndRestaurant(user, restaurant);

        if(todayReviewOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.AVAILABLE));
        }

        return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.ALREADY_REVIEWED));
    }
}
