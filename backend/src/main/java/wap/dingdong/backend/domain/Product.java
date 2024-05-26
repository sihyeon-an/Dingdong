package wap.dingdong.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String title;
    private Long price;
    private String contents;

    @Column(columnDefinition = "int default 0")
    private Integer status = 0; //기본값 0

    // 찜 - 수정
    @Column(columnDefinition = "int default 0")
    private Integer productLike = 0; // 상품 찜 수

    // 어노테이션, 데이터타입, 변수명 수정
    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Buy buy;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Sell sell;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wish wish;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();


    public Product(User user, String title, Long price, String contents, List<Location> locations, List<Image> images) {
        this.user = user;
        this.title = title;
        this.price = price;
        this.contents = contents;
        this.images = images;
        this.locations = locations;
    }


    /* ------------- 상품 찜하기 메소드 -------------- */

    // 찜하기 클릭한 사용자 이메일 저장
    @ElementCollection
    @CollectionTable(name = "product_likes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "user_id")
    private Set<Long> likedByMembers = new HashSet<>(); // 좋아요를 누른 사용자 이메일 저장

    public void increaseLike(Long user_id) {
        if (!likedByMembers.contains(user_id)) {
            productLike++;
            likedByMembers.add(user_id);
        }
    }

    public void decreaseLike(Long user_id) {
        if (likedByMembers.contains(user_id)) {
            productLike--;
            likedByMembers.remove(user_id);
        }
    }

    public boolean isLikedByMember(Long user_id) {
        if (this.user == null || this.likedByMembers.isEmpty()) {
            return false;
        }
        return this.likedByMembers.contains(user_id);
    }
}