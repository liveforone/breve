package breve.breve.board.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
