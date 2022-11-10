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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public List<String> getMyFollowList(String email) {
        List<Follow> followList = followRepository.findByFollower(email);
        List<String> list = new ArrayList<>();

        for (Follow follow : followList) {
            list.add(follow.getUsers().getNickname());
        }

        return list;
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
}
