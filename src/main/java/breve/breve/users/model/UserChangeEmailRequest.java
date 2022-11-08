package breve.breve.users.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserChangeEmailRequest {

    private String email;
    private String password;
}
