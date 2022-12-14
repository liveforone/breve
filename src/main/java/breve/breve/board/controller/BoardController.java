package breve.breve.board.controller;

import breve.breve.board.dto.BoardRequest;
import breve.breve.board.dto.BoardResponse;
import breve.breve.board.model.Board;
import breve.breve.board.service.BoardService;
import breve.breve.utility.CommonUtils;
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
                .ok("????????? ?????? ?????????");
    }

    /*
    ????????? ?????? ?????? ??????
    1. ?????? ??????.
    2. ?????? ??????.
     */
    @PostMapping("/board/post")
    public ResponseEntity<?> boardPost(
            @RequestPart MultipartFile uploadFile,
            @RequestPart("boardRequest") BoardRequest boardRequest,
            Principal principal
    ) throws IllegalStateException, IOException {

        if (uploadFile.isEmpty()) {  //?????? ?????? ?????????
            Long boardId = boardService.saveBoardNoFile(
                    boardRequest,
                    principal.getName()
            );
            log.info("????????? ?????? ?????? !!");

            String url = "/board/" + boardId;
            HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        }

        Long boardId = boardService.saveBoardFile(
                boardRequest,
                uploadFile,
                principal.getName()
        );
        log.info("????????? ??????, ?????? ?????? ?????? !!");

        String url = "/board/" + boardId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    /*
    ????????? ????????? ???????????? ?????? ????????????.
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<?> boardDetail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Board boardEntity = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(boardEntity)) {
            return ResponseEntity.ok("?????? ???????????? ?????? ????????? ??? ????????????.");
        }

        Map<String, Object> map = new HashMap<>();
        BoardResponse board = boardService.entityToDtoDetail(boardEntity);
        String writerEmail = boardEntity.getUsers().getEmail();
        String writerNickname = boardEntity.getUsers().getNickname();

        map.put("user", principal.getName());
        map.put("writerEmail", writerEmail);
        map.put("writerNickname", writerNickname);
        map.put("body", board);

        boardService.updateView(id);
        log.info("????????? +1 ??????!!");

        return ResponseEntity.ok(map);
    }

    //== ?????? ???????????? ????????? ==//
    @GetMapping("/board/image/{saveFileName}")
    @ResponseBody
    public Resource showImage(
            @PathVariable("saveFileName") String saveFileName
    ) throws MalformedURLException {
        return new UrlResource("file:C:\\Temp\\upload\\" + saveFileName);
    }

    @PostMapping("/board/good/{id}")
    public ResponseEntity<?> boardGood(@PathVariable("id") Long id) {
        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("???????????? ???????????? ?????? ???????????? ??????????????????.");
        }

        String url = "/board/" + id;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        boardService.updateGood(id);
        log.info("????????? ?????? ??????!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/board/edit/{id}")
    public ResponseEntity<?> boardEditPage(@PathVariable("id") Long id) {
        BoardResponse board =
                boardService.entityToDtoDetail(boardService.getBoardEntity(id));

        return ResponseEntity.ok(
                Objects.requireNonNullElse(
                        board,
                        "?????? ???????????? ??????????????? ??? ????????????."
                )
        );
    }

    /*
    ???????????? ???????????? ??????
    ????????? ????????? ?????? ?????? : 1. ???????????? ??????, 2. ???????????? ??????
    ????????? ????????? ?????? ?????? : 1. ?????? ??????, 2. ???????????? ?????????
     */
    @PostMapping("/board/edit/{id}")
    public ResponseEntity<?> boardEdit(
            @PathVariable("id") Long id,
            @RequestPart MultipartFile uploadFile,
            @RequestPart("boardRequest") BoardRequest boardRequest,
            Principal principal
    ) throws IllegalStateException, IOException {

        String url = "/board/" + id;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("?????? ???????????? ?????? ????????? ??????????????????.");
        }

        if (!Objects.equals(board.getUsers().getEmail(), principal.getName())) {  //writer check
            log.info("???????????? ?????? ????????? ?????? ?????? ?????????.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        if (uploadFile.isEmpty()) {
            boardService.editBoardNoFile(
                    id,
                    boardRequest
            );
            log.info("????????? id=" + id + " ?????? ??????!!");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        }

        boardService.editBoardFile(
                id,
                uploadFile,
                boardRequest

        );
        log.info("????????? id=" + id + " ?????? ??????!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/board/delete/{id}")
    public ResponseEntity<?> boardDelete(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("?????? ???????????? ?????? ????????? ??????????????????.");
        }

        if (!Objects.equals(board.getUsers().getEmail(), principal.getName())) {
            log.info("???????????? ?????? ????????? ?????? ?????? ?????????.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        boardService.deleteBoard(id);
        log.info("????????? id=" + id + " ?????? ??????!!");

        String url = "/board/today";
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
