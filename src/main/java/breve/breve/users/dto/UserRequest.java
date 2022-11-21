package breve.breve.users.dto;

import breve.breve.users.model.Role;
import breve.breve.users.model.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest {

    private Long id;
    private String email;
    private String password;
    private Role auth;
    private String nickname;
}
