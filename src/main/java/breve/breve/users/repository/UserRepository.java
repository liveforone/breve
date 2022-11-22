package breve.breve.users.repository;

import breve.breve.users.model.Role;
import breve.breve.users.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

    @Query("select u from Users u where u.nickname = :nickname")
    Users findByNickname(@Param("nickname") String nickname);

    @Query("select u from Users u where u.nickname like %:nickname%")
    List<Users> searchByNickName(@Param("nickname") String nickname);

    //== 권한 업데이트 ==//
    @Modifying
    @Query("update Users u set u.auth = :auth where u.email = :email")
    void updateAuth(@Param("auth") Role auth, @Param("email") String email);

    @Modifying
    @Query("update Users u set u.nickname = :nickname where u.email = :email")
    void updateNickname(@Param("nickname") String nickname, @Param("email") String email);

    @Modifying
    @Query("update Users u set u.email = :newEmail where u.email = :oldEmail")
    void updateEmail(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    @Modifying
    @Query("update Users u set u.password = :password where u.id = :id")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}
