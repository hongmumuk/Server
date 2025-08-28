package hongmumuk.hongmumuk.controller;

import hongmumuk.hongmumuk.common.JwtUtil;
import hongmumuk.hongmumuk.dto.PageDto;
import hongmumuk.hongmumuk.dto.ReviewDto;
import hongmumuk.hongmumuk.service.ReviewService;
import hongmumuk.hongmumuk.service.S3Service;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final S3Service s3Service;

    @GetMapping("")
    public ResponseEntity<?> allReviews(@RequestParam int restaurantId, @RequestParam int page, @RequestParam String sort, @RequestParam boolean isUser) {
        return reviewService.allReviews(restaurantId, page, sort, isUser);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReview(@RequestPart(required = false) MultipartFile[] multipartFiles,
                                          @RequestPart ReviewDto.newReviewDto newReviewDto
    ){
        String email = JwtUtil.getCurrentUserEmail();

        List<String> imageUrls = new ArrayList<>();

        if(multipartFiles != null){
            for (MultipartFile file : multipartFiles) {
                imageUrls.add(s3Service.uploadFile(file));
            }
        }

        return reviewService.createReview(email, imageUrls, newReviewDto);
    }

    @DeleteMapping("{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId){
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/available/{rid}")
    public ResponseEntity<?> checkAvailable(@PathVariable Long rid){
        String email = JwtUtil.getCurrentUserEmail();
        return reviewService.checkAvailable(email, rid);
    }
}
