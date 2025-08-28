package hongmumuk.hongmumuk.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReviewDto {
    private Long reviewId;
    private String name;
    private Integer star;
    private String content;
    private LocalDate createdDate;
    private Long rank;
    private boolean isMine;
    private List<String> imageUrls;

    @Data
    @Builder
    public static class newReviewDto{
        private Long rid; // 식당 id
        private Integer star;
        private String content;
    }

    @Data
    @Builder
    public static class availableDto{
        private Long rid; // 식당 id
    }

    @Data
    @Builder
    public static class myReviewDto{
        private Long reviewId;
        private Long rid; // 식당 id
        private String rname;
        private String uname;
        private int star;
        private String content;
        private int rank;
        private String category;
        private LocalDate createdDate;
        private List<String> imageUrls;

    }

    public ReviewDto(Long reviewId, String name, int star, String content, LocalDate createdDate, long rank, boolean isMine, List<String> imageUrls) {
        this.reviewId = reviewId;
        this.name = name;
        this.star = star;
        this.content = content;
        this.createdDate = createdDate;
        this.rank = rank;
        this.isMine = isMine;
        this.imageUrls = imageUrls;
    }

}
