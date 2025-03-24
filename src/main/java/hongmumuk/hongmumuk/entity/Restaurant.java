package hongmumuk.hongmumuk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private Integer likes;
    private Double longitude;
    private Double latitude;
    @Enumerated(EnumType.STRING)
    private Category category;
    private Double front;
    private Double back;
    private String naverLink;
    private String kakaoLink;
    private String fileName;
    private String imageUrl;


    public void setLikes(int i) {
        this.likes = i;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setFront(Double front) {
        this.front = front;
    }

    public void setBack(Double back) {
        this.back = back;
    }

    public void setNaverLink(String naverLink) {
        this.naverLink = naverLink;
    }

    public void setKakaoLink(String kakaoLink) {
        this.kakaoLink = kakaoLink;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
