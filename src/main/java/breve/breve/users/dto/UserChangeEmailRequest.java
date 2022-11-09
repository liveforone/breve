package breve.breve.users.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserChangeEmailRequest {

    private String email;
    private String password;
}
