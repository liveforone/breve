package breve.breve.users.service;

import breve.breve.users.model.Role;
import breve.breve.users.dto.UserRequest;
import breve.breve.users.dto.UserResponse;
import breve.breve.users.model.Users;
import breve.breve.users.repository.UserRepository;
import breve.breve.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private static final int DUPLICATE = 0;
    private static final int NOT_DUPLICATE = 1;
    private static final int PASSWORD_MATCH = 1;
    private static final int PASSWORD_NOT_MATCH = 0;

    //== UserResponse builder method ==//
    public UserResponse dtoBuilder(Users users) {
        return UserResponse.builder()
                .id(users.getId())
                .email(users.getEmail())
                .auth(users.getAuth())
                .nickname(users.getNickname())
                .build();
    }

    //== dto -> entity ==//
    public Users dtoToEntity(UserRequest user) {
        return Users.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .auth(user.getAuth())
                    .nickname(user.getNickname())
                    .build();
    }

    //== entity -> dto1 - detail ==//
    public UserResponse entityToDtoDetail(Users users) {

        if (CommonUtils.isNull(users)) {
            return null;
        }
        return dtoBuilder(users);
    }

    //== entity -> dto2 - list ==//
    public List<UserResponse> entityToDtoList(List<Users> usersList) {
        return usersList
                .stream()
                .map(this::dtoBuilder)
                .collect(Collectors.toList());
    }

    //== ????????? ????????? ?????? - ?????? + ?????? ==//
    public String makeRandomNickname() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    //== ????????? ?????? ?????? ==//
    @Transactional(readOnly = true)
    public int checkDuplicateEmail(String email) {
        Users users = userRepository.findByEmail(email);

        if (CommonUtils.isNull(users)) {
            return NOT_DUPLICATE;
        }
        return DUPLICATE;
    }

    //== ????????? ?????? ?????? ==//
    @Transactional(readOnly = true)
    public int checkDuplicateNickname(String nickname) {
        Users users = userRepository.findByNickname(nickname);

        if (CommonUtils.isNull(users)) {
            return NOT_DUPLICATE;
        }
        return DUPLICATE;
    }

    //== ???????????? ????????? ==//
    public int checkPasswordMatching(String inputPassword, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(inputPassword, password)) {
            return PASSWORD_MATCH;
        }
        return PASSWORD_NOT_MATCH;
    }

    //== ?????? ????????? ?????? ==//
    @Transactional(readOnly = true)
    public Users getUserEntity(String email) {
        return userRepository.findByEmail(email);
    }

    //== ?????? response ?????? ==//
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        return entityToDtoDetail(
                userRepository.findByEmail(email)
        );
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUserListByNickName(String nickname) {
        return entityToDtoList(
                userRepository.searchByNickName(nickname)
        );
    }

    //== ?????? ?????? ?????? for admin ==//
    @Transactional(readOnly = true)
    public List<Users> getAllUsersForAdmin() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByNickname(String nickname) {
        return entityToDtoDetail(
                userRepository.findByNickname(nickname)
        );
    }

    //== ?????? ?????? ?????? ==//
    @Transactional
    public void joinUser(UserRequest userRequest) {
        //???????????? ?????????
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userRequest.setPassword(passwordEncoder.encode(
                userRequest.getPassword()
        ));
        userRequest.setAuth(Role.MEMBER);  //?????? ?????? ??????
        userRequest.setNickname(makeRandomNickname());  //????????? ????????? ??????

        userRepository.save(
                dtoToEntity(userRequest)
        );
    }

    //== ????????? - ????????? ?????????????????? ?????? ==//
    @Transactional
    public void login(UserRequest userRequest, HttpSession httpSession)
            throws UsernameNotFoundException
    {
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        Users user = userRepository.findByEmail(email);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(token);
        httpSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        List<GrantedAuthority> authorities = new ArrayList<>();
        /*
        ?????? ???????????? ???????????? ???????????? ???????????? ???????????? ????????? admin ?????? ???????????????
        ??? ???????????? ???????????? ???????????? ???????????? auth ???????????? ???????????? db ???????????? ????????????,
        GrantedAuthority ??? ???????????? ?????????.
         */
        if (user.getAuth() != Role.ADMIN && ("admin@breve.com").equals(email)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
            userRepository.updateAuth(Role.ADMIN, userRequest.getEmail());
        }
        if (user.getAuth() == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        }
        authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));

        new User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    //== spring context ?????? ?????????(??????) ==//
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException
    {
        Users users = userRepository.findByEmail(email);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (users.getAuth() == Role.ADMIN) {  //????????? ????????? ?????????, ??????????????? ?????????????????????
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        }
        authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));

        return new User(
                users.getEmail(),
                users.getPassword(),
                authorities
        );
    }

    @Transactional
    public void updateNickname(String nickname, String email) {
        userRepository.updateNickname(nickname, email);
    }

    @Transactional
    public void updateEmail(String oldEmail, String newEmail) {
        userRepository.updateEmail(oldEmail, newEmail);
    }

    @Transactional
    public void updatePassword(Long id, String inputPassword) {
        //pw ?????????
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newPassword =  passwordEncoder.encode(inputPassword);
        
        userRepository.updatePassword(id, newPassword);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
