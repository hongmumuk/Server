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
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subTitle;
    private String url;
    private String postDate;
    private String bloggerName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    public Blog(String title, String subTitle, String url, String postDate, String bloggerName, Restaurant restaurant) {
        this.title = title;
        this.subTitle = subTitle;
        this.url = url;
        this.postDate = postDate;
        this.bloggerName = bloggerName;
        this.restaurant = restaurant;
    }
}
