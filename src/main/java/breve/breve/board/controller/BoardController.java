package breve.breve.board.controller;

import breve.breve.board.model.BoardRequest;
import breve.breve.board.model.BoardResponse;
import breve.breve.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/board/post")
    public ResponseEntity<?> postPage() {
        return ResponseEntity
                .ok("게시글 등록 페이지");
    }

    /*
    두가지 생성 조건 존재
    1. 파일 없다.
    2. 파일 있다.
     */
    @PostMapping("/board/post")
    public ResponseEntity<?> boardPost(
            @RequestPart MultipartFile uploadFile,
            @RequestPart("boardRequest") BoardRequest boardRequest,
            Principal principal
    ) throws IllegalStateException, IOException {

        if (!uploadFile.isEmpty()) {  //파일 있는 게시글
            Long boardId = boardService.saveBoardFile(boardRequest, uploadFile, principal.getName());
            log.info("게시글 작성, 파일 저장 성공 !!");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/board/" + boardId));

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {  //파일 없는 게시글
            Long boardId = boardService.saveBoardNoFile(boardRequest, principal.getName());
            log.info("게시글 작성 성공 !!");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/board/" + boardId));

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        }
    }

    /*
    작성자 검증은 이메일로 해도 상관없음.
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<Map<String, Object>> boardDetail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Map<String, Object> map = new HashMap<>();
        String user = principal.getName();
        String writer = boardService.getBoardEntity(id).getUsers().getEmail();
        BoardResponse board = boardService.getBoardDetail(id);

        map.put("user", user);
        map.put("writer", writer);
        map.put("body", board);

        boardService.updateView(id);
        log.info("조회수 +1 성공!!");

        return ResponseEntity.ok(map);
    }

    //== 상품 상세조회 이미지 ==//
    @GetMapping("/board/image/{saveFileName}")
    @ResponseBody
    public Resource showImage(
            @PathVariable("saveFileName") String saveFileName
    ) throws MalformedURLException {
        return new UrlResource("file:C:\\Temp\\upload\\" + saveFileName);
    }
}
