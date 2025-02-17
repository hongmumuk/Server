package hongmumuk.hongmumuk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.EmailDto;
import hongmumuk.hongmumuk.dto.JwtToken;
import hongmumuk.hongmumuk.dto.JwtTokenProvider;
import hongmumuk.hongmumuk.dto.SignInDto;
import hongmumuk.hongmumuk.entity.CustomUserDetail;
import hongmumuk.hongmumuk.entity.EmailCode;
import hongmumuk.hongmumuk.entity.RefreshToken;
import hongmumuk.hongmumuk.entity.User;
import hongmumuk.hongmumuk.repository.EmailCodeRepository;
import hongmumuk.hongmumuk.repository.RefreshTokenRepository;
import hongmumuk.hongmumuk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Ref;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "wjsalswp303@gmail.com";
    private static String randNum;

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
        User user = User.builder()
                .email(signInDto.getEmail())
                .password(passwordEncoder.encode(signInDto.getPassword()))
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    @Transactional
    public ResponseEntity<?> sendService(EmailDto emailDto) throws IOException {
        if(userRepository.existsByEmail(emailDto.getEmail())) {
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.USER_EXISTS));
        }
        else {
            createNumber();

            EmailCode emailCode = EmailCode.builder()
                    .email(emailDto.getEmail())
                    .code(randNum)
                    .createdAt(LocalDateTime.now())
                    .expirationTime(LocalDateTime.now().plusMinutes(5))
                    .build();

            emailCodeRepository.save(emailCode);

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(senderEmail);
            message.setTo(emailDto.getEmail());
            message.setSubject("홍무묵 이메일 인증");
            String body = "";
            body += "요청하신 인증 번호입니다.";
            body += " " + randNum + " ";
            body += " " + "해당 인증번호를 입력해주세요.";
            message.setText(body);

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

}
