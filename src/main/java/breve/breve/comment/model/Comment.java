package breve.breve.comment.model;

import breve.breve.board.model.Board;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writer;  //users와 연관관계를 맺을 이유가 딱히 없음, 작성자.

    @Column(columnDefinition = "TEXT", nullable = false, length = 100)
    @Size(max = 100)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(columnDefinition = "integer default 0")
    private int good;  //좋아요

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Builder
    public Comment(Long id, String writer, String content, Board board, int good) {
        this.id = id;
        this.writer = writer;
        this.content = content;
        this.board = board;
        this.good = good;
    }
}
