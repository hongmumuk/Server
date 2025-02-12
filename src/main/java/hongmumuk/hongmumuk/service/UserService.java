package hongmumuk.hongmumuk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univcert.api.UnivCert;
import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.EmailDto;
import hongmumuk.hongmumuk.dto.JwtToken;
import hongmumuk.hongmumuk.dto.JwtTokenProvider;
import hongmumuk.hongmumuk.dto.SignInDto;
import hongmumuk.hongmumuk.entity.User;
import hongmumuk.hongmumuk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    private final String UNIVCERT_API_KEY = "eef2480f-5db2-4d62-a2e6-d6dcb2279bbb";

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
            Map<String, Object> send = UnivCert.certify(UNIVCERT_API_KEY, emailDto.getEmail(), "홍익대학교", emailDto.getUniv_check());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode response = objectMapper.readTree(objectMapper.writeValueAsString(send));
            String successMessage = response.get("success").asText();

            if(successMessage.equals("true")) {
                return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
            }
            else {
                String errorMessage = response.get("message").asText();
                return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.BAD_REQUEST, errorMessage));
            }
        }
    }

    public ResponseEntity<?> verifyService(EmailDto.VerifyDto verifyDto) throws IOException {
        Map<String, Object> verify = UnivCert.certifyCode(UNIVCERT_API_KEY, verifyDto.getEmail(), "홍익대학교", verifyDto.getCode());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(objectMapper.writeValueAsString(verify));
        String successMessage = response.get("success").asText();

        UnivCert.clear(UNIVCERT_API_KEY);

        if(successMessage.equals("true")) {
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
        }
        else {
            String errorMessage = response.get("message").asText();
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.BAD_REQUEST, errorMessage));
        }
    }

    public void clear() throws IOException {
        UnivCert.clear(UNIVCERT_API_KEY);
    }
}
