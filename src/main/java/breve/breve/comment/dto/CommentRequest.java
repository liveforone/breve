package breve.breve.comment.dto;

import breve.breve.board.model.Board;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRequest {

    private Long id;
    private String writer;
    private String content;
    private Board board;
    private int good;
}
