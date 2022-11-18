package breve.breve.follow.repository;

import breve.breve.follow.model.Follow;
import breve.breve.users.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f from Follow f join fetch f.users join fetch f.follower e where e.email = :email")
    List<Follow> findByFollower(@Param("email") String email);

    @Query("select f from Follow f join fetch f.follower join fetch f.users u where u.email = :email")
    List<Follow> findByUsers(@Param("email") String email);

    @Query("select f from Follow f join fetch f.users join fetch f.follower e where e.nickname = :nickname")
    List<Follow> findByFollowerNickname(@Param("nickname") String nickname);

    @Query("select f from Follow f join fetch f.follower join fetch f.users u where u.nickname = :nickname")
    List<Follow> findByUsersNickname(@Param("nickname") String nickname);

    @Query("select f from Follow f join fetch f.follower join fetch f.users where f.follower = :follower and f.users = :users")
    Follow findOneFollow(@Param("follower") Users follower, @Param("users") Users users);
}
