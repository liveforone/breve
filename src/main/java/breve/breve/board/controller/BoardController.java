package breve.breve.board.controller;

import breve.breve.board.model.BoardResponse;
import breve.breve.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/today")
    public ResponseEntity<Page<BoardResponse>> todayPage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "view", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<BoardResponse> board = boardService.getTodayBoard(pageable);

        return ResponseEntity.ok(board);
    }

    @GetMapping("/board/best")
    public ResponseEntity<Page<BoardResponse>> bestPage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<BoardResponse> board = boardService.getBoardPaging(pageable);

        return ResponseEntity.ok(board);
    }

    @GetMapping("/board/search")
    public ResponseEntity<Page<BoardResponse>> searchPage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "view", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam("keyword") String keyword
    ) {
        Page<BoardResponse> board = boardService.getSearchBoard(keyword, pageable);

        return ResponseEntity.ok(board);
    }

    @GetMapping("/board/hashtag/{hashtag}")
    public ResponseEntity<Page<BoardResponse>> hashTagPage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @PathVariable("hashtag") String hashtag
    ) {
        Page<BoardResponse> board = boardService.getBoardByHashTag(hashtag, pageable);

        return ResponseEntity.ok(board);
    }
}
