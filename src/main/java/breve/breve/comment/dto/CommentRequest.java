package breve.breve.comment.dto;

import breve.breve.board.model.Board;
import breve.breve.comment.model.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequest {

    private Long id;
    private String writer;
    private String content;
    private Board board;
    private int good;

    public Comment toEntity() {
        return Comment.builder()
                .id(id)
                .writer(writer)
                .content(content)
                .board(board)
                .good(good)
                .build();
    }
}
