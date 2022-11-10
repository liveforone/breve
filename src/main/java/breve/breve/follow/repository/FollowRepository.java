package breve.breve.follow.repository;

import breve.breve.follow.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f Follow f join fetch f.users join fetch f.follower e where e.email = :email")
    List<Follow> findByFollower(@Param("email") String email);
}
