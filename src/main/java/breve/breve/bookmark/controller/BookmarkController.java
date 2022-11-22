package breve.breve.bookmark.controller;

import breve.breve.board.model.Board;
import breve.breve.board.service.BoardService;
import breve.breve.bookmark.model.Bookmark;
import breve.breve.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final BoardService boardService;

    @GetMapping("/my-bookmark")
    public ResponseEntity<Map<String, Object>> myBookmark(Principal principal) {
        Map<String, Object> bookmarkList =
                bookmarkService.getBookmarkList(principal.getName());

        return ResponseEntity.ok(bookmarkList);
    }

    @PostMapping("/bookmark/post/{boardId}")
    public ResponseEntity<?> bookmarking(
            @PathVariable("boardId") Long boardId,
            Principal principal
    ) {
        Board board = boardService.getBoardEntity(boardId);
        Bookmark bookmark = bookmarkService.getBookmarkDetail(
                boardId,
                principal.getName()
        );

        if (board == null) {
            return ResponseEntity.ok("게시글을 찾을 수 없어 북마킹이 불가능합니다.");
        }

        if (bookmark != null) {
            return ResponseEntity.ok("이미 북마크 하였습니다.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/board/" + boardId
        ));

        bookmarkService.saveBookmark(
                principal.getName(),
                boardId
        );
        log.info("북마킹 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/bookmark/cancel/{boardId}")
    public ResponseEntity<?> bookmarkCancel(
            @PathVariable("boardId") Long boardId,
            Principal principal
    ) {
        Board board = boardService.getBoardEntity(boardId);
        Bookmark bookmark = bookmarkService.getBookmarkDetail(
                boardId,
                principal.getName()
        );

        if (board == null) {
            return ResponseEntity.ok("게시글을 찾을 수 없어 북마크 취소가 불가능합니다.");
        }

        if (bookmark == null) {
            return ResponseEntity.ok("해당 북마크는 이미 취소되었습니다.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/board/" + boardId
        ));

        bookmarkService.cancelBookmark(
                principal.getName(),
                boardId
        );
        log.info("북마크 삭제 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
