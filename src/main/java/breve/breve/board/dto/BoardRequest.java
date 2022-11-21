package breve.breve.board.dto;

import breve.breve.board.model.Board;
import breve.breve.users.model.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardRequest {

    private Long id;
    private String title;
    private String content;
    private Users users;
    private String hashTag;
    private String saveFileName;
    private int view;
    private int good;
}
