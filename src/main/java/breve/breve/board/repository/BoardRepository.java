package breve.breve.board.repository;

import breve.breve.board.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Modifying
    @Query("update Board b set b.view = b.view + 1 where b.id = :id")
    void updateView(@Param("id") Long id);

    @Modifying
    @Query("update Board b set b.good = b.good + 1 where b.id = :id")
    void updateGood(@Param("id") Long id);
}
