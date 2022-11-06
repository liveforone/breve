package breve.breve.board.model;

import breve.breve.users.model.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @Size(max = 50)
    private String title;

    @Column(columnDefinition = "TEXT", length = 300)
    @Size(max = 300)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;  //작성자

    private String hashTag;  //null 허용
    private String saveFileName;

    @Column(columnDefinition = "integer default 0")
    private int view; //조회수

    @Column(columnDefinition = "integer default 0")
    private int good;  //좋아요

    @CreatedDate
    private LocalDate createdDate;

    @Builder
    public Board(Long id, String title, String content, Users users, String hashTag, String saveFileName, int view, int good) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.users = users;
        this.hashTag = hashTag;
        this.saveFileName = saveFileName;
        this.view = view;
        this.good = good;
    }
}
