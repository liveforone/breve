package breve.breve.follow.service;

import breve.breve.follow.model.Follow;
import breve.breve.follow.repository.FollowRepository;
import breve.breve.users.model.Users;
import breve.breve.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    //== 내가 팔로우하는 사람들 ==//
    public List<String> getMyFollowList(String email) {
        List<Follow> followList = followRepository.findByFollower(email);
        return followList.stream().map(follow -> follow.getUsers().getNickname()).collect(Collectors.toList());
    }

    //== 나를 팔로우하는 사람들 ==//
    public List<String> getMyFollowerList(String email) {
        List<Follow> followerList = followRepository.findByUsers(email);
        return followerList.stream().map(follow -> follow.getFollower().getNickname()).collect(Collectors.toList());
    }

    //== 프로필 - 프로필 주인이 팔로우하는 사람들 ==//
    public List<String> getProfileFollowList(String nickname) {
        List<Follow> followList = followRepository.findByFollowerNickname(nickname);
        return followList.stream().map(follow -> follow.getUsers().getNickname()).collect(Collectors.toList());
    }

    //== 프로필 - 프로필 주인을 팔로우하는 사람들 ==//
    public List<String> getProfileFollowerList(String nickname) {
        List<Follow> followerList = followRepository.findByUsersNickname(nickname);
        return followerList.stream().map(follow -> follow.getFollower().getNickname()).collect(Collectors.toList());
    }

    //== follow detail ==//
    public Follow getFollowDetail(String followerEmail, String userNickname) {
        Users me = userRepository.findByEmail(followerEmail);  //나
        Users myFollow = userRepository.findByNickname(userNickname);  //나의 팔로잉, 팔로잉 당하는 사람

        return followRepository.findOneFollow(
                me,
                myFollow
        );
    }

    @Transactional
    public void saveFollow(String users, String follower) {
        Users user = userRepository.findByNickname(users);
        Users user_follower = userRepository.findByEmail(follower);

        Follow follow = Follow.builder()
                .follower(user_follower)
                .users(user)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(String follower, String users) {
        Users user_me = userRepository.findByEmail(follower);  //나
        Users user_follow = userRepository.findByNickname(users);  //내가 팔로우하는 사람

        Follow follow = followRepository.findOneFollow(
                user_me,
                user_follow
        );
        followRepository.deleteById(follow.getId());
    }
}
