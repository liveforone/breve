package breve.breve.users.controller;

import breve.breve.board.dto.BoardResponse;
import breve.breve.board.service.BoardService;
import breve.breve.users.dto.UserChangeEmailRequest;
import breve.breve.users.dto.UserChangePasswordRequest;
import breve.breve.users.dto.UserRequest;
import breve.breve.users.dto.UserResponse;
import breve.breve.users.model.*;
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

    //== 로그아웃 ==//
    /*
    로그아웃은 시큐리티 단에서 이루어짐.
    url : /user/logout
    method : POST
     */

    //== 접근 거부 페이지 ==//
    @GetMapping("/user/prohibition")
    public ResponseEntity<?> prohibition() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("접근 권한이 없습니다.");
    }

    @GetMapping("/user/mypage")  //rest-api에서는 대문자를 쓰지않는다.
    public ResponseEntity<?> myPage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            Principal principal
    ) {
        UserResponse users = userService.getUserByEmail(principal.getName());

        if (users != null) {
            Map<String, Object> map = new HashMap<>();
            Page<BoardResponse> board = boardService.getBoardByUser(principal.getName(), pageable);

            map.put("users", users);
            map.put("board", board);

            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.ok("해당 유저가 없어 조회할 수 없습니다.");
        }
    }

    //== Profile - 상대가 보는 내 프로필 ==// 닉네임으로 들고옴.
    @GetMapping("/user/profile/{nickname}")
    public ResponseEntity<?> ProfilePage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @PathVariable("nickname") String nickname
    ) {
        UserResponse users = userService.getUserByNickname(nickname);

        if (users != null) {
            Map<String, Object> map = new HashMap<>();
            Page<BoardResponse> board = boardService.getBoardByNickname(nickname, pageable);

            map.put("users", users);
            map.put("board", board);

            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.ok("해당 유저가 없어 조회할 수 없습니다.");
        }
    }

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

    //== 이메일 변경 ==//
    @PostMapping("/user/change-email")
    public ResponseEntity<?> changeEmail(
            @RequestBody UserChangeEmailRequest userRequest,
            Principal principal
    ) {
        Users users = userService.getUserEntity(principal.getName());
        UserResponse changeEmail = userService.getUserByEmail(userRequest.getEmail());

        if (users != null) {
            int checkPassword = userService.passwordDecode(userRequest.getPassword(), users.getPassword());

            if (changeEmail != null) {  //이메일 중복안됨

                if (checkPassword == 1) {  //pw 일치함
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(URI.create("/user/logout"));

                    userService.updateEmail(principal.getName(), userRequest.getEmail());
                    log.info("이메일 변경 성공!!");

                    return ResponseEntity
                            .status(HttpStatus.MOVED_PERMANENTLY)
                            .headers(httpHeaders)
                            .build();
                } else {  //pw 일치하지 않음
                    log.info("비밀번호 일치하지 않음.");
                    return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
                }

            } else {  //이메일 중복됨
                return ResponseEntity.ok("해당 이메일이 이미 존재합니다. 다시 입력해주세요");
            }

        } else {
            return ResponseEntity.ok("해당 유저를 조회할 수 없어 이메일 변경이 불가능합니다.");
        }
    }

    //== 비밀번호 변경 ==//
    @PostMapping("user/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody UserChangePasswordRequest userRequest,
            Principal principal
            ) {
        Users users = userService.getUserEntity(principal.getName());

        if (users != null) {
            int checkPassword = userService.passwordDecode(userRequest.getOldPassword(), users.getPassword());

            if (checkPassword == 1) {  //pw 일치함
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setLocation(URI.create("/user/logout"));

                userService.updatePassword(userRequest.getOldPassword(), userRequest.getNewPassword());
                log.info("비밀번호 변경 성공!!");

                return ResponseEntity
                        .status(HttpStatus.MOVED_PERMANENTLY)
                        .headers(httpHeaders)
                        .build();
            } else {  //pw 일치하지 않음
                log.info("비밀번호 일치하지 않음.");
                return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
            }

        } else {
            return ResponseEntity.ok("해당 유저를 조회할 수 없어 비밀번호 변경이 불가능합니다.");
        }
    }

    //== 회원 탈퇴 ==//
    @PostMapping("/user/withdraw")
    public ResponseEntity<?> userWithdraw(
            @RequestBody String password,
            Principal principal
    ) {
        Users users = userService.getUserEntity(principal.getName());

        if (users != null) {
            int checkPassword = userService.passwordDecode(password, users.getPassword());

            if (checkPassword == 1) { //pw 일치함
                log.info("회원 : " + users.getId() + " 탈퇴 성공!!");
                userService.deleteUser(users.getId());

                return ResponseEntity.ok("그동안 서비스를 이용해주셔서 감사합니다.");
            } else {  //pw 일치하지 않음
                log.info("비밀번호 일치하지 않음.");
                return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
            }

        } else {
            return ResponseEntity.ok("해당 유저를 조회할 수 없어 탈퇴가 불가능합니다.");
        }
    }
}
