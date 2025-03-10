package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.*;
import hongmumuk.hongmumuk.entity.*;
import hongmumuk.hongmumuk.repository.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "wjsalswp303@gmail.com";
    private static String randNum;
    private final RestaurantRepository restaurantRepository;

    // 랜덤 인증번호 생성
    public static void createNumber() {
        randNum = String.format("%06d", (int)(Math.random() * 1000000));
    }

    @Transactional
    public ResponseEntity<?> logIn(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }
        else {
            if(!passwordEncoder.matches(password, user.get().getPassword())){
                return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.WRONG_INFO_ERROR));
            }
        }
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 리프레쉬 토큰 객체 생성
        RefreshToken refreshToken = RefreshToken.builder()
                .id(user.get().getId().toString())
                .refreshToken(jwtToken.getRefreshToken())
                .build();

        // 리프레쉬토큰 DB 에 저장.
        RefreshToken existRefreshToken = refreshTokenRepository.findById(user.get().getId()).orElse(null);
        if(existRefreshToken == null) // 해당 회원에 대한 리프레쉬토큰이 저장된 적이 없을 때
            refreshTokenRepository.save(refreshToken);
        else { // 한번이라도 리프레쉬토큰이 저장된 적이 있을 때 -> 리프레쉬토큰 값만 업데이트.
            existRefreshToken.updateValue(jwtToken.getRefreshToken());
            refreshTokenRepository.save(existRefreshToken);
        }

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, jwtToken));
    }

    @Transactional
    public ResponseEntity<?> joinService(SignInDto signInDto) {

        // 회원이 이미 존재할 때
        Optional<User> userOptional = userRepository.findByEmail(signInDto.getEmail());
        if(userOptional.isPresent()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.USER_EXISTS));
        }

        User user = User.builder()
                .email(signInDto.getEmail())
                .password(passwordEncoder.encode(signInDto.getPassword()))
                .build();
        userRepository.save(user);

        user.setNickName("홍무묵" + user.getId());

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    @Transactional
    public ResponseEntity<?> sendService(EmailDto emailDto) throws MessagingException, MessagingException {

        createNumber(); // 랜덤 번호 생성.

        if (!emailDto.isJoin()) {
            // join == false 일 때 비밀번호 찾기 서비스

            if(!userRepository.existsByEmail(emailDto.getEmail())){
                return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
            }

            Optional<EmailCode> emailCodeOptional = emailCodeRepository.findByEmail(emailDto.getEmail());


            // 해당 이메일로 보낸 인증번호가 없을 때
            if(emailCodeOptional.isEmpty()){
                EmailCode emailCode = EmailCode.builder()
                        .email(emailDto.getEmail())
                        .code(randNum)
                        .createdAt(LocalDateTime.now())
                        .expirationTime(LocalDateTime.now().plusMinutes(5))
                        .build();

                emailCodeRepository.save(emailCode);
            }
            else{ // 있을 때
                EmailCode emailCode = emailCodeOptional.get();
                emailCode.setCode(randNum);
                emailCode.setCreatedAt(LocalDateTime.now());
                emailCode.setExpirationTime(LocalDateTime.now().plusMinutes(5));
                emailCodeRepository.save(emailCode);
            }

            //  HTML 이메일 전송을 위한 MimeMessage 사용
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(emailDto.getEmail());
            helper.setSubject("[홍무묵] 인증번호 안내");

            // 인증번호를 볼드 처리한 HTML 내용
            String body = "<p>본 메일은 <strong>[홍무묵]</strong>의 비밀번호 찾기를 위한 이메일 인증입니다.<br><strong style='font-size:18px; color:#142FB8;'>" + randNum + "</strong>" + " 를 입력하여 본인확인을 해주시기 바랍니다.</p>";

            helper.setText(body, true); // true를 설정해야 HTML 적용됨

            javaMailSender.send(message);
        }
        else {

            // 회원가입 시 이미 존재하는 회원일 때
            if(userRepository.existsByEmail(emailDto.getEmail())){
                return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.USER_EXISTS));
            }

            Optional<EmailCode> emailCodeOptional = emailCodeRepository.findByEmail(emailDto.getEmail());

            // 해당 이메일로 보낸 인증번호가 없을 때
            if(emailCodeOptional.isEmpty()){
                EmailCode emailCode = EmailCode.builder()
                        .email(emailDto.getEmail())
                        .code(randNum)
                        .createdAt(LocalDateTime.now())
                        .expirationTime(LocalDateTime.now().plusMinutes(5))
                        .build();

                emailCodeRepository.save(emailCode);
            }
            else{ // 있을 때
                EmailCode emailCode = emailCodeOptional.get();
                emailCode.setCode(randNum);
                emailCode.setCreatedAt(LocalDateTime.now());
                emailCode.setExpirationTime(LocalDateTime.now().plusMinutes(5));
                emailCodeRepository.save(emailCode);
            }

            //  HTML 이메일 전송을 위한 MimeMessage 사용
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(emailDto.getEmail());
            helper.setSubject("[홍무묵] 인증번호 안내");

            // 인증번호를 볼드 처리한 HTML 내용
            String body = "<p>본 메일은 <strong>[홍무묵]</strong>의 회원가입을 위한 이메일 인증입니다.<br><strong style='font-size:18px; color:#142FB8;'>" + randNum + "</strong>" + " 를 입력하여 본인확인을 해주시기 바랍니다.</p>";

            helper.setText(body, true); // true를 설정해야 HTML 적용됨

            javaMailSender.send(message);
        }

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }


    @Transactional
    public ResponseEntity<?> verifyService(EmailDto.VerifyDto verifyDto) throws IOException {
        Optional<EmailCode> emailCode = emailCodeRepository.findByEmail(verifyDto.getEmail());

        // 해당 이메일에 대한 인증번호가 발급되지 않았을 때
        if(emailCode.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.BAD_REQUEST));
        }

        // 인증번호가 만료되었을때
        if(emailCode.get().getExpirationTime().isBefore(LocalDateTime.now())){
            emailCodeRepository.delete(emailCode.get());
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.CODE_EXPIRED));
        }

        // 인증 성공
        if(emailCode.get().getCode().equals(verifyDto.getCode())){
            emailCodeRepository.delete(emailCode.get());
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
        }
        else{ // 인증 실패
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.WRONG_CODE_ERROR));
        }
    }

    @Transactional
    public ResponseEntity<?> reissue(String accessToken, String refreshToken){
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken))
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.WRONG_TOKEN_ERROR));

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();

        String userEmail = userDetail.getUsername();
        Optional<User> user = userRepository.findByEmail(userEmail);
        // 회원이 존재하지 않을 때, 이메일이 틀렸을 때
        if(user.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }
        Optional<RefreshToken> existRefreshToken = refreshTokenRepository.findById(user.get().getId().toString());
        if(existRefreshToken.isEmpty()) // 해당 유저의 리프레쉬 토큰이 저장이 안되어 있을 때
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.BAD_REQUEST));

        // 3. DB에 매핑 되어있는 Member ID(key)와 토큰값이 같지않으면 에러 리턴
        if(!refreshToken.equals(existRefreshToken.get().getRefreshToken()))
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.WRONG_TOKEN_ERROR));

        // 4. Vaule값이 같다면 토큰 재발급 진행
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.CREATED, jwtToken));
    }

    @Transactional
    public ResponseEntity<?> findProfile(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }

        User user = userOptional.get();

        ProfileDto profileDto = ProfileDto.builder()
                .nickName(user.getNickName())
                .email(user.getEmail())
                .build();

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, profileDto));
    }

    @Transactional
    public ResponseEntity<?> getLikedR(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }

        User user = userOptional.get();

        List<LikedRestaurant> likedRestaurants = likedRestaurantRepository.findByUser(user);

        if(likedRestaurants.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.LIKED_NOT_EXISTS));
        }

        List<Restaurant> restaurants = new java.util.ArrayList<>(List.of());

        for (LikedRestaurant likedRestaurant : likedRestaurants) {
                restaurants.add(restaurantRepository.findById(likedRestaurant.getRestaurant().getId()).orElse(null));
        }

        List<RestaurantPageDto> restaurantPageDtos = restaurants.stream()
                .map(RestaurantPageDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, restaurantPageDtos));
    }

    @Transactional
    public ResponseEntity<?> modifyPassword(String email, String newPassword){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }

        User user = userOptional.get();

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    public ResponseEntity<?> checkPassword(String email, ProfileDto.passwordDto password){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }

        User user = userOptional.get();

        if(passwordEncoder.matches(password.getPassword(), user.getPassword())){
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
        }
        else{
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.INCORRECT_PASSWORD));
        }
    }

    @Transactional
    public ResponseEntity<?> modifyNickname(String email, String nickname){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }

        User user = userOptional.get();

        user.setNickName(nickname);

        userRepository.save(user);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    @Transactional
    public ResponseEntity<?> deleteUser(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.UNKNOWN_USER_ERROR));
        }

        User user = userOptional.get();

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(user.getId());
        refreshToken.ifPresent(refreshTokenRepository::delete);

        List<LikedRestaurant> likedRestaurants = likedRestaurantRepository.findByUser(user);

        List<Restaurant> restaurants = likedRestaurants.stream().map(LikedRestaurant::getRestaurant).toList();

        // 각 레스토랑 좋아요 -1
        for (Restaurant restaurant : restaurants) {
            restaurant.setLikes(restaurant.getLikes()-1);
        }

        likedRestaurantRepository.deleteAll(likedRestaurants);

        userRepository.delete(user);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }
}
