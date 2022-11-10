package breve.breve.follow.controller;

import breve.breve.follow.service.FollowService;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{nickname}")
    public ResponseEntity<?> follow(
            @PathVariable("nickname") String nickname,
            Principal principal
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/user/profile/" + nickname));

        followService.saveFollow(nickname, principal.getName());
        log.info("팔로잉 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/follow/my-follow")
    public ResponseEntity<List<String>> myFollowList(
            Principal principal
    ) {
        List<String> myFollowList = followService.getMyFollowList(principal.getName());

        return ResponseEntity.ok(myFollowList);
    }

    //나의 팔로우 리스트
    //나의 팔로워 리스트
    //언팔로우
    //작가 팔로우 리스트
    //작가 팔로워 리스트
}
