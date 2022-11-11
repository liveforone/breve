package breve.breve.bookmark.repository;

import breve.breve.board.model.Board;
import breve.breve.bookmark.model.Bookmark;
import breve.breve.follow.model.Follow;
import breve.breve.users.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("select b from Bookmark b join fetch b.users join fetch b.board where b.users = :users and b.board = :board")
    Bookmark findUsersAndBoard(@Param("users") Users users, @Param("board") Board board);
}
