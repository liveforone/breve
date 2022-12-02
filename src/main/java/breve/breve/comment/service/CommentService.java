package breve.breve.comment.service;

import breve.breve.board.model.Board;
import breve.breve.board.repository.BoardRepository;
import breve.breve.comment.dto.CommentRequest;
import breve.breve.comment.dto.CommentResponse;
import breve.breve.comment.model.Comment;
import breve.breve.comment.repository.CommentRepository;
import breve.breve.utility.CommonUtils;
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

    //== CommentResponse builder method ==//
    public CommentResponse dtoBuilder(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .good(comment.getGood())
                .createdDate(comment.getCreatedDate())
                .build();
    }

    //== dto -> entity ==//
    public Comment dtoToEntity(CommentRequest comment) {
        return Comment.builder()
                    .id(comment.getId())
                    .writer(comment.getWriter())
                    .content(comment.getContent())
                    .board(comment.getBoard())
                    .good(comment.getGood())
                    .build();
    }

    //== entity ->  dto 편의메소드1 - 페이징 형식 ==//
    public Page<CommentResponse> entityToDtoPage(Page<Comment> commentList) {
        return commentList.map(this::dtoBuilder);
    }

    //== entity -> dto 편의메소드2 - 엔티티 하나 ==//
    public CommentResponse entityToDtoDetail(Comment comment) {

        if (CommonUtils.isNull(comment)) {
            return null;
        }
        return dtoBuilder(comment);
    }

    public Page<CommentResponse> getCommentList(Long boardId, Pageable pageable) {
        return entityToDtoPage(
                commentRepository.findByBoardId(
                        boardId,
                        pageable
                )
        );
    }

    public Comment getCommentEntity(Long id) {
        return commentRepository.findOneById(id);
    }

    @Transactional
    public void saveComment(
            CommentRequest commentRequest,
            String writer,
            Long boardId
    ) {
        Board board = boardRepository.findOneById(boardId);
        commentRequest.setWriter(writer);
        commentRequest.setBoard(board);

        commentRepository.save(
                dtoToEntity(commentRequest)
        );
    }

    @Transactional
    public void editComment(String content, Long id) {
        commentRepository.updateComment(content, id);
    }

    @Transactional
    public void updateGood(Long id) {
        commentRepository.updateGood(id);
    }

    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
