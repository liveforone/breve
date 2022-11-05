package breve.breve.users.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String email;
    private Role auth;
    private String nickname;

    @Builder
    public UserResponse(Long id, String email, Role auth, String nickname) {
        this.id = id;
        this.email = email;
        this.auth = auth;
        this.nickname = nickname;
    }
}
