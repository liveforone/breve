package breve.breve.follow.controller;

import breve.breve.follow.model.Follow;
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
        Follow follow = followService.getFollowDetail(principal.getName(), nickname);

        if (follow == null) {  //중복안됨
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/user/profile/" + nickname));

            followService.saveFollow(nickname, principal.getName());
            log.info("팔로잉 성공!!");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {
            return ResponseEntity.ok("이미 팔로우 되어있습니다.");
        }
    }

    @GetMapping("/follow/my-follow")
    public ResponseEntity<List<String>> myFollowList(Principal principal) {
        List<String> myFollowList = followService.getMyFollowList(principal.getName());

        return ResponseEntity.ok(myFollowList);
    }

    @GetMapping("/follow/my-follower")
    public ResponseEntity<List<String>> myFollowerList(Principal principal) {
        List<String> myFollowerList = followService.getMyFollowerList(principal.getName());

        return ResponseEntity.ok(myFollowerList);
    }

    @PostMapping("/unfollow/{nickname}")
    public ResponseEntity<?> unfollow(
            @PathVariable("nickname") String nickname,
            Principal principal
    ) {
        Follow follow = followService.getFollowDetail(principal.getName(), nickname);

        if (follow != null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/user/mypage"));

            followService.unfollow(principal.getName(), nickname);
            log.info("언팔로우 성공!!");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {
            return ResponseEntity.ok("이미 이웃이 아닙니다.");
        }
    }

    @GetMapping("/follow/profile-follow/{nickname}")
    public ResponseEntity<List<String>> profileFollow(@PathVariable("nickname") String nickname) {
        List<String> followList = followService.getProfileFollowList(nickname);

        return ResponseEntity.ok(followList);
    }

    @GetMapping("/follow/profile-follower/{nickname}")
    public ResponseEntity<List<String>> profileFollower(@PathVariable("nickname") String nickname) {
        List<String> followerList = followService.getProfileFollowerList(nickname);

        return ResponseEntity.ok(followerList);
    }
}
