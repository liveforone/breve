package breve.breve.comment.service;

import breve.breve.board.model.Board;
import breve.breve.board.repository.BoardRepository;
import breve.breve.comment.dto.CommentRequest;
import breve.breve.comment.dto.CommentResponse;
import breve.breve.comment.model.Comment;
import breve.breve.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    //== entity ->  dto 편의메소드1 - 페이징 형식 ==//
    public Page<CommentResponse> entityToDtoPage(Page<Comment> commentList) {
        return commentList.map(m -> CommentResponse.builder()
                .id(m.getId())
                .writer(m.getWriter())
                .content(m.getContent())
                .good(m.getGood())
                .createdDate(m.getCreatedDate())
                .build()
        );
    }

    //== entity -> dto 편의메소드2 - 엔티티 하나 ==//
    public CommentResponse entityToDtoDetail(Comment comment) {

        if (comment != null) {
            return CommentResponse.builder()
                    .id(comment.getId())
                    .writer(comment.getWriter())
                    .content(comment.getContent())
                    .good(comment.getGood())
                    .createdDate(comment.getCreatedDate())
                    .build();
        } else {
            return null;
        }
    }

    public Page<CommentResponse> getCommentList(Long boardId, Pageable pageable) {
        return entityToDtoPage(commentRepository.findByBoardId(boardId, pageable));
    }

    public CommentResponse getCommentDetail(Long id) {
        return entityToDtoDetail(commentRepository.findOneById(id));
    }

    public Comment getCommentEntity(Long id) {
        return commentRepository.findOneById(id);
    }

    @Transactional
    public void saveComment(CommentRequest commentRequest, String writer, Long boardId) {
        Board board = boardRepository.findOneById(boardId);
        commentRequest.setWriter(writer);
        commentRequest.setBoard(board);

        commentRepository.save(commentRequest.toEntity());
    }

    @Transactional
    public void editComment(String content, Long id) {
        commentRepository.updateComment(content, id);
    }

    @Transactional
    public void updateGood(Long id) {
        commentRepository.updateGood(id);
    }
}
