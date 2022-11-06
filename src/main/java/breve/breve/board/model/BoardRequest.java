package breve.breve.board.model;

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

    public Board toEntity() {
        return Board.builder()
                .id(id)
                .title(title)
                .content(content)
                .users(users)
                .hashTag(hashTag)
                .saveFileName(saveFileName)
                .view(view)
                .good(good)
                .build();
    }
}
