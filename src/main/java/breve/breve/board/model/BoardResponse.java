package breve.breve.board.model;

import breve.breve.users.model.Users;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardResponse {

    private Long id;
    private String title;
    private String content;
    private String hashTag;
    private String saveFileName;
    private int view;
    private int good;
    private LocalDate createdDate;

    @Builder
    public BoardResponse(Long id, String title, String content, String hashTag, String saveFileName, int view, int good, LocalDate createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hashTag = hashTag;
        this.saveFileName = saveFileName;
        this.view = view;
        this.good = good;
        this.createdDate = createdDate;
    }
}
