package breve.breve.comment.dto;

import breve.breve.board.model.Board;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {

    private Long id;
    private String writer;
    private String content;
    private int good;
    private LocalDateTime createdDate;

    @Builder
    public CommentResponse(Long id, String writer, String content, int good, LocalDateTime createdDate) {
        this.id = id;
        this.writer = writer;
        this.content = content;
        this.good = good;
        this.createdDate = createdDate;
    }
}
