package breve.breve.comment.repository;

import breve.breve.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join c.board b where b.id = :id")
    Page<Comment> findByBoardId(@Param("id") Long id, Pageable pageable);

    @Query("select c from Comment c join fetch c.board where c.id = :id")
    Comment findOneById(@Param("id") Long id);

    @Modifying
    @Query("update Comment c set c.content = :content where c.id = :id")
    void updateComment(@Param("content") String content, @Param("id") Long id);

    @Modifying
    @Query("update Comment c set c.good = c.good + 1 where c.id = :id")
    void updateGood(@Param("id") Long id);
}
