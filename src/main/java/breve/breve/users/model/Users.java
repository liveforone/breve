package breve.breve.users.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Role auth;

    private String nickname;

    @Builder
    public Users(Long id, String email, String password, Role auth, String nickname) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.auth = auth;
        this.nickname = nickname;
    }
}
