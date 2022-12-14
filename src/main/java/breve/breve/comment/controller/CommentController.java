package breve.breve.comment.controller;

import breve.breve.board.model.Board;
import breve.breve.board.service.BoardService;
import breve.breve.comment.dto.CommentRequest;
import breve.breve.comment.dto.CommentResponse;
import breve.breve.comment.model.Comment;
import breve.breve.comment.service.CommentService;
import breve.breve.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

    @GetMapping("/comment/{boardId}")
    public ResponseEntity<Map<String, Object>> commentList(
            @PageableDefault(page = 0, size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @PathVariable("boardId") Long boardId,
            Principal principal
    ) {
        Map<String, Object> map = new HashMap<>();
        Page<CommentResponse> commentList =
                commentService.getCommentList(boardId, pageable);

        map.put("user", principal.getName());
        map.put("body", commentList);

        return ResponseEntity.ok(map);
    }

    @PostMapping("/comment/post/{boardId}")
    public ResponseEntity<?> commentPost(
            @PathVariable("boardId") Long boardId,
            @RequestBody CommentRequest commentRequest,
            Principal principal
    ) {
        Board board = boardService.getBoardEntity(boardId);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("?????? ???????????? ?????? ??????????????? ??????????????????.");
        }

        String url = "/comment/" + boardId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        commentService.saveComment(
                commentRequest,
                principal.getName(),
                boardId
        );
        log.info("?????? ?????? ??????!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/comment/edit/{id}")
    public ResponseEntity<?> commentEditPage(@PathVariable("id") Long id) {
        CommentResponse comment =
                commentService.entityToDtoDetail(commentService.getCommentEntity(id));

        return ResponseEntity.ok(
                Objects.requireNonNullElse(
                        comment,
                        "?????? ????????? ?????? ??? ????????????."
                )
        );
    }

    /*
    ????????? ??????????????? ??? ??? ??? ???????????? ?????? ????????? ????????????.
     */
    @PostMapping("/comment/edit/{id}")
    public ResponseEntity<?> commentEdit(
            @PathVariable("id") Long id,
            @RequestBody String content,
            Principal principal
    ) {
        Comment comment = commentService.getCommentEntity(id);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("?????? ????????? ?????? ????????? ??????????????????.");
        }

        if (!Objects.equals(comment.getWriter(), principal.getName())) {
            return ResponseEntity
                    .ok("???????????? ?????? ???????????? ?????? ????????? ??? ????????????.");
        }

        String url = "/comment/" + comment.getBoard().getId();
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        commentService.editComment(
                content,
                id
        );
        log.info("?????? ?????? ?????? !!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/comment/good/{id}")
    public ResponseEntity<?> commentUpdateGood(@PathVariable("id") Long id) {
        Comment comment = commentService.getCommentEntity(id);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("?????? ????????? ?????? ????????? ????????? ??????????????????.");
        }

        String url = "/comment/" + comment.getBoard().getId();
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        commentService.updateGood(id);
        log.info("?????? ????????? ???????????? ??????!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/comment/delete/{id}")
    public ResponseEntity<?> commentDelete(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Comment comment = commentService.getCommentEntity(id);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("?????? ???????????? ?????? ????????? ??????????????????.");
        }

        if (!Objects.equals(comment.getWriter(), principal.getName())) {
            return ResponseEntity
                    .ok("???????????? ???????????? ?????? ?????? ?????? ????????? ??????????????????.");
        }

        Long boardId = comment.getBoard().getId();

        commentService.deleteComment(id);
        log.info("?????? ???????????? !!");

        String url = "/comment/" + boardId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
