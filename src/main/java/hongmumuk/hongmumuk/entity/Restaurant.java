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
    private Integer views;
    private String menuUrl;
    private Double longitude;
    private Double latitude;
    @Enumerated(EnumType.STRING)
    private Category category;
    private Double front;
    private Double back;

}
