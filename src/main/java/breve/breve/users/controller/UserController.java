package breve.breve.users.controller;

import breve.breve.board.model.BoardResponse;
import breve.breve.board.service.BoardService;
import breve.breve.users.model.Role;
import breve.breve.users.model.UserRequest;
import breve.breve.users.model.UserResponse;
import breve.breve.users.model.Users;
import breve.breve.users.service.UserService;
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

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final BoardService boardService;

    //== 메인 페이지 ==//
    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("home");
    }

    //== 회원가입 페이지 ==//
    @GetMapping("/user/signup")
    public ResponseEntity<?> signupPage() {
        return ResponseEntity.ok("회원가입페이지");
    }

    //== 회원가입 처리 ==//
    @PostMapping("/user/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequest userRequest) {
        int checkEmail = userService.checkSameEmail(userRequest.getEmail());

        //중복이 아닐때
        if (checkEmail == 1) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/"));  //해당 경로로 리다이렉트

            userService.joinUser(userRequest);
            log.info("회원 가입 성공!!");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {
            return ResponseEntity
                    .ok("중복되는 이메일이 있어 회원가입이 불가능합니다.");
        }
    }

    //== 로그인 페이지 ==//
    @GetMapping("/user/login")
    public ResponseEntity<?> loginPage() {
        return ResponseEntity.ok("로그인 페이지");
    }

    //== 로그인 ==//
    @PostMapping("/user/login")
    public ResponseEntity<?> loginPage(
            @RequestBody UserRequest userRequest,
            HttpSession session
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));

        userService.login(userRequest, session);
        log.info("로그인 성공!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    /*
    로그아웃은 시큐리티 단에서 이루어짐.
    /user/logout 으로 post 하면 된다.
     */

    @GetMapping("/user/mypage")  //rest-api에서는 대문자를 쓰지않는다.
    public ResponseEntity<Map<String, Object>> myPage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            Principal principal
    ) {
        Map<String, Object> map = new HashMap<>();
        UserResponse dto = userService.getUserByEmail(principal.getName());
        Page<BoardResponse> board = boardService.getBoardByUser(principal.getName(), pageable);

        map.put("user", dto);
        map.put("board", board);

        return ResponseEntity.ok(map);
    }

    //== My Profile - 상대가 보는 내 프로필 ==// //닉네임으로 들고옴.


    //== 닉네임 등록 ==//
    @PostMapping("/user/nickname-post")
    public ResponseEntity<?> nicknamePost(
            @RequestBody String nickname,
            Principal principal
    ) {
        int checkNickname = userService.checkSameNickname(nickname);

        //중복이 아닐때
        if (checkNickname == 1) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/user/mypage"));

            userService.updateNickname(nickname, principal.getName());
            log.info("닉네임 수정 성공!!");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {
            return ResponseEntity
                    .ok("중복되는 닉네임이 있어 수정 불가능합니다.");
        }
    }

    //== 닉네임으로 유저 검색 ==//
    @GetMapping("/user/search")
    public ResponseEntity<List<UserResponse>> searchPage(
            @RequestParam("nickname") String nickname
    ) {
        List<UserResponse> userList = userService.getUserListByNickName(nickname);

        return ResponseEntity.ok(userList);
    }

    //== 접근 거부 페이지 ==//
    @GetMapping("/user/prohibition")
    public ResponseEntity<?> prohibition() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("접근 권한이 없습니다.");
    }

    //== 어드민 페이지 ==//
    @GetMapping("/admin")
    public ResponseEntity<?> admin(Principal principal) {
        UserResponse dto = userService.getUserByEmail(principal.getName());
        if (dto.getAuth().equals(Role.ADMIN)) {  //권한 검증
            log.info("어드민이 어드민 페이지에 접속했습니다.");
            return ResponseEntity.ok(userService.getAllUsersForAdmin());
        } else {
            log.info("어드민 페이지 접속에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
