package breve.breve.bookmark.controller;

import breve.breve.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/bookmark/{boardId}")
    public ResponseEntity<?> bookmarking(
            @PathVariable("boardId") Long boardId,
            Principal principal
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/board/" + boardId));

        bookmarkService.saveBookmark(principal.getName(), boardId);
        log.info("북마킹 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/bookmark-cancel/{boardId}")
    public ResponseEntity<?> bookmarkCancel(
            @PathVariable("boardId") Long boardId,
            Principal principal
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/board/" + boardId));

        bookmarkService.bookmarkCancel(principal.getName(), boardId);
        log.info("북마크 삭제 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
