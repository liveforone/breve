package breve.breve.board.controller;

import breve.breve.board.model.Board;
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
import java.util.Objects;

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
    public ResponseEntity<?> boardDetail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        BoardResponse board = boardService.getBoardDetail(id);

        if (board != null) {
            Map<String, Object> map = new HashMap<>();
            String user = principal.getName();
            String writer = boardService.getBoardEntity(id).getUsers().getEmail();

            map.put("user", user);
            map.put("writer", writer);
            map.put("body", board);

            boardService.updateView(id);
            log.info("조회수 +1 성공!!");

            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.ok("해당 게시글이 없어 조회할 수 없습니다.");
        }
    }

    //== 상품 상세조회 이미지 ==//
    @GetMapping("/board/image/{saveFileName}")
    @ResponseBody
    public Resource showImage(
            @PathVariable("saveFileName") String saveFileName
    ) throws MalformedURLException {
        return new UrlResource("file:C:\\Temp\\upload\\" + saveFileName);
    }

    @PostMapping("/board/good/{id}")
    public ResponseEntity<?> boardGood(@PathVariable("id") Long id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/board/" + id));

        boardService.updateGood(id);
        log.info("좋아요 반영 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/board/edit/{id}")
    public ResponseEntity<?> boardEditPage(@PathVariable("id") Long id) {
        BoardResponse board = boardService.getBoardDetail(id);

        return ResponseEntity.ok(Objects.requireNonNullElse(board, "해당 게시글이 없어조회할 수 없습니다."));
    }

    /*
    게시글을 수정하는 조건
    게시글 수정시 파일 있다 : 1. 없었는데 생김, 2. 있었는데 바꿈
    게시글 수정시 파일 없다 : 1. 원래 없다, 2. 있었는데 안바꿈
     */
    @PostMapping("/board/edit/{id}")
    public ResponseEntity<?> boardEdit(
            @PathVariable("id") Long id,
            @RequestPart MultipartFile uploadFile,
            @RequestPart("boardRequest") BoardRequest boardRequest,
            Principal principal
    ) throws IllegalStateException, IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/board/" + id));

        String writer = boardService.getBoardEntity(id).getUsers().getEmail();

        if (Objects.equals(writer, principal.getName())) {  //작성자일 때
            if (!uploadFile.isEmpty()) {  //파일이 있다면
                boardService.editBoardFile(id, uploadFile, boardRequest);
                log.info("게시글 id=" + id + " 수정 완료!!");
            } else {  //파일이 없다면
                boardService.editBoardNoFile(id, boardRequest);
                log.info("게시글 id=" + id + " 수정 완료!!");
            }

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();

        } else {
            log.info("작성자와 현재 유저가 달라 수정 불가능.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @PostMapping("/board/delete/{id}")
    public ResponseEntity<?> boardDelete(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        String writer = boardService.getBoardEntity(id).getUsers().getEmail();
        
        if (Objects.equals(writer, principal.getName())) {
            boardService.deleteBoard(id);
            log.info("게시글 id=" + id + " 삭제 완료!!");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/board/today"));

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {
            log.info("작성자와 현재 유저가 달라 삭제 불가능.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }
}
