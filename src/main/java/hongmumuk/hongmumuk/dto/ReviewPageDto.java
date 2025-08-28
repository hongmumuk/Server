package hongmumuk.hongmumuk.dto;

import hongmumuk.hongmumuk.entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.eclipse.angus.mail.imap.IMAPNestedMessage;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ReviewPageDto {
    // 작성된 총 리뷰 개수
    public Long reviewCount;
    public List<ReviewDto> reviewDto;

    @Data
    @Builder
    public static class myReviewPageDto{
        private Long reviewCount;
        private List<ReviewDto.myReviewDto> myReviewDtoList;
    }
}